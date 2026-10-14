package com.arquitecturajava.viewmodels;

import jakarta.validation.constraints.Max;

// idNivel es lo que envía el formulario; nivel es el nombre que se muestra en el listado
public record CursoViewModel(
		Long id,
		String titulo,
		String autor,
		@Max(value = 200, message = "La duración no puede superar las 200 horas") int duracion,
		Long idNivel,
		String nivel) {

	public static CursoViewModel vacio() {
		return new CursoViewModel(null, null, null, 0, null, null);
	}

	public CursoViewModel conId(Long nuevoId) {
		return new CursoViewModel(nuevoId, titulo, autor, duracion, idNivel, nivel);
	}

}
