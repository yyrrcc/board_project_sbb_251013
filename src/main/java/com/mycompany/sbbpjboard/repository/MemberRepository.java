package com.mycompany.sbbpjboard.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mycompany.sbbpjboard.entity.Member;
import java.util.Optional;


public interface MemberRepository extends JpaRepository<Member, Long>{
	
	// 유저 아이디(username)로 찾기
	public Optional<Member> findByUsername(String username);

}
