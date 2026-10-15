package com.arquitecturajava.viewmodels;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record AlumnoViewModel(
		Long id,
		@NotBlank(message = "El DNI es obligatorio")
		@Pattern(regexp = "\\d{8}[A-Za-z]", message = "El DNI debe tener 8 números y una letra") String dni,
		@NotBlank(message = "El nombre es obligatorio") String nombre,
		@NotBlank(message = "Los apellidos son obligatorios") String apellidos,
		@NotBlank(message = "El email es obligatorio")
		@Email(message = "El email no es válido") String email) {

	public static AlumnoViewModel vacio() {
		return new AlumnoViewModel(null, null, null, null, null);
	}

	public AlumnoViewModel conId(Long nuevoId) {
		return new AlumnoViewModel(nuevoId, dni, nombre, apellidos, email);
	}

}
