package com.arquitecturajava.viewmodels;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class ImparticionViewModelTest {

	private static ValidatorFactory factory;
	private static Validator validator;

	@BeforeAll
	static void crearValidador() {
		factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}

	@AfterAll
	static void cerrarValidador() {
		factory.close();
	}

	@Test
	void imparticionCompletaEsValida() {
		ImparticionViewModel vm = new ImparticionViewModel(null, "Java - Enero", LocalDate.of(2026, 1, 12),
				LocalDate.of(2026, 1, 23), 1L, null);

		assertTrue(validator.validate(vm).isEmpty());
	}

	@Test
	void imparticionVaciaTieneLosCuatroCamposObligatoriosConError() {
		Set<String> campos = validator.validate(ImparticionViewModel.vacia()).stream()
				.map(v -> v.getPropertyPath().toString()).collect(Collectors.toSet());

		assertEquals(Set.of("nombre", "fechaInicio", "fechaFin", "idProfesor"), campos);
	}

	@Test
	void nombreEnBlancoNoEsValido() {
		ImparticionViewModel vm = new ImparticionViewModel(null, "   ", LocalDate.of(2026, 1, 12),
				LocalDate.of(2026, 1, 23), 1L, null);

		assertEquals(1, validator.validate(vm).size());
	}

	@Test
	void fechasCoherentesSiFinEsPosteriorOIgualAInicio() {
		LocalDate dia = LocalDate.of(2026, 1, 12);

		assertTrue(new ImparticionViewModel(null, "X", dia, dia.plusDays(5), null, null).fechasCoherentes());
		assertTrue(new ImparticionViewModel(null, "X", dia, dia, null, null).fechasCoherentes());
	}

	@Test
	void fechasNoCoherentesSiFinEsAnteriorAInicio() {
		LocalDate dia = LocalDate.of(2026, 1, 12);

		assertFalse(new ImparticionViewModel(null, "X", dia, dia.minusDays(1), null, null).fechasCoherentes());
	}

	@Test
	void fechasCoherentesNoFallaSiFaltaAlgunaFecha() {
		assertTrue(new ImparticionViewModel(null, "X", null, LocalDate.of(2026, 1, 12), null, null).fechasCoherentes());
		assertTrue(new ImparticionViewModel(null, "X", LocalDate.of(2026, 1, 12), null, null, null).fechasCoherentes());
	}

}
