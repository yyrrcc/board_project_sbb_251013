package com.mycompany.sbbpjboard.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mycompany.sbbpjboard.dto.MemberDto;
import com.mycompany.sbbpjboard.entity.Member;
import com.mycompany.sbbpjboard.repository.MemberRepository;

import jakarta.validation.Valid;

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
	// ** 유효성 추가!
	@PostMapping("/signup")
	public ResponseEntity<?> signup(@Valid @RequestBody MemberDto memberDto, BindingResult result) {
		// ** Spring Validation 결과 처리
		if(result.hasErrors()) { //참이면 유효성 체크 실패->err
			Map<String, String> errors = new HashMap<>();
			result.getFieldErrors().forEach(
				err -> {
					errors.put(err.getField(), err.getDefaultMessage());
				}
			);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
		}
		// ** Entity 객체 선언 후 사용자가 입력한 값을 entity 객체에 setter 이용해서 저장
		Member member = new Member();
		member.setUsername(memberDto.getUsername());
		member.setPassword(memberDto.getPassword());
				
		if(memberRepository.findByUsername(memberDto.getUsername()).isPresent()) {
			Map<String, String> error = new HashMap<>();
			error.put("iderror", "이미 존재하는 아이디 입니다.");
			return ResponseEntity.badRequest().body(error);
		}
		member.setPassword(passwordEncoder.encode(member.getPassword()));
			memberRepository.save(member);
			// return ResponseEntity.ok(req); 가입 성공 후 해당 Entity 반환할 수도 있음
			return ResponseEntity.ok("회원가입 성공");
	}

	// 로그인 (관례적으로 me라고 사용함)
	@GetMapping("/me") // 현재 로그인한(=나 자신) 사용자 정보를 가져오기 (Principal, Session 같은 느낌)
	public ResponseEntity<?> me(Authentication auth) {
		return ResponseEntity.ok(Map.of("username", auth.getName())); // username으로 로그인한 사용자 정보를 넣어줌
		}
}
