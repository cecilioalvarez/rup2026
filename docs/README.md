# applicacion — documentación

Aplicación Spring Boot de gestión de formación: **cursos** (con nivel), sus **imparticiones** (cada una con un **profesor**) y las **matrículas** de **alumnos** en cada impartición.

## Tecnologías

- Java 17, Spring Boot 4.1.1 (Maven con wrapper `mvnw`)
- Spring Web MVC + Thymeleaf, Bootstrap 5.3.3
- Spring Data JPA (Hibernate) + H2 en memoria, datos de ejemplo en `data.sql`
- Bean Validation, JUnit 6 + Mockito, Selenium 4.43 (tests de extremo a extremo con Edge)

## Diagramas

Todos los diagramas están en PlantUML (`.puml`) y organizados por carpetas:

```
docs/
├── casos-de-uso/
│   ├── diagrama-casos-de-uso.puml        Casos de uso de la aplicación (los fundamentales en azul)
│   ├── diagrama-casos-de-uso-cursos.puml Detalle de los casos de uso de cursos
│   └── secuencia/                        Un diagrama de secuencia por caso de uso fundamental
│       ├── 01-alta-curso.puml
│       ├── 02-consultar-imparticiones.puml
│       ├── 03-alta-imparticion.puml
│       ├── 04-matricular-alumno.puml
│       └── 05-borrar-profesor.puml
├── paquetes/
│   └── diagrama-paquetes.puml            Paquetes y dependencias entre ellos
├── clases/
│   ├── diagrama-clases-negocio.puml      Entidades de negocio y sus relaciones JPA
│   └── diagrama-repositorios.puml        Jerarquía de Spring Data y repositorios
└── entidad-relacion/
    └── diagrama-entidad-relacion.puml    Tablas H2 completas (PK, FK, NOT NULL, UNIQUE, CHECK)
```

### Casos de uso y secuencia
Los diagramas de secuencia agrupan los participantes por capa con el mismo color en todos ellos, para ver de un vistazo cómo viaja una petición:

| Capa | Color | Participantes |
|---|---|---|
| Navegador | gris | plantilla Thymeleaf |
| `controllers` | azul | controllers y `ManejadorErrores` |
| `mapper` | verde | conversión entidad ↔ ViewModel |
| `servicios` | amarillo | reglas de negocio y transacciones |
| `repositories` | naranja | repositorios Spring Data |

| Caso de uso | Qué enseña |
|---|---|
| Alta de curso | flujo completo de un formulario: GET del formulario, POST, validación `@Valid`, mapper, servicio, `save` y redirección (PRG) |
| Consultar imparticiones | lectura de un recurso anidado, 404 con `ManejadorErrores` y `@EntityGraph` para evitar N+1 |
| Alta de impartición | el servicio carga curso y profesor por id y enlaza los dos lados de cada relación |
| Matricular alumno | regla de negocio (no matricular dos veces) que vuelve al formulario como error de campo |
| Borrar profesor | borrado bloqueado con excepción de negocio y mensaje flash en el listado |

### Paquetes
`controllers → servicios → repositories → negocio`, con `mapper` y `viewmodels` entre la presentación y el negocio: los controllers nunca usan entidades directamente.

### Entidad-relación
Refleja el DDL que genera Hibernate. Las cascadas de borrado (curso → imparticiones → matrículas) las aplica JPA; ninguna FK tiene `ON DELETE CASCADE`.

## Ver los diagramas

- **VS Code**: extensión *PlantUML* (jebbs.plantuml), abrir el `.puml` y `Alt+D` para la vista previa.
- **Línea de comandos** (genera PNG en otra carpeta):
  ```
  java -jar <ruta>/plantuml.jar -charset UTF-8 -tpng -o <carpeta-salida> docs/**/*.puml
  ```

## Ejecución

```
.\mvnw.cmd spring-boot:run
```

La aplicación queda disponible en http://localhost:8080. La base de datos H2 es en memoria: Hibernate crea las tablas al arrancar, `data.sql` carga los datos de ejemplo y todo se pierde al parar.

## Tests

```
.\mvnw.cmd test "-Dtest=!**/e2e/**"          unitarios + integración
.\mvnw.cmd test -Dtest=AltasSeleniumSuite     extremo a extremo (abre Edge)
```
