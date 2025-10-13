package com.mycompany.sbbpjboard.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Board {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String title;
	private String content;
	
	@CreationTimestamp // 자동으로 현재 날짜와 시간 삽입. 수정 할 땐 @UpdateTimestamp 사용
	private LocalDateTime createdAt;

	@ManyToOne // 게시판글:게시판글쓴이 = n:1
	private Member author;
}
