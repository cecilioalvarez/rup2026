package com.arquitecturajava.viewmodels;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class CursoViewModelTest {

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
	void duracionDe200HorasEsValida() {
		CursoViewModel vm = new CursoViewModel(null, "Java", "A", 200, "Básico");

		assertTrue(validator.validate(vm).isEmpty());
	}

	@Test
	void duracionDeMasDe200HorasNoEsValida() {
		CursoViewModel vm = new CursoViewModel(null, "Java", "A", 201, "Básico");

		assertEquals(1, validator.validate(vm).size());
	}

}
