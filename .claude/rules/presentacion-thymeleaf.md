---
paths:
  - "src/main/java/**/presentacion/**"
  - "src/main/resources/templates/**"
---

# Presentación: controllers y plantillas Thymeleaf

## Controllers
- `@Controller` + `@RequestMapping("/recurso")`; rutas: `GET` listado, `GET /nuevo`, `POST` alta, `GET /{id}/editar`, `POST /{id}` actualización, `POST /{id}/borrar`.
- Método privado `mostrarFormulario(...)` que añade el id **aparte** del ViewModel (si el binding falla el record llega a null) y las listas de los desplegables (`niveles`, `profesores`, `alumnos`).
- Formularios: `@Valid @ModelAttribute("x") XViewModel x, BindingResult resultado`; validaciones cruzadas en el controller (`validarFechas`) y errores de negocio con `resultado.rejectValue("campo", "codigo", e.getMessage())`.
- Tras guardar o borrar, **PRG**: `return "redirect:/..."`.
- Errores al borrar (padre con hijos): `RedirectAttributes.addFlashAttribute("error", e.getMessage())` y en el listado `<div class="alert alert-danger" th:if="${error}" th:text="${error}">`.
- Parámetros de búsqueda manipulados en la URL no deben romper el listado (se vuelve al campo por defecto).

## Plantillas
- Bootstrap **5.3.3** por CDN (`cdn.jsdelivr.net`) en `<head>` y el bundle JS al final del `<body>`.
- Menú común: `<nav th:replace="~{fragmentos/menu :: menu('cursos')}"></nav>` justo después de `<body>`; el parámetro marca la sección activa (`cursos`, `profesores`, `alumnos` o `''` en inicio). Para cambiar el menú se edita solo `fragmentos/menu.html`.
- Páginas anidadas con `breadcrumb` (Cursos › curso › impartición).
- Formularios con `th:object` + `th:field` (generan `id` y `name`), `th:errorclass="is-invalid"` y `<div class="invalid-feedback" th:errors="*{campo}">`.
- Botones de borrar dentro de un `<form method="post">` con `onsubmit="return confirm('¿Seguro que quieres borrar ...?');"`.
- Fechas en tablas con `#temporals.format(fecha, 'dd/MM/yyyy')`; en formularios `type="date"` (ISO `yyyy-MM-dd`, `@DateTimeFormat(iso = ISO.DATE)` en el ViewModel).
- Mantener `href` estáticos junto a `th:href` para que la plantilla se pueda abrir como HTML plano.
