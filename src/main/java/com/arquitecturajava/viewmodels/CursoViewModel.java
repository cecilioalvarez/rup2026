package com.arquitecturajava.viewmodels;

import jakarta.validation.constraints.Max;

public record CursoViewModel(
		Long id,
		String titulo,
		String autor,
		@Max(value = 200, message = "La duración no puede superar las 200 horas") int duracion,
		String nivel) {

	public static CursoViewModel vacio() {
		return new CursoViewModel(null, null, null, 0, null);
	}

	public CursoViewModel conId(Long nuevoId) {
		return new CursoViewModel(nuevoId, titulo, autor, duracion, nivel);
	}

}
