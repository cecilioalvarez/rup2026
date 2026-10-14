# applicacion

Aplicación Spring Boot con soporte web y persistencia JPA para la gestión de cursos.

## Tecnologías

- Java 17
- Spring Boot 4.1.1
- Spring Web MVC (Tomcat embebido)
- Spring Data JPA (Hibernate)
- H2 (base de datos en memoria)
- Maven (con wrapper `mvnw`)

## Estructura de paquetes

```
com.arquitecturajava
├── ApplicacionApplication.java    Clase principal de Spring Boot
├── negocio
│   └── Curso.java                 Entidad JPA
└── persistencia
    └── CursoRepository.java       Repositorio Spring Data JPA
```

## Modelo

### Curso

| Campo      | Tipo     | Descripción                                 |
|------------|----------|---------------------------------------------|
| `id`       | `Long`   | Clave primaria, generada por la base de datos |
| `titulo`   | `String` | Título del curso                            |
| `autor`    | `String` | Autor del curso                             |
| `duracion` | `int`    | Duración del curso (horas)                  |

## Persistencia

`CursoRepository` extiende `JpaRepository<Curso, Long>`, por lo que Spring Data
genera la implementación con las operaciones CRUD:

| Operación          | Métodos                                                        |
|--------------------|----------------------------------------------------------------|
| Crear / actualizar | `save`, `saveAll`                                              |
| Leer               | `findById`, `findAll`, `existsById`, `count`                   |
| Borrar             | `deleteById`, `delete`, `deleteAll`                            |

La jerarquía de interfaces del repositorio está en el diagrama de clases
[diagrama-repositorios.puml](diagrama-repositorios.puml).

La base de datos H2 es en memoria: Hibernate crea la tabla `curso` al arrancar
y los datos se pierden al parar la aplicación.

## Ejecución

Requiere un JDK 17 o superior.

```
.\mvnw.cmd spring-boot:run
```

La aplicación queda disponible en http://localhost:8080.

## Tests

```
.\mvnw.cmd test
```
