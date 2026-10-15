package com.arquitecturajava.e2e.paginas;

import java.time.Duration;
import java.time.LocalDate;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

// Base de los Page Objects: PageFactory rellena los campos anotados con @FindBy de cada página.
// Los elementos son proxies que se buscan en el navegador cada vez que se usan.
public abstract class Pagina {

	// pausa entre pasos para poder ver en el navegador cómo se rellena el formulario; 0 para ir a toda velocidad
	private static final Duration PAUSA = Duration.ofSeconds(1);

	// el mismo navegador se pasa de una página a la siguiente
	protected final WebDriver driver;

	protected Pagina(WebDriver driver) {
		this.driver = driver;
		// conecta cada campo @FindBy de la página con su elemento en el navegador
		PageFactory.initElements(driver, this);
	}

	// espera PAUSA antes de seguir con el siguiente paso
	protected void pausa() {
		try {
			Thread.sleep(PAUSA.toMillis());
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	// Los <input type="date"> se teclean en el formato del idioma del navegador (dd/mm/aaaa, mm/dd/aaaa...),
	// así que con sendKeys el test dependería del idioma. Se asigna el valor con JavaScript en el formato
	// estándar aaaa-mm-dd, que es el que el campo envía siempre al servidor.
	protected void escribirFecha(WebElement campo, LocalDate fecha) {
		((JavascriptExecutor) driver).executeScript("arguments[0].value = arguments[1];", campo, fecha.toString());
	}

}
