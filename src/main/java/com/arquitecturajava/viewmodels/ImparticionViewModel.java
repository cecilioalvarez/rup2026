package com.arquitecturajava.viewmodels;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ImparticionViewModel(
		Long id,
		@NotBlank(message = "El nombre es obligatorio") String nombre,
		@NotNull(message = "La fecha de inicio es obligatoria") @DateTimeFormat(iso = ISO.DATE) LocalDate fechaInicio,
		@NotNull(message = "La fecha de fin es obligatoria") @DateTimeFormat(iso = ISO.DATE) LocalDate fechaFin) {

	public static ImparticionViewModel vacia() {
		return new ImparticionViewModel(null, null, null, null);
	}

	public boolean fechasCoherentes() {
		return fechaInicio == null || fechaFin == null || !fechaFin.isBefore(fechaInicio);
	}

}
