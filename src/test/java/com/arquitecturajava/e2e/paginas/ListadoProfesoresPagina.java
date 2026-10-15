package com.arquitecturajava.e2e.paginas;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

// Page Object del listado de profesores (/profesores)
public class ListadoProfesoresPagina extends Pagina {

	// título de la página: <h1>Listado de profesores</h1>
	@FindBy(tagName = "h1")
	private WebElement cabecera;

	// botón que lleva al formulario de alta, localizado por su texto
	@FindBy(linkText = "Nuevo profesor")
	private WebElement nuevoProfesor;

	// todas las filas de la tabla de profesores
	@FindBy(css = "tbody tr")
	private List<WebElement> filas;

	public ListadoProfesoresPagina(WebDriver driver) {
		super(driver);
	}

	// punto de entrada: escribe la URL del listado en el navegador
	public static ListadoProfesoresPagina navegarAListado(WebDriver driver, int puerto) {
		driver.get("http://localhost:" + puerto + "/profesores");
		ListadoProfesoresPagina pagina = new ListadoProfesoresPagina(driver);
		pagina.pausa();
		return pagina;
	}

	public String getCabecera() {
		return cabecera.getText();
	}

	// pulsa "Nuevo profesor": el navegador cambia de página, así que se devuelve la página del formulario
	public FormularioProfesorPagina navegarANuevoProfesor() {
		nuevoProfesor.click();
		FormularioProfesorPagina formulario = new FormularioProfesorPagina(driver);
		pausa();
		return formulario;
	}

	// DNI, nombre y apellidos del profesor con ese DNI, tal y como se muestran en la tabla
	public List<String> filaDelProfesor(String dni) {
		// se busca la fila cuya primera columna (DNI) coincide
		WebElement fila = filas.stream()
				.filter(tr -> tr.findElement(By.cssSelector("td:nth-child(1)")).getText().equals(dni))
				.findFirst().orElseThrow(() -> new AssertionError("El profesor " + dni + " no aparece en el listado"));
		// columnas de la tabla: DNI, Nombre, Apellidos, Acciones -> nos quedamos con las 3 primeras
		return fila.findElements(By.tagName("td")).stream().map(WebElement::getText).toList().subList(0, 3);
	}

}
