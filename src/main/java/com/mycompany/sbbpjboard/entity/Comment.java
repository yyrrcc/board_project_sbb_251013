package com.mycompany.sbbpjboard.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Comment {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false, length = 500)
	private String content; // 댓글 내용
	
	@CreationTimestamp
	private LocalDateTime createdAt; // 댓글 입력 날짜 시간
	
	// 댓글 : 사용자 = n : 1
	@ManyToOne(fetch = FetchType.LAZY) // @ManyToOne에서 항상 명시(지연 로딩, 필요할 때만 데이터 가져오게 해주는 것)
	@JoinColumn(name = "author_id") // join되는 테이블의 외래키 이름 설정(명시 해주는 게 좋긴 함)
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // JPA가 내부적으로 붙이는 프록시용 필드를 JSON에서 무시(직렬회 에러 방지)
	private Member author;
	
	// 댓글 : 게시판 = n : 1
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "board_id") // join되는 테이블의 외래키 이름 설정
	@JsonIgnore // 그 필드를 JSON 응답에서 아예 빼버림(Comment 쪽의 board를 숨겨 순환을 끊음)
	private Board board;

}
