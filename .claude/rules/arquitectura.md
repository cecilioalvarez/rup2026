# Arquitectura en capas

## Flujo
`Controller` (controllers) → `Service` (servicios) → `Repository` (repositories) → entidad (negocio)

- El **controller** solo trabaja con **ViewModels**: recibe un ViewModel del formulario, lo convierte con el mapper y llama al servicio; lo que devuelve el servicio lo convierte otra vez a ViewModel para la vista.
- Las **vistas nunca reciben entidades** JPA.
- El **servicio** contiene las reglas de negocio y las transacciones; es el único que usa los repositorios (los tests e2e son la excepción: usan repositorios para preparar y limpiar datos).
- Los **mappers** (`CursoMapper`, `ImparticionMapper`, `MatriculaMapper`...) son clases `final` con constructor privado y métodos `static`:
  - `toViewModel(entidad)` y `toViewModel(List<entidad>)`
  - `toEntidad(viewModel)` (`toCurso`, `toImparticion`...) que **no** resuelve relaciones: el servicio carga las entidades relacionadas a partir de los ids (`idProfesor`, `idAlumno`...).

## Qué tiene cada entidad gestionable
Entidad + `XRepository` + `XService` + `XController` + `XViewModel` + `XMapper` + plantillas `templates/x/listado.html` y `formulario.html` + excepción `XNoEncontradoException` registrada en `ManejadorErrores`.

## Recursos anidados
Los hijos se gestionan bajo la URL del padre y siempre se comprueba que pertenecen a él:
- `/cursos/{cursoId}/imparticiones/...`
- `/cursos/{cursoId}/imparticiones/{imparticionId}/matriculas/...`

La vista del otro lado de una relación puede ser solo de consulta (p. ej. `/alumnos/{id}/matriculas`).
