package com.arquitecturajava.negocio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
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
		Curso curso = new Curso(null, "Java", "Cecilio Álvarez", 200, "Básico");

		assertTrue(validator.validate(curso).isEmpty());
	}

	@Test
	void cursoDeMasDe200HorasNoEsValido() {
		Curso curso = new Curso(null, "Java", "Cecilio Álvarez", 201, "Básico");

		Set<ConstraintViolation<Curso>> errores = validator.validate(curso);

		assertEquals(1, errores.size());
		ConstraintViolation<Curso> error = errores.iterator().next();
		assertEquals("duracion", error.getPropertyPath().toString());
		assertEquals("La duración no puede superar las 200 horas", error.getMessage());
	}

	@Test
	void constructorVacioDejaPropiedadesPorDefecto() {
		Curso curso = new Curso();

		assertNull(curso.getId());
		assertNull(curso.getTitulo());
		assertNull(curso.getAutor());
		assertEquals(0, curso.getDuracion());
		assertNull(curso.getNivel());
	}

	@Test
	void constructorConParametrosAsignaTodasLasPropiedades() {
		Curso curso = new Curso(1L, "Java Básico", "Cecilio Álvarez", 20, "Básico");

		assertEquals(1L, curso.getId());
		assertEquals("Java Básico", curso.getTitulo());
		assertEquals("Cecilio Álvarez", curso.getAutor());
		assertEquals(20, curso.getDuracion());
		assertEquals("Básico", curso.getNivel());
	}

	@Test
	void settersModificanLasPropiedades() {
		Curso curso = new Curso();

		curso.setId(2L);
		curso.setTitulo("Spring Boot");
		curso.setAutor("Cecilio Álvarez");
		curso.setDuracion(30);
		curso.setNivel("Intermedio");

		assertEquals(2L, curso.getId());
		assertEquals("Spring Boot", curso.getTitulo());
		assertEquals("Cecilio Álvarez", curso.getAutor());
		assertEquals(30, curso.getDuracion());
		assertEquals("Intermedio", curso.getNivel());
	}

	@Test
	void setNivelSustituyeElNivelAnterior() {
		Curso curso = new Curso(3L, "Thymeleaf", "Cecilio Álvarez", 10, "Básico");

		curso.setNivel("Avanzado");

		assertEquals("Avanzado", curso.getNivel());
	}

}
