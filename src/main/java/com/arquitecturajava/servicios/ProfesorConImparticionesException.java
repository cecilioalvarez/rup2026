package com.arquitecturajava.servicios;

public class ProfesorConImparticionesException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ProfesorConImparticionesException(String nombreCompleto) {
		super("No se puede borrar a " + nombreCompleto + " porque tiene imparticiones asignadas");
	}

}
