package com.arquitecturajava.servicios;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.arquitecturajava.negocio.Curso;
import com.arquitecturajava.negocio.CursoNivel;
import com.arquitecturajava.repositories.CursoNivelRepository;
import com.arquitecturajava.repositories.CursoRepository;

@ExtendWith(MockitoExtension.class)
class CursoServiceTest {

	private static final CursoNivel BASICO = new CursoNivel(1L, "Básico", 1);

	@Mock
	private CursoRepository cursoRepository;

	@Mock
	private CursoNivelRepository cursoNivelRepository;

	@InjectMocks
	private CursoService cursoService;

	@Test
	void buscarPorSinTextoDevuelveTodos() {
		List<Curso> cursos = List.of(new Curso(1L, "Java", "Cecilio Álvarez", 20, BASICO));
		when(cursoRepository.findAll()).thenReturn(cursos);

		assertSame(cursos, cursoService.buscarPor("titulo", "  "));
	}

	@Test
	void buscarPorTituloUsaElPrefijoSinEspacios() {
		List<Curso> cursos = List.of(new Curso(1L, "Java", "Cecilio Álvarez", 20, BASICO));
		when(cursoRepository.findByTituloStartingWithIgnoreCase("Ja")).thenReturn(cursos);

		assertSame(cursos, cursoService.buscarPor("titulo", " Ja "));
	}

	@Test
	void buscarPorCampoNoValidoLanzaExcepcion() {
		assertThrows(IllegalArgumentException.class, () -> cursoService.buscarPor("duracion", "20"));
	}

	@Test
	void buscarPorIdLanzaExcepcionSiNoExiste() {
		when(cursoRepository.findById(99L)).thenReturn(Optional.empty());

		assertThrows(CursoNoEncontradoException.class, () -> cursoService.buscarPorId(99L));
	}

	@Test
	void insertarIgnoraElIdRecibidoYGuarda() {
		Curso curso = new Curso(5L, "Docker", "Ana Peña", 15, BASICO);
		when(cursoRepository.save(curso)).thenReturn(curso);

		cursoService.insertar(curso);

		assertNull(curso.getId());
		verify(cursoRepository).save(curso);
	}

	@Test
	void actualizarAsignaElIdYGuardaSiExiste() {
		Curso curso = new Curso(null, "Spring Boot 3", "Ana Peña", 40, BASICO);
		when(cursoRepository.existsById(2L)).thenReturn(true);
		when(cursoRepository.save(curso)).thenReturn(curso);

		cursoService.actualizar(2L, curso);

		assertEquals(2L, curso.getId());
		verify(cursoRepository).save(curso);
	}

	@Test
	void actualizarLanzaExcepcionYNoGuardaSiNoExiste() {
		Curso curso = new Curso(null, "Spring Boot 3", "Ana Peña", 40, BASICO);
		when(cursoRepository.existsById(99L)).thenReturn(false);

		assertThrows(CursoNoEncontradoException.class, () -> cursoService.actualizar(99L, curso));
		verify(cursoRepository, never()).save(any());
	}

}
