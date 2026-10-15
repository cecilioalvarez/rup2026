package com.arquitecturajava.presentacion;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

// comprueba la página de inicio y que el menú principal se comparte entre las vistas
@SpringBootTest
@AutoConfigureMockMvc
class InicioControllerIntegracionTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void inicioMuestraElMenuPrincipal() throws Exception {
		mockMvc.perform(get("/"))
				.andExpect(status().isOk())
				.andExpect(view().name("index"))
				.andExpect(content().string(containsString("id=\"menuPrincipal\"")));
	}

	@Test
	void listadoDeCursosMarcaCursosComoActivo() throws Exception {
		mockMvc.perform(get("/cursos"))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("class=\"nav-link active\" href=\"/cursos\" aria-current=\"page\"")));
	}

	@Test
	void listadoDeProfesoresMarcaProfesoresComoActivo() throws Exception {
		mockMvc.perform(get("/profesores"))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("class=\"nav-link active\" href=\"/profesores\" aria-current=\"page\"")));
	}

	@Test
	void listadoDeAlumnosMarcaAlumnosComoActivo() throws Exception {
		mockMvc.perform(get("/alumnos"))
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("class=\"nav-link active\" href=\"/alumnos\" aria-current=\"page\"")));
	}

}
