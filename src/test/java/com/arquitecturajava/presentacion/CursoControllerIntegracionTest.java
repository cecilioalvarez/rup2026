package com.arquitecturajava.presentacion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

import com.arquitecturajava.negocio.Curso;
import com.arquitecturajava.persistencia.CursoRepository;
import com.arquitecturajava.viewmodels.CursoViewModel;

// Controller, servicio y repositorio reales contra H2 con los datos de data.sql;
// cada test se ejecuta en una transacción que se deshace al terminar
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CursoControllerIntegracionTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private CursoRepository cursoRepository;

	@Test
	void listarPorNivelDevuelveLosCursosDeEseNivel() throws Exception {
		MvcResult resultado = mockMvc.perform(get("/cursos").param("campo", "nivel").param("texto", "interm"))
				.andExpect(status().isOk())
				.andExpect(view().name("cursos/listado"))
				.andReturn();

		@SuppressWarnings("unchecked")
		List<CursoViewModel> cursos = (List<CursoViewModel>) resultado.getModelAndView().getModel().get("cursos");
		assertEquals(1, cursos.size());
		assertEquals("Spring Boot", cursos.get(0).titulo());
		assertEquals("Intermedio", cursos.get(0).nivel());
	}

	@Test
	void insertarGuardaElCursoConSuNivelYRedirigeAlListado() throws Exception {
		mockMvc.perform(post("/cursos")
				.param("titulo", "Docker")
				.param("autor", "Ana Peña")
				.param("duracion", "15")
				.param("idNivel", "3"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/cursos"));

		List<Curso> guardados = cursoRepository.findByTituloStartingWithIgnoreCase("Docker");
		assertEquals(1, guardados.size());
		assertEquals("Ana Peña", guardados.get(0).getAutor());
		assertEquals(3L, guardados.get(0).getNivel().getId());
		assertEquals(4, cursoRepository.count());
	}

}
