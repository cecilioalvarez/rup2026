# applicacion

Aplicación web de gestión de formación: **cursos** (con nivel), sus **imparticiones** (cada una con un **profesor**) y las **matrículas** de **alumnos** en cada impartición.

## Stack
- Java 17, Spring Boot 4.1.1 (Maven con wrapper `mvnw`)
- Spring MVC + Thymeleaf, Bootstrap 5.3.3 por CDN
- Spring Data JPA (Hibernate) + H2 en memoria; datos de ejemplo en `src/main/resources/data.sql`
- Bean Validation, JUnit 6 + Mockito, Selenium 4.43 (Microsoft Edge) para e2e
- ArchUnit 1.5.1 (`archunit-junit5`) para tests de arquitectura; versión en la propiedad `archunit.version` del `pom.xml` (Spring Boot no la gestiona)

## Comandos
| Qué | Comando |
|---|---|
| Arrancar (http://localhost:8080) | `.\mvnw.cmd spring-boot:run` |
| Tests unitarios + integración (**por defecto**) | `.\mvnw.cmd test "-Dtest=!**/e2e/**"` |
| Tests e2e (solo si se pide) | `.\mvnw.cmd test -Dtest=AltasSeleniumSuite` o `-Dtest=<ClaseSeleniumTest>` |
| Compilar sin ejecutar tests | `.\mvnw.cmd test-compile` |

## Mapa del código (`com.arquitecturajava`)
- `negocio` — entidades JPA (`Curso`, `CursoNivel`, `Imparticion`, `Profesor`, `Alumno`, `Matricula`)
- `persistencia` — repositorios Spring Data
- `servicios` — lógica de negocio, transacciones y excepciones de negocio
- `presentacion` — controllers MVC, `InicioController` y `ManejadorErrores`
- `viewmodels` — records que usan las vistas y los formularios
- `mapper` — conversión estática entidad ↔ ViewModel
- `templates/<entidad>/listado.html|formulario.html`, menú común en `templates/fragmentos/menu.html`
- `src/test/.../e2e` — tests Selenium y sus Page Objects (`e2e.paginas`)
- `docs/` — diagramas PlantUML por carpetas: casos de uso + secuencia, paquetes, clases, entidad-relación (índice en `docs/README.md`)

## Reglas de oro
1. Hacer **solo lo que se pide**: al instalar algo no añadir tests ni código de ejemplo; proponer el siguiente paso en una línea.
2. Verificar con tests unitarios + integración. Los **e2e de Selenium solo se ejecutan cuando el usuario lo pide** (abren Edge con ventana y pausas).
3. **Commit y push solo cuando se pidan.**
4. Si cambia el modelo o un flujo, actualizar los diagramas de `docs/` y los datos de `data.sql`.
5. Todo en español: nombres de clases, métodos, tests, mensajes y comentarios.

## Reglas detalladas (`.claude/rules/`)
- `arquitectura.md` — capas y flujo controller → servicio → repositorio
- `estilo-codigo.md` — nombres, formato, records y comentarios
- `entidades-jpa.md` — relaciones, cascadas, consultas y `data.sql`
- `servicios.md` — transacciones, altas/actualizaciones y excepciones
- `presentacion-thymeleaf.md` — controllers, formularios y plantillas
- `tests.md` — tests unitarios y de integración
- `tests-e2e-selenium.md` — Selenium, Page Objects y suite
- `documentacion.md` — diagramas PlantUML
- `git.md` — commits, mensajes y push
