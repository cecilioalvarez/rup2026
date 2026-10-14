package com.arquitecturajava.viewmodels;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ProfesorViewModel(
		Long id,
		@NotBlank(message = "El DNI es obligatorio")
		@Pattern(regexp = "\\d{8}[A-Za-z]", message = "El DNI debe tener 8 números y una letra") String dni,
		@NotBlank(message = "El nombre es obligatorio") String nombre,
		@NotBlank(message = "Los apellidos son obligatorios") String apellidos) {

	public static ProfesorViewModel vacio() {
		return new ProfesorViewModel(null, null, null, null);
	}

	public ProfesorViewModel conId(Long nuevoId) {
		return new ProfesorViewModel(nuevoId, dni, nombre, apellidos);
	}

}
