package com.arquitecturajava.e2e.paginas;

import java.time.Duration;

import org.openqa.selenium.WebDriver;
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

}
