package com.mycompany.sbbpjboard.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mycompany.sbbpjboard.entity.Board;

public interface BoardRepository extends JpaRepository<Board, Long>{

}
