package com.arquitecturajava.e2e.paginas;

import java.time.Duration;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

// Page Object del formulario de alta y edición de cursos (/cursos/nuevo, /cursos/{id}/editar)
public class FormularioCursoPagina extends Pagina {

	// campos del formulario localizados por su id en el HTML (th:field genera id="titulo", id="autor"...)
	@FindBy(id = "titulo")
	private WebElement titulo;

	@FindBy(id = "autor")
	private WebElement autor;

	@FindBy(id = "duracion")
	private WebElement duracion;

	// desplegable <select> del nivel
	@FindBy(id = "idNivel")
	private WebElement nivel;

	// botón "Guardar", localizado con un selector CSS
	@FindBy(css = "button[type='submit']")
	private WebElement guardar;

	public FormularioCursoPagina(WebDriver driver) {
		super(driver);
	}

	// los métodos "con..." escriben un campo y devuelven la misma página para encadenar llamadas
	public FormularioCursoPagina conTitulo(String texto) {
		// sendKeys escribe el texto como si lo tecleara el usuario
		titulo.sendKeys(texto);
		pausa();
		return this;
	}

	public FormularioCursoPagina conAutor(String texto) {
		autor.sendKeys(texto);
		pausa();
		return this;
	}

	public FormularioCursoPagina conDuracion(int horas) {
		// el formulario de alta trae la duración a 0: se vacía antes de escribir
		duracion.clear();
		duracion.sendKeys(String.valueOf(horas));
		pausa();
		return this;
	}

	public FormularioCursoPagina conNivel(String texto) {
		// Select es la ayuda de Selenium para desplegables: elige la opción por el texto que se ve
		new Select(nivel).selectByVisibleText(texto);
		pausa();
		return this;
	}

	public ListadoCursosPagina navegarAListadoTrasGuardar() {
		guardar.click();
		// tras guardar se redirige al listado: se espera (máximo 5 s) a que la URL termine en /cursos
		new WebDriverWait(driver, Duration.ofSeconds(5)).until(ExpectedConditions.urlMatches("/cursos$"));
		ListadoCursosPagina listado = new ListadoCursosPagina(driver);
		pausa();
		return listado;
	}

}
