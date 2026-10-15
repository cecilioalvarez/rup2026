package com.arquitecturajava.e2e.paginas;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

// Page Object del listado de alumnos (/alumnos)
public class ListadoAlumnosPagina extends Pagina {

	// título de la página: <h1>Listado de alumnos</h1>
	@FindBy(tagName = "h1")
	private WebElement cabecera;

	// botón que lleva al formulario de alta, localizado por su texto
	@FindBy(linkText = "Nuevo alumno")
	private WebElement nuevoAlumno;

	// todas las filas de la tabla de alumnos
	@FindBy(css = "tbody tr")
	private List<WebElement> filas;

	public ListadoAlumnosPagina(WebDriver driver) {
		super(driver);
	}

	// punto de entrada: escribe la URL del listado en el navegador
	public static ListadoAlumnosPagina navegarAListado(WebDriver driver, int puerto) {
		driver.get("http://localhost:" + puerto + "/alumnos");
		ListadoAlumnosPagina pagina = new ListadoAlumnosPagina(driver);
		pagina.pausa();
		return pagina;
	}

	public String getCabecera() {
		return cabecera.getText();
	}

	// pulsa "Nuevo alumno": el navegador cambia de página, así que se devuelve la página del formulario
	public FormularioAlumnoPagina navegarANuevoAlumno() {
		nuevoAlumno.click();
		FormularioAlumnoPagina formulario = new FormularioAlumnoPagina(driver);
		pausa();
		return formulario;
	}

	// DNI, nombre, apellidos y email del alumno con ese DNI, tal y como se muestran en la tabla
	public List<String> filaDelAlumno(String dni) {
		// se busca la fila cuya primera columna (DNI) coincide
		WebElement fila = filas.stream()
				.filter(tr -> tr.findElement(By.cssSelector("td:nth-child(1)")).getText().equals(dni))
				.findFirst().orElseThrow(() -> new AssertionError("El alumno " + dni + " no aparece en el listado"));
		// columnas de la tabla: DNI, Nombre, Apellidos, Email, Acciones -> nos quedamos con las 4 primeras
		return fila.findElements(By.tagName("td")).stream().map(WebElement::getText).toList().subList(0, 4);
	}

}
