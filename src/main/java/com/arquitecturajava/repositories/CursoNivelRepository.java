package com.arquitecturajava.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.arquitecturajava.negocio.CursoNivel;

public interface CursoNivelRepository extends JpaRepository<CursoNivel, Long> {

	List<CursoNivel> findAllByOrderByOrdenAsc();

}
