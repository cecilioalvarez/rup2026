package com.arquitecturajava.e2e.paginas;

import java.time.Duration;
import java.time.LocalDate;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

// Page Object del formulario de alta y edición de imparticiones
// (/cursos/{id}/imparticiones/nueva, /cursos/{id}/imparticiones/{idImparticion}/editar)
public class FormularioImparticionPagina extends Pagina {

	// campos del formulario localizados por su id en el HTML (th:field genera id="nombre", id="fechaInicio"...)
	@FindBy(id = "nombre")
	private WebElement nombre;

	@FindBy(id = "fechaInicio")
	private WebElement fechaInicio;

	@FindBy(id = "fechaFin")
	private WebElement fechaFin;

	// desplegable <select> del profesor
	@FindBy(id = "idProfesor")
	private WebElement profesor;

	// botón "Guardar", localizado con un selector CSS
	@FindBy(css = "button[type='submit']")
	private WebElement guardar;

	public FormularioImparticionPagina(WebDriver driver) {
		super(driver);
	}

	// los métodos "con..." escriben un campo y devuelven la misma página para encadenar llamadas
	public FormularioImparticionPagina conNombre(String texto) {
		nombre.sendKeys(texto);
		pausa();
		return this;
	}

	public FormularioImparticionPagina conFechaInicio(LocalDate fecha) {
		escribirFecha(fechaInicio, fecha);
		pausa();
		return this;
	}

	public FormularioImparticionPagina conFechaFin(LocalDate fecha) {
		escribirFecha(fechaFin, fecha);
		pausa();
		return this;
	}

	// elige el profesor por el texto que se ve en el desplegable: "nombre apellidos"
	public FormularioImparticionPagina conProfesor(String nombreCompleto) {
		new Select(profesor).selectByVisibleText(nombreCompleto);
		pausa();
		return this;
	}

	public ListadoImparticionesPagina navegarAListadoTrasGuardar() {
		guardar.click();
		// tras guardar se redirige al listado: se espera (máximo 5 s) a que la URL termine en /imparticiones
		new WebDriverWait(driver, Duration.ofSeconds(5)).until(ExpectedConditions.urlMatches("/imparticiones$"));
		ListadoImparticionesPagina listado = new ListadoImparticionesPagina(driver);
		pausa();
		return listado;
	}

}
