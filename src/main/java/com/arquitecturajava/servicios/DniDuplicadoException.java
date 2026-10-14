package com.arquitecturajava.servicios;

public class DniDuplicadoException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public DniDuplicadoException(String dni) {
		super("Ya existe un profesor con el DNI " + dni);
	}

}
