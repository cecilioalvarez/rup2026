---
paths:
  - "src/test/java/**/e2e/**"
---

# Tests de extremo a extremo con Selenium

## Cuándo
- **Solo se ejecutan cuando el usuario lo pide.** Al crearlos o modificarlos, comprobar únicamente que compilan (`.\mvnw.cmd test-compile`).
- Ejecutar: `.\mvnw.cmd test -Dtest=AltasSeleniumSuite` o `-Dtest=<Clase>`.

## Estructura
- Tests en `com.arquitecturajava.e2e`, Page Objects en `com.arquitecturajava.e2e.paginas`.
- Clases de test `<Escenario>SeleniumTest` (p. ej. `AltaCursoConImparticionSeleniumTest`, `AltaAlumnoConMatriculaSeleniumTest`), con `@DisplayName` en la clase ("Alta de alumno con matrícula") y en el test ("el alumno dado de alta se matricula ...").
- `@SpringBootTest(webEnvironment = RANDOM_PORT)` + `@LocalServerPort`.
- Navegador: Microsoft Edge (`EdgeDriver`) **con ventana visible** (sin `--headless`) y `--window-size=1280,900`; Selenium Manager descarga el driver.
- **Datos de partida** ("suponiendo que ya existe un curso...") se crean con los repositorios en `@BeforeEach`; lo que hace el usuario va por el navegador.
- **Limpieza** en `@AfterEach`: `driver.quit()` y borrar con repositorios lo creado (el servidor real no hace rollback). Borrar primero el curso (cascada a imparticiones y matrículas) y después profesores/alumnos.
- Cuerpo del test con pasos numerados en comentarios sencillos (`// 1. el usuario entra en...`, `// 4. comprobaciones: ...`).

## Page Objects
- Heredan de `Pagina`: guarda el `driver`, llama a `PageFactory.initElements(driver, this)` y ofrece `pausa()` (1 s, constante `PAUSA`) y `escribirFecha(campo, fecha)`.
- Elementos con `@FindBy(id = ...)`, `linkText`, `css` o `xpath`; filas de tablas como `List<WebElement>` con `@FindBy(css = "tbody tr")`.
- **Métodos que cambian de página empiezan por `navegarA...`** y devuelven la página destino: `navegarAListado(driver, puerto)` (estático, punto de entrada), `navegarANuevoCurso()`, `navegarAImparticiones(titulo)`, `navegarAListadoTrasGuardar()`.
- Métodos que rellenan campos: `conTitulo(...)`, `conFecha(...)`… devuelven `this` para encadenar.
- Acciones que recargan la misma página no llevan `navegarA` (`MatriculasPagina.matricular()`).
- Lectura de resultados: `getCabecera()`, `filaDeX(clave)` devuelve las celdas visibles como `List<String>`.
- Cada acción llama a `pausa()` para que se vea en el navegador.

## Trucos aprendidos
- Los campos `@FindBy` son **proxies** que se vuelven a buscar en cada uso: para esperar una recarga en la misma URL guardar `driver.findElement(By.tagName("html"))` antes del clic y esperar `ExpectedConditions.stalenessOf(...)`.
- Tras guardar con redirección, esperar la URL: `new WebDriverWait(driver, Duration.ofSeconds(5)).until(ExpectedConditions.urlMatches("/cursos$"))`.
- `<input type="date">`: asignar el valor por JavaScript en ISO (`escribirFecha`), porque `sendKeys` depende del idioma del navegador.
- Campos con valor inicial (duración = 0): `clear()` antes de `sendKeys`.
- Desplegables con `new Select(elemento).selectByVisibleText(...)`.
- Si hay varios `submit` en la página, localizar el botón por su texto (`//button[text()='Matricular']`).
- `confirm()` de JavaScript: `wait.until(alertIsPresent())` y `driver.switchTo().alert().accept()`.

## Suite
- `AltasSeleniumSuite`: `@Suite`, `@SuiteDisplayName`, `@SelectClasses({...})` (dependencia `junit-platform-suite`).
- El nombre no termina en `Test` para que un `mvnw test` normal no la lance y no duplique los tests.
- Para añadir un test a la suite, añadir su clase a `@SelectClasses`.
