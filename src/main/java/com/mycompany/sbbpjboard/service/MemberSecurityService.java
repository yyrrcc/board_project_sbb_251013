package com.mycompany.sbbpjboard.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.mycompany.sbbpjboard.entity.Member;
import com.mycompany.sbbpjboard.repository.MemberRepository;

@Service
//시큐리티가 로그인 시 사용할 서비스 (UserDetailsService 인터페이스 구현할 것)
public class MemberSecurityService implements UserDetailsService{
	
	@Autowired
	private MemberRepository memberRepository;

	@Override
	// 유저에게 받은 username과 (암호화된) password를 조회해서 권한을 준다!
	// build()에서 UserDetails 객체 생성 반환해줌
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Member member = memberRepository.findByUsername(username).orElseThrow(
				() -> new UsernameNotFoundException("사용자 없음"));
		return org.springframework.security.core.userdetails.User
				.withUsername(member.getUsername())
				.password(member.getPassword())
				.authorities("USER")
				.build();
	}

}
