---
name: crear-entidad-jpa
description: Crea una entidad JPA nueva en el paquete negocio siguiendo las convenciones del proyecto (relaciones, cascadas, métodos add/remove, data.sql y test de la entidad). Usar cuando el usuario pida crear o añadir una entidad, tabla o clase de negocio JPA.
argument-hint: "<NombreEntidad> [atributos y relaciones]"
---

# Crear una entidad JPA

Crea la entidad `$ARGUMENTS` aplicando las reglas del proyecto. Antes de escribir código, lee una entidad parecida como modelo:
- con relación inversa `@OneToMany` y métodos `add/remove`: `negocio/Imparticion.java`, `negocio/Profesor.java`
- con relaciones obligatorias y restricción única compuesta: `negocio/Matricula.java`

## 1. Aclarar el modelo
Si el usuario no lo ha dicho, pregunta solo lo imprescindible:
- atributos y tipos (`String`, `int`, `LocalDate`...), cuáles son únicos (`@Column(unique = true)`)
- relaciones con entidades existentes: quién es el lado propietario (`@ManyToOne`), si es obligatoria y si el hijo se borra con el padre (cascada)

No crees repositorio, servicio, controller, ViewModel, mapper ni plantillas: eso es otro paso. Al terminar, propónlo en una línea.

## 2. Entidad en `src/main/java/com/arquitecturajava/negocio/<Entidad>.java`
- `@Entity` y `@Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;`
- Constructor vacío y constructor con `id` + atributos simples (**sin** relaciones), getters y setters.
- Si representa a una persona: `getNombreCompleto()` (nombre + " " + apellidos).
- Solo puede importar `java.*`, `jakarta.*` y otras clases de `negocio`: lo comprueba la regla ArchUnit `las_entidades_de_negocio_no_dependen_de_otros_paquetes`.

### Relaciones
- Lado propietario:
  ```java
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "padre_id")
  private Padre padre;
  ```
  Si es obligatoria: `@ManyToOne(fetch = FetchType.LAZY, optional = false)` y `@JoinColumn(name = "padre_id", nullable = false)`.
- Lado inverso (en la entidad padre, si se necesita navegar):
  ```java
  @OneToMany(mappedBy = "padre")
  private List<Hijo> hijos = new ArrayList<>();

  public List<Hijo> getHijos() {
  	return Collections.unmodifiableList(hijos);
  }

  public void addHijo(Hijo hijo) {
  	hijos.add(hijo);
  	hijo.setPadre(this);
  }

  public void removeHijo(Hijo hijo) {
  	hijos.remove(hijo);
  	hijo.setPadre(null);
  }
  ```
- Unicidad compuesta: `@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "a_id", "b_id" }))` con un comentario que explique la regla.

### Borrados
- `cascade = CascadeType.REMOVE` **solo** si el hijo no tiene sentido sin el padre, con comentario: `// las matrículas no tienen sentido sin su impartición: se borran con ella`.
- Sin cascada, comentario `// sin cascada: un profesor con imparticiones no se puede borrar`; el servicio que se cree después deberá lanzar una excepción `<Padre>Con<Hijos>Exception`.
- Nunca `ON DELETE CASCADE` en la base de datos.

## 3. Formato
- Tabuladores, línea en blanco tras abrir la clase y antes de cerrarla.
- Imports: `java.*`, después `jakarta.*` / `org.*`, al final `com.arquitecturajava.*`.
- Todo en español; comentarios cortos, en minúscula y solo para el porqué.

## 4. `src/main/resources/data.sql`
- Añadir 2-3 filas de ejemplo con nombres de columna en snake_case (`fecha_inicio`, `profesor_id`).
- Respetar el orden de las FK: niveles → cursos → profesores → imparticiones → alumnos → matrículas, colocando la nueva tabla después de sus padres.
- Si se tocan datos existentes, revisar los `*ControllerIntegracionTest` que dependen de ellos.

## 5. Test de la entidad en `src/test/java/com/arquitecturajava/negocio/<Entidad>Test.java`
Solo si la entidad tiene lógica (métodos `add/remove`, `getNombreCompleto`, validaciones). Modelo: `ImparticionTest`:
- `<padre>NuevoNoTiene<Hijos>`
- `add<Hijo>EnlazaAmbosLados`
- `remove<Hijo>DesenlazaAmbosLados`
- `laListaDe<Hijos>NoSePuedeModificarDesdeFuera`

## 6. Verificar
Solo los tests unitarios: se excluyen los e2e, los de arquitectura (`arquitectura/**`) y los de integración (`*IntegracionTest` y `ApplicacionApplicationTests`).
```
.\mvnw.cmd test "-Dtest=!**/e2e/**,!**/arquitectura/**,!**/*IntegracionTest,!ApplicacionApplicationTests"
```
No hacer commit ni push salvo que se pida.
