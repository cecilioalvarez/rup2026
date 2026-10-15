---
paths:
  - "src/test/**"
---

# Tests unitarios y de integración

## Qué se ejecuta por defecto
Para verificar cambios se ejecutan **solo** los unitarios y los de integración:

```
.\mvnw.cmd test "-Dtest=!**/e2e/**"
```

Los e2e de Selenium (`com.arquitecturajava.e2e`) solo se ejecutan cuando el usuario lo pide expresamente. Si no se van a ejecutar, basta con comprobar que compilan (`test-compile`).

## Organización
Los tests replican el paquete de la clase probada:
- `negocio/*Test` — reglas de la entidad (validaciones con `Validator`, métodos `addX/removeX`).
- `viewmodels/*ViewModelTest` — validación del record con `Validation.buildDefaultValidatorFactory()` (`@BeforeAll` / `@AfterAll`).
- `mapper/*MapperTest` — conversiones en ambos sentidos, incluidos los casos con relaciones a `null`.
- `servicios/*ServiceTest` — Mockito: `@ExtendWith(MockitoExtension.class)`, `@Mock` de cada repositorio, `@InjectMocks` del servicio; se comprueba también lo que **no** debe pasar (`verify(repo, never()).save(any())`).
- `controllers/*ControllerIntegracionTest` — controller + servicio + repositorio reales contra H2 y `data.sql`: `@SpringBootTest @AutoConfigureMockMvc @Transactional` (cada test se deshace al terminar); se usan `MockMvc`, `view().name(...)`, `model()...`, `redirectedUrl(...)`, `flash()...` y `status().isNotFound()`.

## Nombres
Métodos en español que describen el comportamiento esperado, sin prefijo `test`:
`insertarRechazaFechaFinAnteriorAInicio`, `borrarNoEliminaElProfesorConImparticionesYAvisa`, `listarDeUnaImparticionDeOtroCursoDevuelve404`.

## Datos
- Los tests de integración dependen de `data.sql`; si un test necesita un registro "limpio" (p. ej. un profesor sin imparticiones para poder borrarlo) lo crea con el repositorio dentro del propio test.
- Al cambiar firmas o `data.sql`, actualizar los tests afectados en el mismo cambio.
