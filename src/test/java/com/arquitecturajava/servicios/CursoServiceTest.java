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
import com.arquitecturajava.persistencia.CursoRepository;

@ExtendWith(MockitoExtension.class)
class CursoServiceTest {

	@Mock
	private CursoRepository cursoRepository;

	@InjectMocks
	private CursoService cursoService;

	@Test
	void getNivelesDevuelveLosTresNiveles() {
		assertEquals(List.of("Básico", "Intermedio", "Avanzado"), cursoService.getNiveles());
	}

	@Test
	void buscarTodosDevuelveLosCursosDelRepositorio() {
		List<Curso> cursos = List.of(new Curso(1L, "Java", "Cecilio Álvarez", 20, "Básico"));
		when(cursoRepository.findAll()).thenReturn(cursos);

		assertSame(cursos, cursoService.buscarTodos());
	}

	@Test
	void buscarPorSinTextoDevuelveTodos() {
		List<Curso> cursos = List.of(new Curso(1L, "Java", "Cecilio Álvarez", 20, "Básico"));
		when(cursoRepository.findAll()).thenReturn(cursos);

		assertSame(cursos, cursoService.buscarPor("titulo", "  "));
	}

	@Test
	void buscarPorTituloUsaElPrefijoSinEspacios() {
		List<Curso> cursos = List.of(new Curso(1L, "Java", "Cecilio Álvarez", 20, "Básico"));
		when(cursoRepository.findByTituloStartingWithIgnoreCase("Ja")).thenReturn(cursos);

		assertSame(cursos, cursoService.buscarPor("titulo", " Ja "));
	}

	@Test
	void buscarPorAutorDelegaEnElRepositorio() {
		List<Curso> cursos = List.of(new Curso(1L, "Java", "Cecilio Álvarez", 20, "Básico"));
		when(cursoRepository.findByAutorStartingWithIgnoreCase("Cec")).thenReturn(cursos);

		assertSame(cursos, cursoService.buscarPor("autor", "Cec"));
	}

	@Test
	void buscarPorNivelDelegaEnElRepositorio() {
		List<Curso> cursos = List.of(new Curso(1L, "Java", "Cecilio Álvarez", 20, "Básico"));
		when(cursoRepository.findByNivelStartingWithIgnoreCase("Bás")).thenReturn(cursos);

		assertSame(cursos, cursoService.buscarPor("nivel", "Bás"));
	}

	@Test
	void buscarPorCampoNoValidoLanzaExcepcion() {
		assertThrows(IllegalArgumentException.class, () -> cursoService.buscarPor("duracion", "20"));
	}

	@Test
	void buscarPorIdDevuelveElCursoSiExiste() {
		Curso curso = new Curso(1L, "Java", "Cecilio Álvarez", 20, "Básico");
		when(cursoRepository.findById(1L)).thenReturn(Optional.of(curso));

		assertSame(curso, cursoService.buscarPorId(1L));
	}

	@Test
	void buscarPorIdLanzaExcepcionSiNoExiste() {
		when(cursoRepository.findById(99L)).thenReturn(Optional.empty());

		assertThrows(CursoNoEncontradoException.class, () -> cursoService.buscarPorId(99L));
	}

	@Test
	void insertarIgnoraElIdRecibidoYGuarda() {
		Curso curso = new Curso(5L, "Docker", "Ana Peña", 15, "Intermedio");
		when(cursoRepository.save(curso)).thenReturn(curso);

		cursoService.insertar(curso);

		assertNull(curso.getId());
		verify(cursoRepository).save(curso);
	}

	@Test
	void actualizarAsignaElIdYGuardaSiExiste() {
		Curso curso = new Curso(null, "Spring Boot 3", "Ana Peña", 40, "Avanzado");
		when(cursoRepository.existsById(2L)).thenReturn(true);
		when(cursoRepository.save(curso)).thenReturn(curso);

		cursoService.actualizar(2L, curso);

		assertEquals(2L, curso.getId());
		verify(cursoRepository).save(curso);
	}

	@Test
	void actualizarLanzaExcepcionYNoGuardaSiNoExiste() {
		Curso curso = new Curso(null, "Spring Boot 3", "Ana Peña", 40, "Avanzado");
		when(cursoRepository.existsById(99L)).thenReturn(false);

		assertThrows(CursoNoEncontradoException.class, () -> cursoService.actualizar(99L, curso));
		verify(cursoRepository, never()).save(any());
	}

	@Test
	void borrarDelegaEnElRepositorio() {
		cursoService.borrar(3L);

		verify(cursoRepository).deleteById(3L);
	}

}
