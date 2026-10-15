package com.arquitecturajava.servicios;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.arquitecturajava.negocio.Alumno;
import com.arquitecturajava.persistencia.AlumnoRepository;

@ExtendWith(MockitoExtension.class)
class AlumnoServiceTest {

	@Mock
	private AlumnoRepository alumnoRepository;

	@InjectMocks
	private AlumnoService alumnoService;

	@Test
	void buscarPorIdLanzaExcepcionSiNoExiste() {
		when(alumnoRepository.findById(99L)).thenReturn(Optional.empty());

		assertThrows(AlumnoNoEncontradoException.class, () -> alumnoService.buscarPorId(99L));
	}

	@Test
	void insertarIgnoraElIdRecibidoYGuarda() {
		Alumno alumno = new Alumno(5L, "11223344B", "Carlos", "Díaz Romero", "carlos.diaz@example.com");
		when(alumnoRepository.save(alumno)).thenReturn(alumno);

		alumnoService.insertar(alumno);

		assertNull(alumno.getId());
		verify(alumnoRepository).save(alumno);
	}

	@Test
	void insertarConDniRepetidoLanzaExcepcionYNoGuarda() {
		Alumno alumno = new Alumno(null, "11223344B", "Carlos", "Díaz Romero", "carlos.diaz@example.com");
		when(alumnoRepository.existsByDniIgnoreCase("11223344B")).thenReturn(true);

		assertThrows(DniDuplicadoException.class, () -> alumnoService.insertar(alumno));
		verify(alumnoRepository, never()).save(any());
	}

	@Test
	void actualizarConDniDeOtroAlumnoLanzaExcepcionYNoGuarda() {
		Alumno alumno = new Alumno(null, "44332211C", "Carlos", "Díaz Romero", "carlos.diaz@example.com");
		when(alumnoRepository.existsById(1L)).thenReturn(true);
		when(alumnoRepository.existsByDniIgnoreCaseAndIdNot("44332211C", 1L)).thenReturn(true);

		assertThrows(DniDuplicadoException.class, () -> alumnoService.actualizar(1L, alumno));
		verify(alumnoRepository, never()).save(any());
	}

	@Test
	void actualizarAsignaElIdYGuardaSiExiste() {
		Alumno alumno = new Alumno(null, "11223344B", "Carlos", "Díaz Romero", "carlos.diaz@example.com");
		when(alumnoRepository.existsById(2L)).thenReturn(true);
		when(alumnoRepository.save(alumno)).thenReturn(alumno);

		alumnoService.actualizar(2L, alumno);

		assertEquals(2L, alumno.getId());
		verify(alumnoRepository).save(alumno);
	}

	@Test
	void actualizarLanzaExcepcionYNoGuardaSiNoExiste() {
		Alumno alumno = new Alumno(null, "11223344B", "Carlos", "Díaz Romero", "carlos.diaz@example.com");
		when(alumnoRepository.existsById(99L)).thenReturn(false);

		assertThrows(AlumnoNoEncontradoException.class, () -> alumnoService.actualizar(99L, alumno));
		verify(alumnoRepository, never()).save(any());
	}

}
