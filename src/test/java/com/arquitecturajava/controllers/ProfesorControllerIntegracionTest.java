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

import com.arquitecturajava.negocio.Profesor;
import com.arquitecturajava.repositories.ProfesorRepository;
import com.arquitecturajava.viewmodels.ProfesorViewModel;

// Controller, servicio y repositorio reales contra H2 con los datos de data.sql;
// cada test se ejecuta en una transacción que se deshace al terminar
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ProfesorControllerIntegracionTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ProfesorRepository profesorRepository;

	@Test
	void listarDevuelveLosProfesoresOrdenadosPorApellidos() throws Exception {
		MvcResult resultado = mockMvc.perform(get("/profesores"))
				.andExpect(status().isOk())
				.andExpect(view().name("profesores/listado"))
				.andReturn();

		@SuppressWarnings("unchecked")
		List<ProfesorViewModel> profesores = (List<ProfesorViewModel>) resultado.getModelAndView().getModel()
				.get("profesores");
		assertEquals(List.of("Martín García", "Peña López"),
				profesores.stream().map(ProfesorViewModel::apellidos).toList());
	}

	@Test
	void listarFiltraPorNombreSinDistinguirMayusculas() throws Exception {
		assertEquals(List.of("Peña López"), apellidosListados("NOMBRE", "an"));
	}

	@Test
	void listarFiltraPorApellidos() throws Exception {
		assertEquals(List.of("Martín García"), apellidosListados("APELLIDOS", "garcía"));
	}

	@Test
	void listarConTextoVacioDevuelveTodos() throws Exception {
		assertEquals(List.of("Martín García", "Peña López"), apellidosListados("APELLIDOS", "  "));
	}

	@Test
	void insertarGuardaElProfesorYRedirigeAlListado() throws Exception {
		mockMvc.perform(post("/profesores")
				.param("dni", "11111111H")
				.param("nombre", "Pedro")
				.param("apellidos", "Ruiz Sanz"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/profesores"));

		assertEquals(3, profesorRepository.count());
	}

	@Test
	void insertarConDniNoValidoVuelveAlFormularioYNoGuarda() throws Exception {
		mockMvc.perform(post("/profesores")
				.param("dni", "123")
				.param("nombre", "Pedro")
				.param("apellidos", "Ruiz Sanz"))
				.andExpect(status().isOk())
				.andExpect(view().name("profesores/formulario"))
				.andExpect(model().attributeHasFieldErrors("profesor", "dni"));

		assertEquals(2, profesorRepository.count());
	}

	@Test
	void insertarConDniRepetidoVuelveAlFormularioConErrorYNoGuarda() throws Exception {
		mockMvc.perform(post("/profesores")
				.param("dni", "12345678z")
				.param("nombre", "Pedro")
				.param("apellidos", "Ruiz Sanz"))
				.andExpect(status().isOk())
				.andExpect(view().name("profesores/formulario"))
				.andExpect(model().attributeHasFieldErrorCode("profesor", "dni", "duplicado"));

		assertEquals(2, profesorRepository.count());
	}

	@Test
	void actualizarModificaElProfesorExistente() throws Exception {
		Long id = profesorRepository.findAllByOrderByApellidosAscNombreAsc().get(0).getId();

		mockMvc.perform(post("/profesores/{id}", id)
				.param("dni", "12345678Z")
				.param("nombre", "Laura")
				.param("apellidos", "Martín Gómez"))
				.andExpect(redirectedUrl("/profesores"));

		Profesor profesor = profesorRepository.findById(id).orElseThrow();
		assertEquals("Martín Gómez", profesor.getApellidos());
	}

	@Test
	void editarProfesorInexistenteDevuelve404() throws Exception {
		mockMvc.perform(get("/profesores/{id}/editar", 999))
				.andExpect(status().isNotFound());
	}

	@Test
	void borrarEliminaElProfesorSinImparticiones() throws Exception {
		Long id = profesorRepository.save(new Profesor(null, "11111111H", "Pedro", "Ruiz Sanz")).getId();

		mockMvc.perform(post("/profesores/{id}/borrar", id))
				.andExpect(redirectedUrl("/profesores"))
				.andExpect(flash().attributeCount(0));

		assertFalse(profesorRepository.existsById(id));
	}

	@Test
	void borrarNoEliminaElProfesorConImparticionesYAvisa() throws Exception {
		// en data.sql todos los profesores tienen imparticiones asignadas
		Long id = profesorRepository.findAllByOrderByApellidosAscNombreAsc().get(0).getId();

		mockMvc.perform(post("/profesores/{id}/borrar", id))
				.andExpect(redirectedUrl("/profesores"))
				.andExpect(flash().attribute("error",
						"No se puede borrar a Laura Martín García porque tiene imparticiones asignadas"));

		assertTrue(profesorRepository.existsById(id));
	}

	private List<String> apellidosListados(String campo, String texto) throws Exception {
		MvcResult resultado = mockMvc.perform(get("/profesores").param("campo", campo).param("texto", texto))
				.andExpect(status().isOk())
				.andExpect(view().name("profesores/listado"))
				.andReturn();

		@SuppressWarnings("unchecked")
		List<ProfesorViewModel> profesores = (List<ProfesorViewModel>) resultado.getModelAndView().getModel()
				.get("profesores");
		return profesores.stream().map(ProfesorViewModel::apellidos).toList();
	}

}
