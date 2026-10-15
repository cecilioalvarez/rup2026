package com.arquitecturajava.persistencia;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.arquitecturajava.negocio.Profesor;

public interface ProfesorRepository extends JpaRepository<Profesor, Long> {

	List<Profesor> findAllByOrderByApellidosAscNombreAsc();

	List<Profesor> findByNombreContainingIgnoreCaseOrderByApellidosAscNombreAsc(String nombre);

	List<Profesor> findByApellidosContainingIgnoreCaseOrderByApellidosAscNombreAsc(String apellidos);

	boolean existsByDniIgnoreCase(String dni);

	boolean existsByDniIgnoreCaseAndIdNot(String dni, Long id);

}
