package com.arquitecturajava.e2e.paginas;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

// Page Object del listado de imparticiones de un curso (/cursos/{id}/imparticiones)
public class ListadoImparticionesPagina extends Pagina {

	// título de la página: <h1>Imparticiones de [título del curso]</h1>
	@FindBy(tagName = "h1")
	private WebElement cabecera;

	// botón que lleva al formulario de alta, localizado por su texto
	@FindBy(linkText = "Nueva impartición")
	private WebElement nuevaImparticion;

	// todas las filas de la tabla de imparticiones
	@FindBy(css = "tbody tr")
	private List<WebElement> filas;

	public ListadoImparticionesPagina(WebDriver driver) {
		super(driver);
	}

	public String getCabecera() {
		return cabecera.getText();
	}

	// pulsa "Nueva impartición": el navegador cambia de página, así que se devuelve la página del formulario
	public FormularioImparticionPagina navegarANuevaImparticion() {
		nuevaImparticion.click();
		FormularioImparticionPagina formulario = new FormularioImparticionPagina(driver);
		pausa();
		return formulario;
	}

	// nombre, profesor, fecha de inicio y fecha de fin de la impartición, tal y como se muestran en la tabla
	public List<String> filaDeImparticion(String nombre) {
		// se busca la fila cuya segunda columna (Nombre) coincide
		WebElement fila = filas.stream()
				.filter(tr -> tr.findElement(By.cssSelector("td:nth-child(2)")).getText().equals(nombre))
				.findFirst()
				.orElseThrow(() -> new AssertionError("La impartición " + nombre + " no aparece en el listado"));
		// columnas de la tabla: Id, Nombre, Profesor, Fecha de inicio, Fecha de fin, Acciones -> las 4 centrales
		return fila.findElements(By.tagName("td")).stream().map(WebElement::getText).toList().subList(1, 5);
	}

}
