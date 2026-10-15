package com.arquitecturajava.e2e;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

import com.arquitecturajava.e2e.paginas.ListadoAlumnosPagina;
import com.arquitecturajava.e2e.paginas.ListadoCursosPagina;
import com.arquitecturajava.e2e.paginas.MatriculasPagina;
import com.arquitecturajava.negocio.Curso;
import com.arquitecturajava.negocio.Imparticion;
import com.arquitecturajava.repositories.AlumnoRepository;
import com.arquitecturajava.repositories.CursoNivelRepository;
import com.arquitecturajava.repositories.CursoRepository;
import com.arquitecturajava.repositories.ImparticionRepository;
import com.arquitecturajava.repositories.ProfesorRepository;

// Test de extremo a extremo: partiendo de un curso con una impartición activa, da de alta un alumno
// nuevo y lo matricula en esa impartición desde el navegador (Microsoft Edge con ventana visible).
// Los detalles de cada pantalla (ids, selectores, pausas) están en los Page Objects de e2e.paginas.
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DisplayName("Alta de alumno con matrícula")
class AltaAlumnoConMatriculaSeleniumTest {

	// datos del curso y la impartición de partida, y del alumno que se crea en el test;
	// sirven también para encontrarlos y borrarlos al final
	private static final String TITULO = "Selenium para Matrículas";
	private static final String IMPARTICION = "Selenium para Matrículas - Octubre";
	private static final String DNI = "55667788D";

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

	@Autowired
	private AlumnoRepository alumnoRepository;

	// el WebDriver es el "mando a distancia" del navegador: abre páginas, hace clic, escribe...
	private WebDriver driver;

	// antes de cada test: se preparan los datos de partida y se abre un navegador Edge nuevo
	@BeforeEach
	void prepararCursoConImparticionYAbrirNavegador() {
		// punto de partida: un curso con una impartición activa (empezó hace una semana y acaba dentro de un mes)
		Curso curso = cursoRepository.save(new Curso(null, TITULO, "Cecilio Álvarez", 20,
				cursoNivelRepository.findAllByOrderByOrdenAsc().get(0)));
		Imparticion imparticion = new Imparticion(null, IMPARTICION, LocalDate.now().minusDays(7),
				LocalDate.now().plusDays(30));
		imparticion.setCurso(curso);
		// toda impartición necesita un profesor: se usa el primero de data.sql
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
		// al borrar el curso se borran en cascada su impartición y las matrículas de esta
		cursoRepository.deleteAll(cursoRepository.findByTituloStartingWithIgnoreCase(TITULO));
		// sin matrículas, el alumno ya se puede borrar
		alumnoRepository.findAllByOrderByApellidosAscNombreAsc().stream()
				.filter(a -> a.getDni().equals(DNI))
				.forEach(alumnoRepository::delete);
	}

	@Test
	@DisplayName("el alumno dado de alta se matricula en la impartición y aparece entre sus matriculados")
	void altaDeAlumnoConMatriculaLoMuestraEntreLosMatriculados() {
		// 1. el usuario da de alta el alumno desde el listado de alumnos
		ListadoAlumnosPagina alumnos = ListadoAlumnosPagina.navegarAListado(driver, puerto)
				.navegarANuevoAlumno()
				.conDni(DNI)
				.conNombre("Lucía")
				.conApellidos("Vega Ruiz")
				.conEmail("lucia.vega@example.com")
				.navegarAListadoTrasGuardar();
		assertEquals(List.of(DNI, "Lucía", "Vega Ruiz", "lucia.vega@example.com"), alumnos.filaDelAlumno(DNI));

		// 2. va a los cursos, entra en las imparticiones del curso y en las matrículas de la impartición
		MatriculasPagina matriculas = ListadoCursosPagina.navegarAListado(driver, puerto)
				.navegarAImparticiones(TITULO)
				.navegarAMatriculas(IMPARTICION);
		assertEquals("Matrículas de " + IMPARTICION, matriculas.getCabecera());

		// 3. elige al alumno nuevo en el desplegable, pone la fecha de hoy y pulsa "Matricular"
		LocalDate hoy = LocalDate.now();
		matriculas = matriculas
				.conAlumno("Lucía Vega Ruiz (" + DNI + ")")
				.conFecha(hoy)
				.matricular();

		// 4. comprobación: el alumno aparece entre los matriculados con la fecha de hoy (formato dd/MM/yyyy)
		assertEquals(List.of("Lucía Vega Ruiz", hoy.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))),
				matriculas.filaDeMatricula("Lucía Vega Ruiz"));
	}

}
