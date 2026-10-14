package com.arquitecturajava.persistencia;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.arquitecturajava.negocio.Curso;

public interface CursoRepository extends JpaRepository<Curso, Long> {

	List<Curso> findByTituloStartingWithIgnoreCase(String titulo);

	List<Curso> findByAutorStartingWithIgnoreCase(String autor);

	List<Curso> findByNivelStartingWithIgnoreCase(String nivel);

}
