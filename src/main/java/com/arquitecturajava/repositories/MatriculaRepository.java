package com.arquitecturajava.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.arquitecturajava.negocio.Matricula;

public interface MatriculaRepository extends JpaRepository<Matricula, Long> {

	// el listado de una impartición muestra el alumno: se carga en la misma consulta para evitar N+1
	@EntityGraph(attributePaths = "alumno")
	List<Matricula> findByImparticionIdOrderByAlumnoApellidosAscAlumnoNombreAsc(Long imparticionId);

	// el listado de un alumno muestra la impartición y su curso
	@EntityGraph(attributePaths = { "imparticion", "imparticion.curso" })
	List<Matricula> findByAlumnoIdOrderByFechaDesc(Long alumnoId);

	Optional<Matricula> findByIdAndImparticionId(Long id, Long imparticionId);

	boolean existsByAlumnoIdAndImparticionId(Long alumnoId, Long imparticionId);

}
