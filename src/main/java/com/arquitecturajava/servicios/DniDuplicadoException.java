package com.arquitecturajava.servicios;

public class DniDuplicadoException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	// "persona" indica a quién pertenece el DNI repetido: profesor, alumno...
	public DniDuplicadoException(String persona, String dni) {
		super("Ya existe un " + persona + " con el DNI " + dni);
	}

}
