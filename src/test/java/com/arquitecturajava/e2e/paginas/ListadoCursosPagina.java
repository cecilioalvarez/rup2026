package com.arquitecturajava.e2e.paginas;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

// Page Object del listado de cursos (/cursos)
public class ListadoCursosPagina extends Pagina {

	// título de la página: <h1>Listado de cursos</h1>
	@FindBy(tagName = "h1")
	private WebElement cabecera;

	// botón que lleva al formulario de alta, localizado por su texto
	@FindBy(linkText = "Nuevo curso")
	private WebElement nuevoCurso;

	// todas las filas de la tabla de cursos: con List se recogen todos los elementos que coinciden
	@FindBy(css = "tbody tr")
	private List<WebElement> filas;

	public ListadoCursosPagina(WebDriver driver) {
		super(driver);
	}

	// punto de entrada: escribe la URL del listado en el navegador
	public static ListadoCursosPagina navegarAListado(WebDriver driver, int puerto) {
		driver.get("http://localhost:" + puerto + "/cursos");
		ListadoCursosPagina pagina = new ListadoCursosPagina(driver);
		pagina.pausa();
		return pagina;
	}

	public String getCabecera() {
		return cabecera.getText();
	}

	// pulsa "Nuevo curso": el navegador cambia de página, así que se devuelve la página del formulario
	public FormularioCursoPagina navegarANuevoCurso() {
		nuevoCurso.click();
		FormularioCursoPagina formulario = new FormularioCursoPagina(driver);
		pausa();
		return formulario;
	}

	// título, autor, duración y nivel del curso con ese título, tal y como se muestran en la tabla
	public List<String> filaDelCurso(String titulo) {
		WebElement fila = buscarFila(titulo);
		// columnas de la tabla: Id, Título, Autor, Duración, Nivel, Acciones -> nos quedamos con las 4 centrales
		return fila.findElements(By.tagName("td")).stream().map(WebElement::getText).toList().subList(1, 5);
	}

	// pulsa el botón "Imparticiones" de la fila del curso: el navegador pasa al listado de sus imparticiones
	public ListadoImparticionesPagina navegarAImparticiones(String titulo) {
		buscarFila(titulo).findElement(By.linkText("Imparticiones")).click();
		ListadoImparticionesPagina imparticiones = new ListadoImparticionesPagina(driver);
		pausa();
		return imparticiones;
	}

	// se busca la fila cuya segunda columna (Título) coincide
	private WebElement buscarFila(String titulo) {
		return filas.stream()
				.filter(tr -> tr.findElement(By.cssSelector("td:nth-child(2)")).getText().equals(titulo))
				.findFirst().orElseThrow(() -> new AssertionError("El curso " + titulo + " no aparece en el listado"));
	}

}
