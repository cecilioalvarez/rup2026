package com.arquitecturajava.persistencia;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.arquitecturajava.negocio.Alumno;

public interface AlumnoRepository extends JpaRepository<Alumno, Long> {

	List<Alumno> findAllByOrderByApellidosAscNombreAsc();

	boolean existsByDniIgnoreCase(String dni);

	boolean existsByDniIgnoreCaseAndIdNot(String dni, Long id);

}
