package com.arquitecturajava.controllers;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.arquitecturajava.negocio.Matricula;
import com.arquitecturajava.repositories.ImparticionRepository;
import com.arquitecturajava.repositories.MatriculaRepository;
import com.arquitecturajava.viewmodels.MatriculaViewModel;

// Controller, servicio y repositorio reales contra H2 con los datos de data.sql;
// cada test se ejecuta en una transacción que se deshace al terminar
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class MatriculaControllerIntegracionTest {

	// en data.sql la impartición 1 (Java Básico - Enero, curso 1) tiene matriculados a Carlos (1) y Marta (2)
	private static final String URL = "/cursos/1/imparticiones/1/matriculas";

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private MatriculaRepository matriculaRepository;

	@Autowired
	private ImparticionRepository imparticionRepository;

	@Test
	void listarMuestraLosAlumnosMatriculadosOrdenadosPorApellidos() throws Exception {
		MvcResult resultado = mockMvc.perform(get(URL))
				.andExpect(status().isOk())
				.andExpect(view().name("matriculas/listado"))
				.andExpect(model().attributeExists("curso", "imparticion", "alumnos", "matricula"))
				.andExpect(content().string(containsString("<td>Carlos Díaz Romero</td>")))
				.andReturn();

		@SuppressWarnings("unchecked")
		List<MatriculaViewModel> matriculas = (List<MatriculaViewModel>) resultado.getModelAndView().getModel()
				.get("matriculas");
		assertEquals(List.of("Carlos Díaz Romero", "Marta Sánchez Gil"),
				matriculas.stream().map(MatriculaViewModel::alumno).toList());
	}

	@Test
	void listarDeUnaImparticionDeOtroCursoDevuelve404() throws Exception {
		mockMvc.perform(get("/cursos/2/imparticiones/1/matriculas"))
				.andExpect(status().isNotFound());
	}

	@Test
	void matricularGuardaLaMatriculaYRedirige() throws Exception {
		// Marta (2) no está matriculada en Java Básico - Marzo (2)
		mockMvc.perform(post("/cursos/1/imparticiones/2/matriculas")
				.param("idAlumno", "2")
				.param("fecha", "2026-02-20"))
				.andExpect(redirectedUrl("/cursos/1/imparticiones/2/matriculas"));

		List<Matricula> matriculas = matriculaRepository.findByImparticionIdOrderByAlumnoApellidosAscAlumnoNombreAsc(2L);
		assertEquals(1, matriculas.size());
		assertEquals("Marta Sánchez Gil", matriculas.get(0).getAlumno().getNombreCompleto());
		assertEquals(LocalDate.of(2026, 2, 20), matriculas.get(0).getFecha());
	}

	@Test
	void matricularDosVecesAlMismoAlumnoMuestraError() throws Exception {
		mockMvc.perform(post(URL)
				.param("idAlumno", "1")
				.param("fecha", "2026-01-02"))
				.andExpect(status().isOk())
				.andExpect(view().name("matriculas/listado"))
				.andExpect(model().attributeHasFieldErrorCode("matricula", "idAlumno", "duplicado"));

		assertEquals(2, matriculaRepository.findByImparticionIdOrderByAlumnoApellidosAscAlumnoNombreAsc(1L).size());
	}

	@Test
	void matricularSinAlumnoNiFechaMuestraErrores() throws Exception {
		mockMvc.perform(post(URL))
				.andExpect(status().isOk())
				.andExpect(model().attributeHasFieldErrors("matricula", "idAlumno", "fecha"));
	}

	@Test
	void borrarEliminaLaMatricula() throws Exception {
		Long id = matriculaRepository.findByImparticionIdOrderByAlumnoApellidosAscAlumnoNombreAsc(1L).get(0).getId();

		mockMvc.perform(post(URL + "/{id}/borrar", id))
				.andExpect(redirectedUrl(URL));

		assertFalse(matriculaRepository.existsById(id));
	}

	@Test
	void borrarUnaMatriculaDeOtraImparticionDevuelve404() throws Exception {
		Long idDeOtraImparticion = matriculaRepository.findByImparticionIdOrderByAlumnoApellidosAscAlumnoNombreAsc(4L)
				.get(0).getId();

		mockMvc.perform(post(URL + "/{id}/borrar", idDeOtraImparticion))
				.andExpect(status().isNotFound());

		assertTrue(matriculaRepository.existsById(idDeOtraImparticion));
	}

	@Test
	void borrarLaImparticionBorraSusMatriculas() throws Exception {
		mockMvc.perform(post("/cursos/1/imparticiones/1/borrar"))
				.andExpect(redirectedUrl("/cursos/1/imparticiones"));

		assertFalse(imparticionRepository.existsById(1L));
		assertTrue(matriculaRepository.findByImparticionIdOrderByAlumnoApellidosAscAlumnoNombreAsc(1L).isEmpty());
	}

}
