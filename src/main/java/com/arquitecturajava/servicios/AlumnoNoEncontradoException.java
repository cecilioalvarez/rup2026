package com.arquitecturajava.servicios;

public class AlumnoNoEncontradoException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public AlumnoNoEncontradoException(Long id) {
		super("No existe el alumno con id " + id);
	}

}
