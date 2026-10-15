package com.arquitecturajava.servicios;

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

import com.arquitecturajava.negocio.Alumno;
import com.arquitecturajava.negocio.Imparticion;
import com.arquitecturajava.negocio.Matricula;
import com.arquitecturajava.repositories.AlumnoRepository;
import com.arquitecturajava.repositories.ImparticionRepository;
import com.arquitecturajava.repositories.MatriculaRepository;

@ExtendWith(MockitoExtension.class)
class MatriculaServiceTest {

	@Mock
	private MatriculaRepository matriculaRepository;

	@Mock
	private ImparticionRepository imparticionRepository;

	@Mock
	private AlumnoRepository alumnoRepository;

	@InjectMocks
	private MatriculaService matriculaService;

	private final Imparticion enero = new Imparticion(3L, "Java - Enero", LocalDate.of(2026, 1, 12),
			LocalDate.of(2026, 1, 23));
	private final Alumno carlos = new Alumno(5L, "11223344B", "Carlos", "Díaz Romero", "carlos@example.com");

	@Test
	void buscarPorImparticionDevuelveLasMatriculasDeLaImparticion() {
		List<Matricula> matriculas = List.of(new Matricula(1L, LocalDate.of(2026, 1, 2)));
		when(imparticionRepository.findByIdAndCursoId(3L, 1L)).thenReturn(Optional.of(enero));
		when(matriculaRepository.findByImparticionIdOrderByAlumnoApellidosAscAlumnoNombreAsc(3L))
				.thenReturn(matriculas);

		assertSame(matriculas, matriculaService.buscarPorImparticion(1L, 3L));
	}

	@Test
	void buscarPorImparticionLanzaExcepcionSiNoExisteEnElCurso() {
		when(imparticionRepository.findByIdAndCursoId(3L, 99L)).thenReturn(Optional.empty());

		assertThrows(ImparticionNoEncontradaException.class, () -> matriculaService.buscarPorImparticion(99L, 3L));
	}

	@Test
	void buscarPorAlumnoLanzaExcepcionSiElAlumnoNoExiste() {
		when(alumnoRepository.existsById(99L)).thenReturn(false);

		assertThrows(AlumnoNoEncontradoException.class, () -> matriculaService.buscarPorAlumno(99L));
		verify(matriculaRepository, never()).findByAlumnoIdOrderByFechaDesc(any());
	}

	@Test
	void matricularEnlazaAlumnoEImparticionYGuarda() {
		Matricula matricula = new Matricula(7L, LocalDate.of(2026, 1, 2));
		when(imparticionRepository.findByIdAndCursoId(3L, 1L)).thenReturn(Optional.of(enero));
		when(alumnoRepository.findById(5L)).thenReturn(Optional.of(carlos));
		when(matriculaRepository.existsByAlumnoIdAndImparticionId(5L, 3L)).thenReturn(false);
		when(matriculaRepository.save(matricula)).thenReturn(matricula);

		matriculaService.matricular(1L, 3L, 5L, matricula);

		assertNull(matricula.getId());
		assertSame(enero, matricula.getImparticion());
		assertSame(carlos, matricula.getAlumno());
		assertTrue(enero.getMatriculas().contains(matricula));
		assertTrue(carlos.getMatriculas().contains(matricula));
		verify(matriculaRepository).save(matricula);
	}

	@Test
	void matricularRechazaAlumnoYaMatriculado() {
		Matricula matricula = new Matricula(null, LocalDate.of(2026, 1, 2));
		when(imparticionRepository.findByIdAndCursoId(3L, 1L)).thenReturn(Optional.of(enero));
		when(alumnoRepository.findById(5L)).thenReturn(Optional.of(carlos));
		when(matriculaRepository.existsByAlumnoIdAndImparticionId(5L, 3L)).thenReturn(true);

		assertThrows(AlumnoYaMatriculadoException.class, () -> matriculaService.matricular(1L, 3L, 5L, matricula));
		verify(matriculaRepository, never()).save(any());
	}

	@Test
	void matricularLanzaExcepcionSiElAlumnoNoExiste() {
		Matricula matricula = new Matricula(null, LocalDate.of(2026, 1, 2));
		when(imparticionRepository.findByIdAndCursoId(3L, 1L)).thenReturn(Optional.of(enero));
		when(alumnoRepository.findById(99L)).thenReturn(Optional.empty());

		assertThrows(AlumnoNoEncontradoException.class, () -> matriculaService.matricular(1L, 3L, 99L, matricula));
		verify(matriculaRepository, never()).save(any());
	}

	@Test
	void borrarEliminaLaMatriculaDeLaImparticion() {
		Matricula matricula = new Matricula(8L, LocalDate.of(2026, 1, 2));
		when(imparticionRepository.findByIdAndCursoId(3L, 1L)).thenReturn(Optional.of(enero));
		when(matriculaRepository.findByIdAndImparticionId(8L, 3L)).thenReturn(Optional.of(matricula));

		matriculaService.borrar(1L, 3L, 8L);

		verify(matriculaRepository).delete(matricula);
	}

	@Test
	void borrarLanzaExcepcionSiLaMatriculaNoEsDeLaImparticion() {
		when(imparticionRepository.findByIdAndCursoId(3L, 1L)).thenReturn(Optional.of(enero));
		when(matriculaRepository.findByIdAndImparticionId(8L, 3L)).thenReturn(Optional.empty());

		assertThrows(MatriculaNoEncontradaException.class, () -> matriculaService.borrar(1L, 3L, 8L));
		verify(matriculaRepository, never()).delete(any());
	}

}
