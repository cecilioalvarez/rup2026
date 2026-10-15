package com.arquitecturajava.e2e.paginas;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

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
		WebElement fila = buscarFila(dni)
				.orElseThrow(() -> new AssertionError("El profesor " + dni + " no aparece en el listado"));
		// columnas de la tabla: DNI, Nombre, Apellidos, Acciones -> nos quedamos con las 3 primeras
		return fila.findElements(By.tagName("td")).stream().map(WebElement::getText).toList().subList(0, 3);
	}

	public boolean contieneProfesor(String dni) {
		return buscarFila(dni).isPresent();
	}

	// pulsa "Borrar" en la fila del profesor y acepta el cuadro de confirmación del navegador
	public ListadoProfesoresPagina navegarAListadoTrasBorrar(String dni) {
		WebElement fila = buscarFila(dni)
				.orElseThrow(() -> new AssertionError("El profesor " + dni + " no aparece en el listado"));
		// el botón "Borrar" está dentro de la propia fila
		fila.findElement(By.cssSelector("button.btn-outline-danger")).click();

		// el botón lanza un confirm() de JavaScript: Selenium lo ve como una "alert" que hay que aceptar
		WebDriverWait espera = new WebDriverWait(driver, Duration.ofSeconds(5));
		espera.until(ExpectedConditions.alertIsPresent());
		pausa();
		driver.switchTo().alert().accept();

		// tras borrar se vuelve a cargar el listado: se espera a que la fila antigua desaparezca de la página
		espera.until(ExpectedConditions.stalenessOf(fila));
		ListadoProfesoresPagina listado = new ListadoProfesoresPagina(driver);
		pausa();
		return listado;
	}

	// se busca la fila cuya primera columna (DNI) coincide
	private Optional<WebElement> buscarFila(String dni) {
		return filas.stream()
				.filter(tr -> tr.findElement(By.cssSelector("td:nth-child(1)")).getText().equals(dni))
				.findFirst();
	}

}
