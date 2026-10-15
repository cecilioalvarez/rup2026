package com.arquitecturajava.viewmodels;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// idProfesor llega del formulario; profesor (nombre completo) solo se usa para mostrarlo en el listado
public record ImparticionViewModel(
		Long id,
		@NotBlank(message = "El nombre es obligatorio") String nombre,
		@NotNull(message = "La fecha de inicio es obligatoria") @DateTimeFormat(iso = ISO.DATE) LocalDate fechaInicio,
		@NotNull(message = "La fecha de fin es obligatoria") @DateTimeFormat(iso = ISO.DATE) LocalDate fechaFin,
		@NotNull(message = "El profesor es obligatorio") Long idProfesor,
		String profesor) {

	public static ImparticionViewModel vacia() {
		return new ImparticionViewModel(null, null, null, null, null, null);
	}

	public boolean fechasCoherentes() {
		return fechaInicio == null || fechaFin == null || !fechaFin.isBefore(fechaInicio);
	}

}
