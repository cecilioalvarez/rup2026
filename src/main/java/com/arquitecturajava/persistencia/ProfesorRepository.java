package com.arquitecturajava.persistencia;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.arquitecturajava.negocio.Profesor;

public interface ProfesorRepository extends JpaRepository<Profesor, Long> {

	List<Profesor> findAllByOrderByApellidosAscNombreAsc();

	boolean existsByDniIgnoreCase(String dni);

	boolean existsByDniIgnoreCaseAndIdNot(String dni, Long id);

}
