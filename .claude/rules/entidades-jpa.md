---
paths:
  - "src/main/java/**/negocio/**"
  - "src/main/java/**/persistencia/**"
  - "src/main/resources/data.sql"
---

# Entidades JPA y repositorios

## Relaciones
- Lado propietario: `@ManyToOne(fetch = FetchType.LAZY)` con `@JoinColumn(name = "x_id")`.
  - Si la relación es obligatoria: `optional = false` y `nullable = false` (p. ej. `Imparticion.profesor`, `Matricula.alumno`).
- Lado inverso: `@OneToMany(mappedBy = "...")` con:
  - lista `private List<X> xs = new ArrayList<>();`
  - getter que devuelve `Collections.unmodifiableList(xs)`
  - métodos `addX(x)` / `removeX(x)` que enlazan **los dos lados** (añaden a la lista y hacen `x.setPadre(this)`).
- Restricciones de unicidad compuestas con `@Table(uniqueConstraints = @UniqueConstraint(columnNames = {...}))` (un alumno solo una vez por impartición).

## Borrados
- `cascade = CascadeType.REMOVE` solo cuando el hijo no tiene sentido sin el padre: `Curso → Imparticion → Matricula`.
- Si no hay cascada (`Profesor → Imparticion`, `Alumno → Matricula`), el **servicio** impide borrar el padre con hijos lanzando una excepción de negocio (`ProfesorConImparticionesException`, `AlumnoConMatriculasException`) que el controller convierte en un mensaje flash.
- La cascada la aplica JPA, no la base de datos: las FK no tienen `ON DELETE CASCADE`.

## Consultas
- Consultas derivadas por nombre de método (`findByNombreContainingIgnoreCaseOrderByApellidosAscNombreAsc`).
- Los listados que muestran datos de una relación LAZY usan `@EntityGraph(attributePaths = ...)` para evitar N+1 (`ImparticionRepository.findByCursoIdOrderByFechaInicioAsc`, `MatriculaRepository`).
- Para recursos anidados, buscar el hijo por id **y** padre: `findByIdAndCursoId`, `findByIdAndImparticionId`; así no se accede a hijos de otro padre cambiando la URL.

## data.sql
- Se ejecuta después de que Hibernate cree las tablas (`spring.jpa.defer-datasource-initialization=true`).
- Insertar respetando las FK: niveles → cursos → profesores → imparticiones → alumnos → matrículas.
- Los tests de integración dependen de estos datos: al cambiarlos, revisar `*ControllerIntegracionTest` y el borrado de profesores/alumnos (los de ejemplo tienen imparticiones/matrículas y no se pueden borrar).
