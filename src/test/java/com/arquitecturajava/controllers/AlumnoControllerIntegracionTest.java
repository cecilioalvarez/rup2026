package com.arquitecturajava.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
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

import com.arquitecturajava.negocio.Alumno;
import com.arquitecturajava.repositories.AlumnoRepository;
import com.arquitecturajava.viewmodels.AlumnoViewModel;
import com.arquitecturajava.viewmodels.MatriculaViewModel;

// Controller, servicio y repositorio reales contra H2 con los datos de data.sql;
// cada test se ejecuta en una transacción que se deshace al terminar
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AlumnoControllerIntegracionTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private AlumnoRepository alumnoRepository;

	@Test
	void listarDevuelveLosAlumnosOrdenadosPorApellidos() throws Exception {
		MvcResult resultado = mockMvc.perform(get("/alumnos"))
				.andExpect(status().isOk())
				.andExpect(view().name("alumnos/listado"))
				.andReturn();

		@SuppressWarnings("unchecked")
		List<AlumnoViewModel> alumnos = (List<AlumnoViewModel>) resultado.getModelAndView().getModel()
				.get("alumnos");
		assertEquals(List.of("Díaz Romero", "Sánchez Gil"),
				alumnos.stream().map(AlumnoViewModel::apellidos).toList());
	}

	@Test
	void insertarGuardaElAlumnoYRedirigeAlListado() throws Exception {
		mockMvc.perform(post("/alumnos")
				.param("dni", "55667788D")
				.param("nombre", "Lucía")
				.param("apellidos", "Ortega Ruiz")
				.param("email", "lucia.ortega@example.com"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/alumnos"));

		assertEquals(3, alumnoRepository.count());
	}

	@Test
	void insertarConEmailNoValidoVuelveAlFormularioYNoGuarda() throws Exception {
		mockMvc.perform(post("/alumnos")
				.param("dni", "55667788D")
				.param("nombre", "Lucía")
				.param("apellidos", "Ortega Ruiz")
				.param("email", "lucia"))
				.andExpect(status().isOk())
				.andExpect(view().name("alumnos/formulario"))
				.andExpect(model().attributeHasFieldErrors("alumno", "email"));

		assertEquals(2, alumnoRepository.count());
	}

	@Test
	void insertarConDniRepetidoVuelveAlFormularioConErrorYNoGuarda() throws Exception {
		mockMvc.perform(post("/alumnos")
				.param("dni", "11223344b")
				.param("nombre", "Lucía")
				.param("apellidos", "Ortega Ruiz")
				.param("email", "lucia.ortega@example.com"))
				.andExpect(status().isOk())
				.andExpect(view().name("alumnos/formulario"))
				.andExpect(model().attributeHasFieldErrorCode("alumno", "dni", "duplicado"));

		assertEquals(2, alumnoRepository.count());
	}

	@Test
	void actualizarModificaElAlumnoExistente() throws Exception {
		Long id = alumnoRepository.findAllByOrderByApellidosAscNombreAsc().get(0).getId();

		mockMvc.perform(post("/alumnos/{id}", id)
				.param("dni", "11223344B")
				.param("nombre", "Carlos")
				.param("apellidos", "Díaz Romero")
				.param("email", "cdiaz@example.com"))
				.andExpect(redirectedUrl("/alumnos"));

		Alumno alumno = alumnoRepository.findById(id).orElseThrow();
		assertEquals("cdiaz@example.com", alumno.getEmail());
	}

	@Test
	void editarAlumnoInexistenteDevuelve404() throws Exception {
		mockMvc.perform(get("/alumnos/{id}/editar", 999))
				.andExpect(status().isNotFound());
	}

	@Test
	void borrarEliminaElAlumnoSinMatriculas() throws Exception {
		Long id = alumnoRepository.save(new Alumno(null, "55667788D", "Lucía", "Vega Ruiz", "lucia@example.com"))
				.getId();

		mockMvc.perform(post("/alumnos/{id}/borrar", id))
				.andExpect(redirectedUrl("/alumnos"))
				.andExpect(flash().attributeCount(0));

		assertFalse(alumnoRepository.existsById(id));
	}

	@Test
	void borrarNoEliminaElAlumnoConMatriculasYAvisa() throws Exception {
		// en data.sql todos los alumnos tienen matrículas
		Long id = alumnoRepository.findAllByOrderByApellidosAscNombreAsc().get(0).getId();

		mockMvc.perform(post("/alumnos/{id}/borrar", id))
				.andExpect(redirectedUrl("/alumnos"))
				.andExpect(flash().attribute("error", "No se puede borrar a Carlos Díaz Romero porque tiene matrículas"));

		assertTrue(alumnoRepository.existsById(id));
	}

	@Test
	void matriculasMuestraLasMatriculasDelAlumnoConSuCurso() throws Exception {
		Long id = alumnoRepository.findAllByOrderByApellidosAscNombreAsc().get(0).getId();

		MvcResult resultado = mockMvc.perform(get("/alumnos/{id}/matriculas", id))
				.andExpect(status().isOk())
				.andExpect(view().name("alumnos/matriculas"))
				.andReturn();

		@SuppressWarnings("unchecked")
		List<MatriculaViewModel> matriculas = (List<MatriculaViewModel>) resultado.getModelAndView().getModel()
				.get("matriculas");
		// ordenadas de la más reciente a la más antigua
		assertEquals(List.of("Spring Boot - Febrero", "Java Básico - Enero"),
				matriculas.stream().map(MatriculaViewModel::imparticion).toList());
		assertEquals(List.of("Spring Boot", "Java Básico"),
				matriculas.stream().map(MatriculaViewModel::curso).toList());
	}

	@Test
	void matriculasDeUnAlumnoQueNoExisteDevuelve404() throws Exception {
		mockMvc.perform(get("/alumnos/{id}/matriculas", 999))
				.andExpect(status().isNotFound());
	}

}
