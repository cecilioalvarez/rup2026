package com.arquitecturajava.controllers;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.arquitecturajava.negocio.Imparticion;
import com.arquitecturajava.repositories.ImparticionRepository;
import com.arquitecturajava.viewmodels.ImparticionViewModel;

// Controller, servicio y repositorio reales contra H2 con los datos de data.sql;
// cada test se ejecuta en una transacción que se deshace al terminar
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ImparticionControllerIntegracionTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ImparticionRepository imparticionRepository;

	@Test
	void listarMuestraElProfesorDeCadaImparticion() throws Exception {
		MvcResult resultado = mockMvc.perform(get("/cursos/1/imparticiones"))
				.andExpect(status().isOk())
				.andExpect(view().name("imparticiones/listado"))
				.andExpect(content().string(containsString("<td>Laura Martín García</td>")))
				.andExpect(content().string(containsString("<td>Ana Peña López</td>")))
				.andReturn();

		@SuppressWarnings("unchecked")
		List<ImparticionViewModel> imparticiones = (List<ImparticionViewModel>) resultado.getModelAndView()
				.getModel().get("imparticiones");
		assertEquals(List.of("Laura Martín García", "Ana Peña López"),
				imparticiones.stream().map(ImparticionViewModel::profesor).toList());
	}

	@Test
	void formularioOfreceLosProfesores() throws Exception {
		mockMvc.perform(get("/cursos/1/imparticiones/nueva"))
				.andExpect(status().isOk())
				.andExpect(model().attributeExists("profesores"))
				.andExpect(content().string(containsString("Ana Peña López</option>")));
	}

	@Test
	void insertarGuardaLaImparticionConSuProfesor() throws Exception {
		mockMvc.perform(post("/cursos/1/imparticiones")
				.param("nombre", "Java Básico - Mayo")
				.param("fechaInicio", "2026-05-04")
				.param("fechaFin", "2026-05-08")
				.param("idProfesor", "2"))
				.andExpect(redirectedUrl("/cursos/1/imparticiones"));

		Imparticion guardada = imparticionRepository.findByCursoIdOrderByFechaInicioAsc(1L).stream()
				.filter(i -> i.getNombre().equals("Java Básico - Mayo")).findFirst().orElseThrow();
		assertEquals("Ana Peña López", guardada.getProfesor().getNombreCompleto());
	}

	@Test
	void insertarSinProfesorVuelveAlFormularioConError() throws Exception {
		mockMvc.perform(post("/cursos/1/imparticiones")
				.param("nombre", "Java Básico - Mayo")
				.param("fechaInicio", "2026-05-04")
				.param("fechaFin", "2026-05-08"))
				.andExpect(status().isOk())
				.andExpect(view().name("imparticiones/formulario"))
				.andExpect(model().attributeHasFieldErrors("imparticion", "idProfesor"));
	}

	@Test
	void actualizarCambiaElProfesor() throws Exception {
		Imparticion enero = imparticionRepository.findByCursoIdOrderByFechaInicioAsc(1L).get(0);

		mockMvc.perform(post("/cursos/1/imparticiones/{id}", enero.getId())
				.param("nombre", enero.getNombre())
				.param("fechaInicio", "2026-01-12")
				.param("fechaFin", "2026-01-23")
				.param("idProfesor", "2"))
				.andExpect(redirectedUrl("/cursos/1/imparticiones"));

		assertEquals("Ana Peña López",
				imparticionRepository.findById(enero.getId()).orElseThrow().getProfesor().getNombreCompleto());
	}

}
