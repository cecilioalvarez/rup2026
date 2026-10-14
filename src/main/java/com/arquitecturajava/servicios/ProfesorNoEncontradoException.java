package com.arquitecturajava.servicios;

public class ProfesorNoEncontradoException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ProfesorNoEncontradoException(Long id) {
		super("No existe el profesor con id " + id);
	}

}
