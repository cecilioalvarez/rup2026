package com.arquitecturajava.e2e;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;

import com.arquitecturajava.e2e.paginas.ListadoCursosPagina;
import com.arquitecturajava.e2e.paginas.ListadoImparticionesPagina;
import com.arquitecturajava.persistencia.CursoRepository;

// Test de extremo a extremo: arranca la aplicación en un puerto aleatorio, da de alta un curso
// y después, desde ese curso, da de alta una impartición (Microsoft Edge con ventana visible).
// Los detalles de cada pantalla (ids, selectores, pausas) están en los Page Objects de e2e.paginas.
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DisplayName("Alta de curso con impartición")
class AltaCursoConImparticionSeleniumTest {

	// título del curso que se crea en el test; sirve también para encontrarlo y borrarlo al final
	private static final String TITULO = "Selenium Avanzado";
	private static final String IMPARTICION = "Selenium Avanzado - Mayo";

	// puerto aleatorio en el que Spring Boot ha arrancado el servidor para este test
	@LocalServerPort
	private int puerto;

	// acceso directo a la base de datos para limpiar lo que hace el navegador
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
		// al borrar el curso se borran también sus imparticiones (cascade = REMOVE en Curso)
		cursoRepository.deleteAll(cursoRepository.findByTituloStartingWithIgnoreCase(TITULO));
	}

	@Test
	@DisplayName("el curso dado de alta recibe una impartición que aparece en su listado de imparticiones")
	void altaDeCursoConImparticionLaMuestraEnElListadoDeImparticiones() {
		// 1. el usuario da de alta el curso desde el listado de cursos
		ListadoCursosPagina cursos = ListadoCursosPagina.navegarAListado(driver, puerto)
				.navegarANuevoCurso()
				.conTitulo(TITULO)
				.conAutor("Cecilio Álvarez")
				.conDuracion(25)
				.conNivel("Avanzado")
				.navegarAListadoTrasGuardar();

		// 2. en la fila del curso nuevo pulsa "Imparticiones": entra en las imparticiones de ese curso
		ListadoImparticionesPagina imparticiones = cursos.navegarAImparticiones(TITULO);
		assertEquals("Imparticiones de " + TITULO, imparticiones.getCabecera());

		// 3. pulsa "Nueva impartición", rellena el formulario y guarda
		imparticiones = imparticiones.navegarANuevaImparticion()
				.conNombre(IMPARTICION)
				.conFechaInicio(LocalDate.of(2026, 5, 4))
				.conFechaFin(LocalDate.of(2026, 5, 15))
				.conProfesor("Laura Martín García")
				.navegarAListadoTrasGuardar();

		// 4. comprobaciones: seguimos en las imparticiones del curso nuevo...
		assertEquals("Imparticiones de " + TITULO, imparticiones.getCabecera());
		// ...y la impartición aparece con su profesor y sus fechas (en la tabla van con formato dd/MM/yyyy)
		assertEquals(List.of(IMPARTICION, "Laura Martín García", "04/05/2026", "15/05/2026"),
				imparticiones.filaDeImparticion(IMPARTICION));
	}

}
