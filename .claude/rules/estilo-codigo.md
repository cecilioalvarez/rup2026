# Estilo de código

## Nombres (en español)
- Servicios y repositorios: `buscarTodos`, `buscarPorId`, `buscarPor...`, `insertar`, `actualizar`, `borrar`; acciones de dominio con verbo propio (`matricular`).
- Excepciones: `XNoEncontradoException`, `DniDuplicadoException`, `ProfesorConImparticionesException`... Extienden `RuntimeException`, llevan `serialVersionUID = 1L` y construyen el mensaje en español en el constructor.
- Entidades con nombre de persona exponen `getNombreCompleto()` (nombre + apellidos).

## Formato
- Indentación con **tabuladores**; línea en blanco tras la apertura de la clase y antes del cierre.
- Imports ordenados: `java.*`, librerías (`org.*`, `jakarta.*`), y al final `com.arquitecturajava.*`.
- Inyección por **constructor** con campos `private final`; nunca `@Autowired` en código de producción.

## ViewModels
- Son `record` con las anotaciones de Bean Validation y los mensajes en español.
- Factorías: `vacio()` / `vacia()` para el formulario de alta y `conId(id)` para la edición.
- Si el formulario envía un id de relación y el listado muestra su nombre, el record lleva ambos (`idNivel` + `nivel`, `idProfesor` + `profesor`).

## Comentarios
- Cortos, en minúscula, y explican **el porqué** o una decisión no evidente (`// un alta nunca debe sobrescribir un curso existente`).
- No comentar lo obvio ni repetir lo que dice el código.
- En los tests e2e sí se añaden comentarios sencillos de apoyo paso a paso (ver `tests-e2e-selenium.md`).

## Ficheros
- Los ficheros del proyecto usan CRLF en el working copy de Windows (git normaliza); `mvnw` en LF.
- No hay Python en la máquina: para editar ficheros usar las herramientas de edición, no scripts de Python.
