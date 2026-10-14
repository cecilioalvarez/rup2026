package com.arquitecturajava.servicios;

public class CursoNoEncontradoException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public CursoNoEncontradoException(Long id) {
		super("No existe el curso con id " + id);
	}

}
