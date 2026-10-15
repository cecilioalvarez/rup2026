package com.arquitecturajava.servicios;

public class AlumnoYaMatriculadoException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public AlumnoYaMatriculadoException(String nombreCompleto) {
		super(nombreCompleto + " ya está matriculado en esta impartición");
	}

}
