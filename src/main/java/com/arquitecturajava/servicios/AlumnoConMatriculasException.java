package com.arquitecturajava.servicios;

public class AlumnoConMatriculasException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public AlumnoConMatriculasException(String nombreCompleto) {
		super("No se puede borrar a " + nombreCompleto + " porque tiene matrículas");
	}

}
