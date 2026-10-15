# Git

- Rama de trabajo: `master`. Remoto: `remoto` (`https://github.com/cecilioalvarez/rup2026.git`).
- **Commit solo cuando el usuario lo pide**, y **push solo cuando lo pide** (`git push remoto master`).
- Antes de hacer commit, revisar `git status` y añadir solo los ficheros del cambio pedido (nada de `target/`, `HELP.md` ni ficheros ajenos).
- Normalmente un commit por petición; si los cambios de varias peticiones tocan los mismos ficheros, un único commit que describa ambas partes.
- Mensaje en español:
  - título corto que resume el cambio (`Tests e2e de alta y borrado de profesor`)
  - línea en blanco y viñetas con lo que incluye
  - línea final `Co-Authored-By` del asistente
- Los avisos `LF will be replaced by CRLF` son normales en esta máquina y no son errores.
- Antes de hacer push, indicar si hay commits anteriores aún sin subir.
