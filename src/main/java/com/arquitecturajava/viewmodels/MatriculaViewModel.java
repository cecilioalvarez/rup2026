package com.arquitecturajava.viewmodels;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import jakarta.validation.constraints.NotNull;

// fecha e idAlumno llegan del formulario; el resto solo se usa para mostrar los listados
public record MatriculaViewModel(
		Long id,
		@NotNull(message = "La fecha es obligatoria") @DateTimeFormat(iso = ISO.DATE) LocalDate fecha,
		@NotNull(message = "El alumno es obligatorio") Long idAlumno,
		String alumno,
		Long idImparticion,
		String imparticion,
		Long idCurso,
		String curso) {

	public static MatriculaViewModel vacia(LocalDate fecha) {
		return new MatriculaViewModel(null, fecha, null, null, null, null, null, null);
	}

}
