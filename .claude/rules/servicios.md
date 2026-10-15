---
paths:
  - "src/main/java/**/servicios/**"
---

# Servicios

- `@Service` y `@Transactional` **a nivel de clase**; los métodos de lectura llevan `@Transactional(readOnly = true)`.
- Dependen de repositorios (no de otros servicios) inyectados por constructor.

## Operaciones
- `buscarPorId(id)`: `repository.findById(id).orElseThrow(() -> new XNoEncontradoException(id))`.
- `insertar(x)`: pone `x.setId(null)` antes de guardar (`// un alta nunca debe sobrescribir ... existente`).
- `actualizar(id, datos)`: si la entidad tiene relaciones, se **carga** y se copian los campos sobre ella para conservar sus relaciones; si no, se comprueba que existe y se guarda con `setId(id)`.
- Relaciones por id: el servicio recibe los ids (`insertar(cursoId, profesorId, imparticion)`, `matricular(cursoId, imparticionId, alumnoId, matricula)`), carga las entidades y usa los métodos `addX` de la entidad.
- Recursos anidados: buscar siempre el hijo dentro de su padre (`findByIdAndCursoId`) y lanzar la excepción "no encontrada" si no pertenece.

## Validaciones y errores
- Las reglas de negocio se comprueban en el servicio aunque el formulario ya las valide (fechas coherentes, DNI duplicado, alumno ya matriculado).
- Excepciones "no encontrado" → **404** mediante un `@ExceptionHandler` en `controllers/ManejadorErrores`; al crear una nueva, registrarla allí.
- Excepciones de negocio (`DniDuplicadoException`, `AlumnoYaMatriculadoException`, `...ConImparticiones/ConMatriculasException`) las captura el controller para mostrarlas en la vista.
