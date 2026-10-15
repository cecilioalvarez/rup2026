package com.arquitecturajava.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.arquitecturajava.negocio.Imparticion;

public interface ImparticionRepository extends JpaRepository<Imparticion, Long> {

	// el listado muestra el profesor: se carga en la misma consulta para evitar N+1
	@EntityGraph(attributePaths = "profesor")
	List<Imparticion> findByCursoIdOrderByFechaInicioAsc(Long cursoId);

	Optional<Imparticion> findByIdAndCursoId(Long id, Long cursoId);

}
