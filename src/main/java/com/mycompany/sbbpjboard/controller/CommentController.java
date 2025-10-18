package com.mycompany.sbbpjboard.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mycompany.sbbpjboard.dto.CommentDto;
import com.mycompany.sbbpjboard.entity.Board;
import com.mycompany.sbbpjboard.entity.Comment;
import com.mycompany.sbbpjboard.entity.Member;
import com.mycompany.sbbpjboard.repository.BoardRepository;
import com.mycompany.sbbpjboard.repository.CommentRepository;
import com.mycompany.sbbpjboard.repository.MemberRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/comments")
public class CommentController {
	
	@Autowired
	private CommentRepository commentRepository;
	
	@Autowired
	private MemberRepository memberRepository ;
	
	@Autowired
	private BoardRepository boardRepository;
	
	// 댓글 작성 (게시글의 id 필요)
	@PostMapping("/{boardId}")
	public ResponseEntity<?> writeComment(
			@PathVariable("boardId") Long boardId, 
			@Valid @RequestBody CommentDto commentDto, 
			BindingResult bindingResult, 
			Authentication auth) {
		
		// Spring Validation 결과 처리
		if(bindingResult.hasErrors()) {
			Map<String, String> errors = new HashMap<>();
			bindingResult.getFieldErrors().forEach(
				err -> {
					errors.put(err.getField(), err.getDefaultMessage());					
				}
			);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
		}
		
		// boardId로 받은 게시글의 존재 여부 확인
		Optional<Board> optional = boardRepository.findById(boardId);
		if (optional.isEmpty()) {
			Map<String, String> error = new HashMap<>();
			error.put("boardError", "해당 게시글이 존재하지 않습니다.");
			return ResponseEntity.status(404).body(error); // 404 Not Found
		}
		
		// 로그인한 유저의 Member 객체 가져오기 + comment entity 선언 후 값 넣어주기
		Member member = memberRepository.findByUsername(auth.getName()).orElseThrow();
		Comment comment = new Comment();
		comment.setBoard(optional.get());
		comment.setAuthor(member);
		comment.setContent(commentDto.getContent());
		commentRepository.save(comment); // 작성된 comment 엔티티를 db에 삽입		
		return ResponseEntity.ok(comment); // 200 응답과 엔티티 반환
	}
	
	
	// 댓글 가져오기 (댓글이 달린 게시글의 id 필요)
	@GetMapping("/{boardId}")
	public ResponseEntity<?> getComments(@PathVariable("boardId") Long boardId) {
		// 원 게시글의 존재 여부 확인
		Optional<Board> optional = boardRepository.findById(boardId);
		if (optional.isEmpty()) {
			return ResponseEntity.badRequest().body("해당 게시글이 존재하지 않습니다.");
		}
		
		List<Comment> comments  = commentRepository.findByBoard(optional.get());		
		return ResponseEntity.ok(comments);
	}
	
	// 댓글 수정 (댓글의 기본키 필요)
	@PutMapping("/{commentId}")
	public ResponseEntity<?> updateComment(
			@PathVariable("commentId") Long commentId,
			@RequestBody CommentDto commentDto,
			Authentication auth) {
		
		// 수정할 댓글 찾아오기
		Comment comment = commentRepository.findById(commentId).orElseThrow();
		// 권한 확인 (로그인한 유저 = 댓글 쓴 유저)
		if (!comment.getAuthor().getUsername().equals(auth.getName())) {
			return ResponseEntity.status(403).body("수정 권한이 없습니다.");
		}
		// 수정한 댓글 기존 댓글 엔티티에 setter 이용해서 넣어주기
		comment.setContent(commentDto.getContent());
		commentRepository.save(comment);
		return ResponseEntity.ok(comment); // 수정 완료 후 수정된 댓글 객체 반환
	}
	
	// 댓글 삭제
	@DeleteMapping("/{commentId}")
	public ResponseEntity<?> deleteComment(
			@PathVariable("commentId") Long commentId,
			Authentication auth) {
		// 댓글 존재 여부 확인 및 변수로 선언
		Optional<Comment> optional = commentRepository.findById(commentId);
		if (optional.isEmpty()) {
			return ResponseEntity.status(404).body("삭제할 댓글이 존재하지 않습니다."); // 404 Not Found
		}
		// 권한 확인
		if (!optional.get().getAuthor().getUsername().equals(auth.getName()) ) {
			return ResponseEntity.status(403).body("삭제 권한이 없습니다."); // 403 Forbidden 권한 없음
		}
		commentRepository.delete(optional.get());
		return ResponseEntity.ok("댓글 삭제 성공!");
	}
	

}
