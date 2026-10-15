package com.arquitecturajava.servicios;

public class MatriculaNoEncontradaException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public MatriculaNoEncontradaException(Long imparticionId, Long id) {
		super("No existe la matrícula con id " + id + " en la impartición con id " + imparticionId);
	}

}
