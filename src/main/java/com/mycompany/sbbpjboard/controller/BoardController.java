package com.mycompany.sbbpjboard.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mycompany.sbbpjboard.entity.Board;
import com.mycompany.sbbpjboard.entity.Member;
import com.mycompany.sbbpjboard.repository.BoardRepository;
import com.mycompany.sbbpjboard.repository.MemberRepository;

@RestController
@RequestMapping("/api/board")
public class BoardController {
	
	@Autowired
	private BoardRepository boardRepository;
	@Autowired
	private MemberRepository memberRepository;
	
	// 전체 게시글 조회(Read-get)
	@GetMapping
	public List<Board> listBoard() {
		return boardRepository.findAll();
	}
	
	// 글 작성(Create-post)
	@PostMapping
	public ResponseEntity<?> createBoard(@RequestBody Board req, Authentication auth) {
		// auth.getName() : 로그인한 username
		Member member = memberRepository.findByUsername(auth.getName()).orElseThrow(
				() -> new UsernameNotFoundException("사용자 없음"));
		// Board 객체를 새로 만들어서 setter 이용해서 값을 넣어준 후 저장(save)
		Board board = new Board();
		board.setTitle(req.getTitle());
		board.setContent(req.getContent());
		board.setAuthor(member);
		boardRepository.save(board);
		return ResponseEntity.ok("글 작성 성공");
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

