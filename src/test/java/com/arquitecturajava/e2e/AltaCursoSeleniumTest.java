package com.arquitecturajava.e2e;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;

import com.arquitecturajava.e2e.paginas.ListadoCursosPagina;
import com.arquitecturajava.persistencia.CursoRepository;

// Test de extremo a extremo: arranca la aplicación en un puerto aleatorio y da de alta un curso
// desde el navegador (Microsoft Edge con ventana visible). Selenium Manager descarga el msedgedriver
// adecuado la primera vez que se ejecuta.
// Los detalles de cada pantalla (ids, selectores, pausas) están en los Page Objects de e2e.paginas.
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class AltaCursoSeleniumTest {

	// título del curso que se crea en el test; sirve también para encontrarlo y borrarlo al final
	private static final String TITULO = "Selenium WebDriver";

	// puerto aleatorio en el que Spring Boot ha arrancado el servidor para este test
	@LocalServerPort
	private int puerto;

	// acceso directo a la base de datos para comprobar y limpiar lo que hace el navegador
	@Autowired
	private CursoRepository cursoRepository;

	// el WebDriver es el "mando a distancia" del navegador: abre páginas, hace clic, escribe...
	private WebDriver driver;

	// antes de cada test: se abre un navegador Edge nuevo
	@BeforeEach
	void abrirNavegador() {
		EdgeOptions opciones = new EdgeOptions();
		// sin "--headless=new" el navegador se abre en una ventana visible
		opciones.addArguments("--window-size=1280,900");
		driver = new EdgeDriver(opciones);
	}

	// después de cada test: se cierra el navegador y se deja la base de datos como estaba
	@AfterEach
	void cerrarNavegadorYLimpiar() {
		if (driver != null) {
			// quit() cierra la ventana y termina el proceso msedgedriver
			driver.quit();
		}
		// el servidor real no deshace transacciones: se borra el curso creado para no afectar a otros tests
		cursoRepository.deleteAll(cursoRepository.findByTituloStartingWithIgnoreCase(TITULO));
	}

	@Test
	void altaDeCursoDesdeElFormularioLoMuestraEnElListado() {
		// 1. el usuario entra en el listado de cursos y pulsa "Nuevo curso"
		ListadoCursosPagina listado = ListadoCursosPagina.navegarAListado(driver, puerto)
				.navegarANuevoCurso()
				// 2. rellena el formulario campo a campo
				.conTitulo(TITULO)
				.conAutor("Cecilio Álvarez")
				.conDuracion(15)
				.conNivel("Intermedio")
				// 3. pulsa "Guardar" y la aplicación le devuelve al listado
				.navegarAListadoTrasGuardar();

		// 4. comprobaciones: estamos en el listado...
		assertEquals("Listado de cursos", listado.getCabecera());
		// ...el curso nuevo aparece en la tabla con los datos escritos...
		assertEquals(List.of(TITULO, "Cecilio Álvarez", "15", "Intermedio"), listado.filaDelCurso(TITULO));
		// ...y se ha guardado una sola vez en la base de datos
		assertEquals(1, cursoRepository.findByTituloStartingWithIgnoreCase(TITULO).size());
	}

}
