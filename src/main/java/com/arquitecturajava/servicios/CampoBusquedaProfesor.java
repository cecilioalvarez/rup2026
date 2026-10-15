package com.arquitecturajava.servicios;

public enum CampoBusquedaProfesor {

	NOMBRE("Nombre"), APELLIDOS("Apellidos");

	private final String etiqueta;

	CampoBusquedaProfesor(String etiqueta) {
		this.etiqueta = etiqueta;
	}

	public String getEtiqueta() {
		return etiqueta;
	}

}
