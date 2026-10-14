package com.arquitecturajava.persistencia;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.arquitecturajava.negocio.Imparticion;

public interface ImparticionRepository extends JpaRepository<Imparticion, Long> {

	List<Imparticion> findByCursoIdOrderByFechaInicioAsc(Long cursoId);

	Optional<Imparticion> findByIdAndCursoId(Long id, Long cursoId);

}
