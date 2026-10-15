package com.arquitecturajava.e2e.paginas;

import java.time.Duration;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

// Page Object del formulario de alta y edición de profesores (/profesores/nuevo, /profesores/{id}/editar)
public class FormularioProfesorPagina extends Pagina {

	// campos del formulario localizados por su id en el HTML (th:field genera id="dni", id="nombre"...)
	@FindBy(id = "dni")
	private WebElement dni;

	@FindBy(id = "nombre")
	private WebElement nombre;

	@FindBy(id = "apellidos")
	private WebElement apellidos;

	// botón "Guardar", localizado con un selector CSS
	@FindBy(css = "button[type='submit']")
	private WebElement guardar;

	public FormularioProfesorPagina(WebDriver driver) {
		super(driver);
	}

	// los métodos "con..." escriben un campo y devuelven la misma página para encadenar llamadas
	public FormularioProfesorPagina conDni(String texto) {
		dni.sendKeys(texto);
		pausa();
		return this;
	}

	public FormularioProfesorPagina conNombre(String texto) {
		nombre.sendKeys(texto);
		pausa();
		return this;
	}

	public FormularioProfesorPagina conApellidos(String texto) {
		apellidos.sendKeys(texto);
		pausa();
		return this;
	}

	public ListadoProfesoresPagina navegarAListadoTrasGuardar() {
		guardar.click();
		// tras guardar se redirige al listado: se espera (máximo 5 s) a que la URL termine en /profesores
		new WebDriverWait(driver, Duration.ofSeconds(5)).until(ExpectedConditions.urlMatches("/profesores$"));
		ListadoProfesoresPagina listado = new ListadoProfesoresPagina(driver);
		pausa();
		return listado;
	}

}
