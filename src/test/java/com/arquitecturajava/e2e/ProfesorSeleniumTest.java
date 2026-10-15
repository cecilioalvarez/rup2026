package com.arquitecturajava.e2e;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

import com.arquitecturajava.e2e.paginas.ListadoProfesoresPagina;
import com.arquitecturajava.negocio.Profesor;
import com.arquitecturajava.persistencia.ProfesorRepository;

// Tests de extremo a extremo del alta y el borrado de profesores desde el navegador
// (Microsoft Edge con ventana visible). Los detalles de cada pantalla están en los Page Objects de e2e.paginas.
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class ProfesorSeleniumTest {

	// DNIs de los profesores que crean los tests; sirven para encontrarlos y borrarlos al final
	private static final String DNI_ALTA = "11111111H";
	private static final String DNI_BORRADO = "22222222J";

	// puerto aleatorio en el que Spring Boot ha arrancado el servidor para este test
	@LocalServerPort
	private int puerto;

	// acceso directo a la base de datos para preparar datos, comprobar y limpiar
	@Autowired
	private ProfesorRepository profesorRepository;

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
			driver.quit();
		}
		// el servidor real no deshace transacciones: se borran los profesores que hayan quedado de los tests
		profesorRepository.findAllByOrderByApellidosAscNombreAsc().stream()
				.filter(p -> p.getDni().equals(DNI_ALTA) || p.getDni().equals(DNI_BORRADO))
				.forEach(profesorRepository::delete);
	}

	@Test
	void altaDeProfesorDesdeElFormularioLoMuestraEnElListado() {
		// 1. el usuario entra en el listado de profesores y pulsa "Nuevo profesor"
		ListadoProfesoresPagina listado = ListadoProfesoresPagina.navegarAListado(driver, puerto)
				.navegarANuevoProfesor()
				// 2. rellena el formulario campo a campo
				.conDni(DNI_ALTA)
				.conNombre("Pedro")
				.conApellidos("Ruiz Sanz")
				// 3. pulsa "Guardar" y la aplicación le devuelve al listado
				.navegarAListadoTrasGuardar();

		// 4. comprobaciones: estamos en el listado...
		assertEquals("Listado de profesores", listado.getCabecera());
		// ...el profesor nuevo aparece en la tabla con los datos escritos...
		assertEquals(List.of(DNI_ALTA, "Pedro", "Ruiz Sanz"), listado.filaDelProfesor(DNI_ALTA));
		// ...y se ha guardado en la base de datos
		assertTrue(profesorRepository.existsByDniIgnoreCase(DNI_ALTA));
	}

	@Test
	void borradoDeProfesorDesdeElListadoLoQuitaDeLaTabla() {
		// 1. preparación: se crea directamente en la base de datos un profesor sin imparticiones
		// (los de data.sql tienen imparticiones y la aplicación no deja borrarlos)
		profesorRepository.save(new Profesor(null, DNI_BORRADO, "Lucía", "Vega Ruiz"));

		// 2. el usuario entra en el listado y el profesor aparece en la tabla
		ListadoProfesoresPagina listado = ListadoProfesoresPagina.navegarAListado(driver, puerto);
		assertTrue(listado.contieneProfesor(DNI_BORRADO));

		// 3. pulsa "Borrar" en su fila y acepta la confirmación
		listado = listado.navegarAListadoTrasBorrar(DNI_BORRADO);

		// 4. comprobaciones: el profesor ya no aparece en la tabla...
		assertEquals("Listado de profesores", listado.getCabecera());
		assertFalse(listado.contieneProfesor(DNI_BORRADO));
		// ...y tampoco está en la base de datos
		assertFalse(profesorRepository.existsByDniIgnoreCase(DNI_BORRADO));
	}

}
