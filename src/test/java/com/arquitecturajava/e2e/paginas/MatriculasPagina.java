package com.arquitecturajava.e2e.paginas;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

// Page Object de las matrículas de una impartición (/cursos/{id}/imparticiones/{idImparticion}/matriculas).
// En la misma página están el formulario "Nueva matrícula" y la tabla de alumnos matriculados.
public class MatriculasPagina extends Pagina {

	// título de la página: <h1>Matrículas de [nombre de la impartición]</h1>
	@FindBy(tagName = "h1")
	private WebElement cabecera;

	// desplegable <select> del alumno; cada opción se ve como "nombre apellidos (dni)"
	@FindBy(id = "idAlumno")
	private WebElement alumno;

	@FindBy(id = "fecha")
	private WebElement fecha;

	// botón "Matricular", localizado por su texto con XPath
	// (button[type='submit'] no vale: los botones "Borrar" de la tabla también son de tipo submit)
	@FindBy(xpath = "//button[text()='Matricular']")
	private WebElement matricular;

	// todas las filas de la tabla de matrículas
	@FindBy(css = "tbody tr")
	private List<WebElement> filas;

	public MatriculasPagina(WebDriver driver) {
		super(driver);
	}

	public String getCabecera() {
		return cabecera.getText();
	}

	// los métodos "con..." rellenan el formulario y devuelven la misma página para encadenar llamadas
	public MatriculasPagina conAlumno(String textoVisible) {
		new Select(alumno).selectByVisibleText(textoVisible);
		pausa();
		return this;
	}

	public MatriculasPagina conFecha(LocalDate dia) {
		escribirFecha(fecha, dia);
		pausa();
		return this;
	}

	// pulsa "Matricular": la aplicación guarda y vuelve a cargar esta misma página con la tabla actualizada
	public MatriculasPagina matricular() {
		// la URL no cambia al recargar, así que se guarda el <html> de la página actual para saber cuándo se ha ido.
		// Se busca con driver.findElement porque los campos @FindBy son proxies que siempre encuentran
		// el elemento de la página nueva y nunca "caducan"
		WebElement paginaAntigua = driver.findElement(By.tagName("html"));
		matricular.click();
		// se espera a que la página antigua desaparezca antes de leer la nueva
		new WebDriverWait(driver, Duration.ofSeconds(5)).until(ExpectedConditions.stalenessOf(paginaAntigua));
		MatriculasPagina recargada = new MatriculasPagina(driver);
		pausa();
		return recargada;
	}

	// alumno y fecha de matrícula de la fila de ese alumno, tal y como se muestran en la tabla
	public List<String> filaDeMatricula(String nombreCompletoAlumno) {
		// se busca la fila cuya segunda columna (Alumno) coincide
		WebElement fila = filas.stream()
				.filter(tr -> tr.findElement(By.cssSelector("td:nth-child(2)")).getText().equals(nombreCompletoAlumno))
				.findFirst()
				.orElseThrow(() -> new AssertionError(nombreCompletoAlumno + " no aparece entre los matriculados"));
		// columnas de la tabla: Id, Alumno, Fecha de matrícula, Acciones -> nos quedamos con las 2 centrales
		return fila.findElements(By.tagName("td")).stream().map(WebElement::getText).toList().subList(1, 3);
	}

}
