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

class AlumnoViewModelTest {

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
	void alumnoConDatosCorrectosEsValido() {
		AlumnoViewModel vm = new AlumnoViewModel(null, "11223344B", "Carlos", "Díaz Romero", "carlos.diaz@example.com");

		assertTrue(validator.validate(vm).isEmpty());
	}

	@Test
	void dniSinLetraNoEsValido() {
		AlumnoViewModel vm = new AlumnoViewModel(null, "11223344", "Carlos", "Díaz Romero", "carlos.diaz@example.com");

		Set<ConstraintViolation<AlumnoViewModel>> errores = validator.validate(vm);

		assertEquals(1, errores.size());
		assertEquals("dni", errores.iterator().next().getPropertyPath().toString());
	}

	@Test
	void emailMalFormadoNoEsValido() {
		AlumnoViewModel vm = new AlumnoViewModel(null, "11223344B", "Carlos", "Díaz Romero", "carlos.example.com");

		Set<ConstraintViolation<AlumnoViewModel>> errores = validator.validate(vm);

		assertEquals(1, errores.size());
		assertEquals("email", errores.iterator().next().getPropertyPath().toString());
	}

	@Test
	void camposVaciosNoSonValidos() {
		AlumnoViewModel vm = new AlumnoViewModel(null, "11223344B", " ", "", "");

		assertEquals(3, validator.validate(vm).size());
	}

}
