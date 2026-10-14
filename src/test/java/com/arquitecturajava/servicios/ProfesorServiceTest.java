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

import com.arquitecturajava.negocio.Profesor;
import com.arquitecturajava.persistencia.ProfesorRepository;

@ExtendWith(MockitoExtension.class)
class ProfesorServiceTest {

	@Mock
	private ProfesorRepository profesorRepository;

	@InjectMocks
	private ProfesorService profesorService;

	@Test
	void buscarPorIdLanzaExcepcionSiNoExiste() {
		when(profesorRepository.findById(99L)).thenReturn(Optional.empty());

		assertThrows(ProfesorNoEncontradoException.class, () -> profesorService.buscarPorId(99L));
	}

	@Test
	void insertarIgnoraElIdRecibidoYGuarda() {
		Profesor profesor = new Profesor(5L, "12345678Z", "Laura", "Martín García");
		when(profesorRepository.save(profesor)).thenReturn(profesor);

		profesorService.insertar(profesor);

		assertNull(profesor.getId());
		verify(profesorRepository).save(profesor);
	}

	@Test
	void insertarConDniRepetidoLanzaExcepcionYNoGuarda() {
		Profesor profesor = new Profesor(null, "12345678Z", "Laura", "Martín García");
		when(profesorRepository.existsByDniIgnoreCase("12345678Z")).thenReturn(true);

		assertThrows(DniDuplicadoException.class, () -> profesorService.insertar(profesor));
		verify(profesorRepository, never()).save(any());
	}

	@Test
	void actualizarConDniDeOtroProfesorLanzaExcepcionYNoGuarda() {
		Profesor profesor = new Profesor(null, "87654321X", "Laura", "Martín García");
		when(profesorRepository.existsById(1L)).thenReturn(true);
		when(profesorRepository.existsByDniIgnoreCaseAndIdNot("87654321X", 1L)).thenReturn(true);

		assertThrows(DniDuplicadoException.class, () -> profesorService.actualizar(1L, profesor));
		verify(profesorRepository, never()).save(any());
	}

	@Test
	void actualizarAsignaElIdYGuardaSiExiste() {
		Profesor profesor = new Profesor(null, "12345678Z", "Laura", "Martín García");
		when(profesorRepository.existsById(2L)).thenReturn(true);
		when(profesorRepository.save(profesor)).thenReturn(profesor);

		profesorService.actualizar(2L, profesor);

		assertEquals(2L, profesor.getId());
		verify(profesorRepository).save(profesor);
	}

	@Test
	void actualizarLanzaExcepcionYNoGuardaSiNoExiste() {
		Profesor profesor = new Profesor(null, "12345678Z", "Laura", "Martín García");
		when(profesorRepository.existsById(99L)).thenReturn(false);

		assertThrows(ProfesorNoEncontradoException.class, () -> profesorService.actualizar(99L, profesor));
		verify(profesorRepository, never()).save(any());
	}

}
