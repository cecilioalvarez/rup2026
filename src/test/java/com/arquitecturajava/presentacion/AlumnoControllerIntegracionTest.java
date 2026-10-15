package com.arquitecturajava.presentacion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import com.arquitecturajava.persistencia.AlumnoRepository;
import com.arquitecturajava.viewmodels.AlumnoViewModel;

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
	void borrarEliminaElAlumno() throws Exception {
		Long id = alumnoRepository.findAllByOrderByApellidosAscNombreAsc().get(0).getId();

		mockMvc.perform(post("/alumnos/{id}/borrar", id))
				.andExpect(redirectedUrl("/alumnos"));

		assertFalse(alumnoRepository.existsById(id));
	}

}
