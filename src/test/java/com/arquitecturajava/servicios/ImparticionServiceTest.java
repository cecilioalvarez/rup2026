package com.arquitecturajava.servicios;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.arquitecturajava.negocio.Curso;
import com.arquitecturajava.negocio.Imparticion;
import com.arquitecturajava.persistencia.CursoRepository;
import com.arquitecturajava.persistencia.ImparticionRepository;

@ExtendWith(MockitoExtension.class)
class ImparticionServiceTest {

	@Mock
	private ImparticionRepository imparticionRepository;

	@Mock
	private CursoRepository cursoRepository;

	@InjectMocks
	private ImparticionService imparticionService;

	@Test
	void buscarPorCursoDevuelveLasImparticionesDelCurso() {
		List<Imparticion> imparticiones = List.of(
				new Imparticion(1L, "Java - Enero", LocalDate.of(2026, 1, 12), LocalDate.of(2026, 1, 23)));
		when(cursoRepository.existsById(1L)).thenReturn(true);
		when(imparticionRepository.findByCursoIdOrderByFechaInicioAsc(1L)).thenReturn(imparticiones);

		assertSame(imparticiones, imparticionService.buscarPorCurso(1L));
	}

	@Test
	void buscarPorCursoLanzaExcepcionSiElCursoNoExiste() {
		when(cursoRepository.existsById(99L)).thenReturn(false);

		assertThrows(CursoNoEncontradoException.class, () -> imparticionService.buscarPorCurso(99L));
		verify(imparticionRepository, never()).findByCursoIdOrderByFechaInicioAsc(any());
	}

	@Test
	void insertarAsociaLaImparticionAlCursoYGuarda() {
		Curso curso = new Curso(1L, "Java", "Cecilio Álvarez", 20, "Básico");
		Imparticion imparticion = new Imparticion(7L, "Java - Mayo", LocalDate.of(2026, 5, 4),
				LocalDate.of(2026, 5, 8));
		when(cursoRepository.findById(1L)).thenReturn(Optional.of(curso));
		when(imparticionRepository.save(imparticion)).thenReturn(imparticion);

		imparticionService.insertar(1L, imparticion);

		assertNull(imparticion.getId());
		assertSame(curso, imparticion.getCurso());
		assertTrue(curso.getImparticiones().contains(imparticion));
		verify(imparticionRepository).save(imparticion);
	}

	@Test
	void insertarLanzaExcepcionSiElCursoNoExiste() {
		Imparticion imparticion = new Imparticion(null, "X", LocalDate.of(2026, 5, 4), LocalDate.of(2026, 5, 8));
		when(cursoRepository.findById(99L)).thenReturn(Optional.empty());

		assertThrows(CursoNoEncontradoException.class, () -> imparticionService.insertar(99L, imparticion));
		verify(imparticionRepository, never()).save(any());
	}

	@Test
	void insertarRechazaFechaFinAnteriorAInicio() {
		Curso curso = new Curso(1L, "Java", "Cecilio Álvarez", 20, "Básico");
		Imparticion imparticion = new Imparticion(null, "X", LocalDate.of(2026, 5, 8), LocalDate.of(2026, 5, 4));
		when(cursoRepository.findById(1L)).thenReturn(Optional.of(curso));

		assertThrows(IllegalArgumentException.class, () -> imparticionService.insertar(1L, imparticion));
		verify(imparticionRepository, never()).save(any());
	}

	@Test
	void buscarPorIdDevuelveLaImparticionDelCurso() {
		Imparticion imparticion = new Imparticion(3L, "Java - Mayo", LocalDate.of(2026, 5, 4),
				LocalDate.of(2026, 5, 8));
		when(imparticionRepository.findByIdAndCursoId(3L, 1L)).thenReturn(Optional.of(imparticion));

		assertSame(imparticion, imparticionService.buscarPorId(1L, 3L));
	}

	@Test
	void buscarPorIdLanzaExcepcionSiNoExisteEnElCurso() {
		when(imparticionRepository.findByIdAndCursoId(3L, 99L)).thenReturn(Optional.empty());

		assertThrows(ImparticionNoEncontradaException.class, () -> imparticionService.buscarPorId(99L, 3L));
	}

	@Test
	void actualizarModificaLosDatosConservandoElCurso() {
		Curso curso = new Curso(1L, "Java", "Cecilio Álvarez", 20, "Básico");
		Imparticion existente = new Imparticion(3L, "Java - Mayo", LocalDate.of(2026, 5, 4),
				LocalDate.of(2026, 5, 8));
		curso.addImparticion(existente);
		Imparticion datos = new Imparticion(null, "Java - Junio", LocalDate.of(2026, 6, 1),
				LocalDate.of(2026, 6, 5));
		when(imparticionRepository.findByIdAndCursoId(3L, 1L)).thenReturn(Optional.of(existente));
		when(imparticionRepository.save(existente)).thenReturn(existente);

		imparticionService.actualizar(1L, 3L, datos);

		assertEquals(3L, existente.getId());
		assertEquals("Java - Junio", existente.getNombre());
		assertEquals(LocalDate.of(2026, 6, 1), existente.getFechaInicio());
		assertEquals(LocalDate.of(2026, 6, 5), existente.getFechaFin());
		assertSame(curso, existente.getCurso());
		verify(imparticionRepository).save(existente);
	}

	@Test
	void actualizarLanzaExcepcionSiNoExisteEnElCurso() {
		Imparticion datos = new Imparticion(null, "X", LocalDate.of(2026, 6, 1), LocalDate.of(2026, 6, 5));
		when(imparticionRepository.findByIdAndCursoId(3L, 99L)).thenReturn(Optional.empty());

		assertThrows(ImparticionNoEncontradaException.class, () -> imparticionService.actualizar(99L, 3L, datos));
		verify(imparticionRepository, never()).save(any());
	}

	@Test
	void actualizarRechazaFechaFinAnteriorAInicio() {
		Imparticion existente = new Imparticion(3L, "Java - Mayo", LocalDate.of(2026, 5, 4),
				LocalDate.of(2026, 5, 8));
		Imparticion datos = new Imparticion(null, "X", LocalDate.of(2026, 6, 5), LocalDate.of(2026, 6, 1));
		when(imparticionRepository.findByIdAndCursoId(3L, 1L)).thenReturn(Optional.of(existente));

		assertThrows(IllegalArgumentException.class, () -> imparticionService.actualizar(1L, 3L, datos));
		verify(imparticionRepository, never()).save(any());
	}

	@Test
	void borrarEliminaLaImparticionDelCurso() {
		Imparticion imparticion = new Imparticion(3L, "Java - Mayo", LocalDate.of(2026, 5, 4),
				LocalDate.of(2026, 5, 8));
		when(imparticionRepository.findByIdAndCursoId(3L, 1L)).thenReturn(Optional.of(imparticion));

		imparticionService.borrar(1L, 3L);

		verify(imparticionRepository).delete(imparticion);
	}

	@Test
	void borrarLanzaExcepcionSiNoExisteEnElCurso() {
		when(imparticionRepository.findByIdAndCursoId(3L, 99L)).thenReturn(Optional.empty());

		assertThrows(ImparticionNoEncontradaException.class, () -> imparticionService.borrar(99L, 3L));
		verify(imparticionRepository, never()).delete(any());
	}

}
