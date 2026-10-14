package com.arquitecturajava.viewmodels;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class ProfesorViewModelTest {

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
	void profesorConDniDeOchoNumerosYLetraEsValido() {
		ProfesorViewModel vm = new ProfesorViewModel(null, "12345678Z", "Laura", "Martín García");

		assertTrue(validator.validate(vm).isEmpty());
	}

	@Test
	void dniSinLetraNoEsValido() {
		ProfesorViewModel vm = new ProfesorViewModel(null, "12345678", "Laura", "Martín García");

		Set<ConstraintViolation<ProfesorViewModel>> errores = validator.validate(vm);

		assertEquals(1, errores.size());
		assertEquals("dni", errores.iterator().next().getPropertyPath().toString());
	}

	@Test
	void camposVaciosNoSonValidos() {
		ProfesorViewModel vm = new ProfesorViewModel(null, "12345678Z", " ", "");

		assertEquals(2, validator.validate(vm).size());
	}

}
