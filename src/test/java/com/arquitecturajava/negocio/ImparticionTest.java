package com.arquitecturajava.negocio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class ImparticionTest {

	private static final CursoNivel BASICO = new CursoNivel(1L, "Básico", 1);

	@Test
	void cursoNuevoNoTieneImparticiones() {
		assertTrue(new Curso().getImparticiones().isEmpty());
	}

	@Test
	void addImparticionEnlazaAmbosLados() {
		Curso curso = new Curso(1L, "Java", "Cecilio Álvarez", 20, BASICO);
		Imparticion enero = new Imparticion(null, "Java - Enero", LocalDate.of(2026, 1, 12), LocalDate.of(2026, 1, 23));
		Imparticion marzo = new Imparticion(null, "Java - Marzo", LocalDate.of(2026, 3, 2), LocalDate.of(2026, 3, 13));

		curso.addImparticion(enero);
		curso.addImparticion(marzo);

		assertEquals(2, curso.getImparticiones().size());
		assertSame(curso, enero.getCurso());
		assertSame(curso, marzo.getCurso());
	}

	@Test
	void removeImparticionDesenlazaAmbosLados() {
		Curso curso = new Curso(1L, "Java", "Cecilio Álvarez", 20, BASICO);
		Imparticion enero = new Imparticion(null, "Java - Enero", LocalDate.of(2026, 1, 12), LocalDate.of(2026, 1, 23));
		curso.addImparticion(enero);

		curso.removeImparticion(enero);

		assertTrue(curso.getImparticiones().isEmpty());
		assertNull(enero.getCurso());
	}

	@Test
	void laListaDeImparticionesNoSePuedeModificarDesdeFuera() {
		Curso curso = new Curso();

		assertThrows(UnsupportedOperationException.class,
				() -> curso.getImparticiones().add(new Imparticion()));
	}

}
