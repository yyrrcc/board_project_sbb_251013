package com.mycompany.sbbpjboard.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mycompany.sbbpjboard.entity.Member;
import com.mycompany.sbbpjboard.repository.MemberRepository;

@RestController
@RequestMapping("/api/auth")
public class AuthController { // 인증 전용 컨트롤러
	
	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	// 회원가입 (동일 username 있는지 확인 -> 비밀번호 암호화 -> 가입 성공)
	// 리액트로 응답을 보내주기 위해서 ResponseEntity 사용 badRequest().body(), ok()
	// ResponseEntity<?> 통해서 타입을 지정 안 해줄수도 있음 / 다양하게 만들 수 있음! (boolean 이용)
	@PostMapping("/signup")
	public ResponseEntity<?> signup(@RequestBody Member req) {
		if(memberRepository.findByUsername(req.getUsername()).isPresent()) {
			return ResponseEntity.badRequest().body("이미 존재하는 아이디 입니다.");
		}
			req.setPassword(passwordEncoder.encode(req.getPassword()));
			memberRepository.save(req);
			// return ResponseEntity.ok(req); 가입 성공 후 해당 Entity 반환할 수도 있음
			return ResponseEntity.ok("회원가입 성공");
	}

	// 로그인 (관례적으로 me라고 사용함)
	@GetMapping("/me") // 현재 로그인한(=나 자신) 사용자 정보를 가져오기 (Principal, Session 같은 느낌)
	public ResponseEntity<?> me(Authentication auth) {
		return ResponseEntity.ok(Map.of("username", auth.getName())); // username으로 로그인한 사용자 정보를 넣어줌
		}
}
