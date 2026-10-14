package com.arquitecturajava.servicios;

public class ImparticionNoEncontradaException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ImparticionNoEncontradaException(Long cursoId, Long id) {
		super("No existe la impartición con id " + id + " en el curso con id " + cursoId);
	}

}
