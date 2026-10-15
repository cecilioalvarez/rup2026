---
paths:
  - "docs/**"
---

# Documentación y diagramas

## Diagramas PlantUML en `docs/` (por carpetas; índice en `docs/README.md`)
- `casos-de-uso/diagrama-casos-de-uso.puml` — un actor ("Gestor de formación"), casos agrupados por área; los fundamentales con el estereotipo `<<fundamental>>` (azul).
- `casos-de-uso/secuencia/NN-nombre.puml` — un diagrama de secuencia por caso fundamental.
- `paquetes/diagrama-paquetes.puml` — paquetes de `com.arquitecturajava` y sus dependencias (sacadas de los imports reales).
- `clases/diagrama-clases-negocio.puml` — entidades de `negocio` con atributos, métodos `add/remove` y relaciones.
- `clases/diagrama-repositorios.puml` — jerarquía de Spring Data y los repositorios con sus consultas propias.
- `entidad-relacion/diagrama-entidad-relacion.puml` — tablas H2 tal y como las genera Hibernate (PK, FK, `NOT NULL`, `UNIQUE`, `CHECK`); `|o` si la FK admite NULL, `||` si es NOT NULL.

## Cuándo actualizarlos
- Cambia el modelo (entidad, relación, columna) → clases de negocio + entidad-relación (+ repositorios si cambian consultas).
- Cambia un flujo (controller, servicio, excepción) de un caso fundamental → su diagrama de secuencia.
- Nuevo paquete o nueva dependencia entre paquetes → diagrama de paquetes.
- Para comprobar el esquema real: ejecutar un test de integración con `-Dlogging.level.org.hibernate.SQL=DEBUG` y leer los `create table`.

## Convenciones
- Títulos y notas en español; `@startuml <nombre-del-fichero>`.
- Secuencia: `autonumber`, participantes agrupados con `box` por capa y siempre los mismos colores (Navegador gris `#F2F2F2`, controllers azul `#DDEBF7`, mapper verde `#E2F0D9`, servicios amarillo `#FFF2CC`, repositories naranja `#FCE4D6`), `alt/else` para los caminos de error, nombres de métodos reales.
- Las anotaciones JPA de cada relación van en **notas laterales** (`note left of` / `note right of`), no en `note on link`: con varias relaciones se solapan y no se leen.
- En casos de uso, las reglas de negocio van en la `legend`, no en notas sueltas (quedan lejos y se cruzan las líneas).
- Getters y setters se omiten.

## Comprobar que renderizan
Usar el jar de la extensión PlantUML de VS Code y generar los PNG en una carpeta temporal (no dentro del proyecto):

```
java -jar ~/.vscode/extensions/jebbs.plantuml-<versión>/plantuml.jar -charset UTF-8 -tpng -o <carpeta-temporal> docs/*/*.puml docs/*/*/*.puml
```

Abrir las imágenes y revisar que no haya textos solapados.
