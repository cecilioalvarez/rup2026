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
import com.arquitecturajava.e2e.paginas.ListadoProfesoresPagina;
import com.arquitecturajava.negocio.Curso;
import com.arquitecturajava.negocio.Imparticion;
import com.arquitecturajava.repositories.CursoNivelRepository;
import com.arquitecturajava.repositories.CursoRepository;
import com.arquitecturajava.repositories.ImparticionRepository;
import com.arquitecturajava.repositories.ProfesorRepository;

// Test de extremo a extremo: partiendo de un curso con una impartición, registra un profesor nuevo
// y lo asigna a esa impartición desde el navegador (Microsoft Edge con ventana visible).
// Los detalles de cada pantalla (ids, selectores, pausas) están en los Page Objects de e2e.paginas.
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DisplayName("Alta de profesor asignado a una impartición")
class AltaProfesorConImparticionSeleniumTest {

	// datos del curso y la impartición de partida, y del profesor que se crea en el test;
	// sirven también para encontrarlos y borrarlos al final
	private static final String TITULO = "Selenium para Profesores";
	private static final String IMPARTICION = "Selenium para Profesores - Noviembre";
	private static final String DNI = "33445566K";

	// puerto aleatorio en el que Spring Boot ha arrancado el servidor para este test
	@LocalServerPort
	private int puerto;

	// acceso directo a la base de datos para preparar los datos de partida y limpiar al final
	@Autowired
	private CursoRepository cursoRepository;

	@Autowired
	private CursoNivelRepository cursoNivelRepository;

	@Autowired
	private ImparticionRepository imparticionRepository;

	@Autowired
	private ProfesorRepository profesorRepository;

	// el WebDriver es el "mando a distancia" del navegador: abre páginas, hace clic, escribe...
	private WebDriver driver;

	// antes de cada test: se preparan los datos de partida y se abre un navegador Edge nuevo
	@BeforeEach
	void prepararCursoConImparticionYAbrirNavegador() {
		// punto de partida: un curso con una impartición. La aplicación exige que toda impartición tenga
		// profesor, así que empieza con el primero de data.sql (Laura); el test la cambiará por el nuevo
		Curso curso = cursoRepository.save(new Curso(null, TITULO, "Cecilio Álvarez", 20,
				cursoNivelRepository.findAllByOrderByOrdenAsc().get(0)));
		Imparticion imparticion = new Imparticion(null, IMPARTICION, LocalDate.of(2026, 11, 2),
				LocalDate.of(2026, 11, 13));
		imparticion.setCurso(curso);
		imparticion.setProfesor(profesorRepository.findAllByOrderByApellidosAscNombreAsc().get(0));
		imparticionRepository.save(imparticion);

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
		// al borrar el curso se borra en cascada su impartición
		cursoRepository.deleteAll(cursoRepository.findByTituloStartingWithIgnoreCase(TITULO));
		// sin la impartición, el profesor nuevo ya no tiene imparticiones y se puede borrar
		profesorRepository.findAllByOrderByApellidosAscNombreAsc().stream()
				.filter(p -> p.getDni().equals(DNI))
				.forEach(profesorRepository::delete);
	}

	@Test
	@DisplayName("el profesor dado de alta se asigna a la impartición y aparece en su listado")
	void altaDeProfesorAsignadoAImparticionLoMuestraEnElListadoDeImparticiones() {
		// 1. el usuario da de alta el profesor desde el listado de profesores
		ListadoProfesoresPagina profesores = ListadoProfesoresPagina.navegarAListado(driver, puerto)
				.navegarANuevoProfesor()
				.conDni(DNI)
				.conNombre("Pedro")
				.conApellidos("Ruiz Sanz")
				.navegarAListadoTrasGuardar();
		assertEquals(List.of(DNI, "Pedro", "Ruiz Sanz"), profesores.filaDelProfesor(DNI));

		// 2. va a los cursos y entra en las imparticiones del curso: la impartición tiene a Laura
		ListadoImparticionesPagina imparticiones = ListadoCursosPagina.navegarAListado(driver, puerto)
				.navegarAImparticiones(TITULO);
		assertEquals("Laura Martín García", imparticiones.filaDeImparticion(IMPARTICION).get(1));

		// 3. edita la impartición, elige al profesor nuevo en el desplegable y guarda
		imparticiones = imparticiones.navegarAEditarImparticion(IMPARTICION)
				.conProfesor("Pedro Ruiz Sanz")
				.navegarAListadoTrasGuardar();

		// 4. comprobación: la impartición conserva sus datos y ahora la imparte el profesor nuevo
		assertEquals(List.of(IMPARTICION, "Pedro Ruiz Sanz", "02/11/2026", "13/11/2026"),
				imparticiones.filaDeImparticion(IMPARTICION));
	}

}
