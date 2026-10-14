package com.arquitecturajava.negocio;

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

class CursoTest {

	private static final CursoNivel BASICO = new CursoNivel(1L, "Básico", 1);

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
	void cursoDe200HorasEsValido() {
		Curso curso = new Curso(null, "Java", "Cecilio Álvarez", 200, BASICO);

		assertTrue(validator.validate(curso).isEmpty());
	}

	@Test
	void cursoDeMasDe200HorasNoEsValido() {
		Curso curso = new Curso(null, "Java", "Cecilio Álvarez", 201, BASICO);

		Set<ConstraintViolation<Curso>> errores = validator.validate(curso);

		assertEquals(1, errores.size());
		ConstraintViolation<Curso> error = errores.iterator().next();
		assertEquals("duracion", error.getPropertyPath().toString());
		assertEquals("La duración no puede superar las 200 horas", error.getMessage());
	}

}
