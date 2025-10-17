package com.mycompany.sbbpjboard.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mycompany.sbbpjboard.dto.BoardDto;
import com.mycompany.sbbpjboard.entity.Board;
import com.mycompany.sbbpjboard.entity.Member;
import com.mycompany.sbbpjboard.repository.BoardRepository;
import com.mycompany.sbbpjboard.repository.MemberRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/board")
public class BoardController {
	
	@Autowired
	private BoardRepository boardRepository;
	@Autowired
	private MemberRepository memberRepository;
	
//	// 전체 게시글 조회(Read-get)
//	@GetMapping
//	public List<Board> listBoard() {
//		return boardRepository.findAll();
//	}
	
	// *** 전체 게시글 조회 + 페이징 처리
	@GetMapping
	public ResponseEntity<?> pagingList(@RequestParam(name = "page", defaultValue = "0") int page,
						@RequestParam(name ="size", defaultValue = "10") int size) {
		//page->사용자가 요청한 페이지의 번호, size->한 페이지당 보여질 글의 갯수
		
		if (page < 0) {
			page = 0;
		}
		
		if (size <= 0) {
			size = 10;
		}
		
		//Pageable 객체 생성->findAll에서 사용
		Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
		Page<Board> boardPage = boardRepository.findAll(pageable); //DB에서 페이징된 게시글만 조회
		//boardPage가 포함하는 정보->
		//1. 해당 페이지 글 리스트->boardPage.getContent()
		//2. 현재 페이지 번호->boardPage.getNumber()
		//3. 전체 페이지 수->boardPage.getTotalPages()
		//4. 전체 게시글 수-> boardPage.getTotalElements()
		
		Map<String, Object> pagingResponse = new HashMap<>();
		pagingResponse.put("posts", boardPage.getContent()); //페이징된 현재 페이지에 해당하는 게시글 리스트 10개
		pagingResponse.put("currentPage", boardPage.getNumber()); //현재 페이지 번호
		pagingResponse.put("totalPages", boardPage.getTotalPages()); //모든 페이지의 수
		pagingResponse.put("totalItems", boardPage.getTotalElements()); //게시판에 올라와 있는 모든 글 수(Long)
		//{"currentPage":3, totalPages:57}
		//System.out.println("총 글의 갯수:" + boardPage.getTotalElements());
		
		return ResponseEntity.ok(pagingResponse);
	}
	
	// 글 작성(Create-post)
	// ** 유효성 검사 추가
	@PostMapping
	public ResponseEntity<?> createBoard(@Valid @RequestBody BoardDto boardDto, BindingResult result, Authentication auth) {
		// ** 사용자의 로그인 여부 확인
		if (auth == null) {
			return ResponseEntity.status(401).body("로그인 후 글쓰기 가능합니다.");
		}
		// ** Spring Validation 결과 처리
		if(result.hasErrors()) { //참이면 유효성 체크 실패->error 발생
			Map<String, String> errors = new HashMap<>();
			result.getFieldErrors().forEach(
				err -> {
					errors.put(err.getField(), err.getDefaultMessage());					
				}
			);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
		}
		
		// auth.getName() : 로그인한 username
		Member member = memberRepository.findByUsername(auth.getName()).orElseThrow(
				() -> new UsernameNotFoundException("사용자 없음"));
		// Board 객체를 새로 만들어서 setter 이용해서 값을 넣어준 후 저장(save)
		Board board = new Board();
		board.setTitle(boardDto.getTitle());
		board.setContent(boardDto.getContent());
		board.setAuthor(member);
		boardRepository.save(board);
		return ResponseEntity.ok(board);
	}
	
	// id 이용해서 특정 글 상세보기
	@GetMapping("/{id}")
	public ResponseEntity<?> getBoardById(@PathVariable("id") Long id) {
		Optional<Board> optional = boardRepository.findById(id);
		if (optional.isPresent()) {
			return ResponseEntity.ok(optional.get()); // 해당 글 반환
		} else {
			return ResponseEntity.status(404).body("해당 글은 존재하지 않습니다.");
		}
	}
	
	// id 이용해서 특정 글 수정하기(Update-put)
	@PutMapping("/{id}")
	public ResponseEntity<?> updateBoard(@PathVariable("id") Long id, Authentication auth, @RequestBody Board updatedBoard) {
		// id를 이용해서 특정 글 여부 확인
		Optional<Board> optional = boardRepository.findById(id);
		if (optional.isEmpty()) {
			return ResponseEntity.status(404).body("해당 글은 존재하지 않습니다.");
		}
		// 글 존재함! 권한 여부 확인
		if (auth == null || !optional.get().getAuthor().getUsername().equals(auth.getName())) {
			return ResponseEntity.status(403).body("해당 글의 수정 권한이 없습니다.");
		}
			Board board = optional.get();
			board.setTitle(updatedBoard.getTitle());
			board.setContent(updatedBoard.getContent());
			boardRepository.save(board);
			return ResponseEntity.ok(board);
	}
	
	// id 이용해서 특정 글 삭제하기 (로그인 여부에 따라)
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteBoardById(@PathVariable("id") Long id, Authentication auth) {
		Optional<Board> optional = boardRepository.findById(id);
		if (optional.isEmpty()) {
			return ResponseEntity.status(404).body("해당 글은 존재하지 않습니다.");
		} 
		// 비회원 || id로 찾은 글의 글쓴이가 로그인한 유저와 동일하지 않은 경우
		if (auth == null || !optional.get().getAuthor().getUsername().equals(auth.getName())) {
			return ResponseEntity.status(403).body("해당 글의 삭제 권한이 없습니다.");
		}
		boardRepository.deleteById(id);
		return ResponseEntity.ok("글 삭제 성공");
	}
	
	
}

