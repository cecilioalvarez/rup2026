INSERT INTO curso_nivel (nombre, orden) VALUES ('Básico', 1);
INSERT INTO curso_nivel (nombre, orden) VALUES ('Intermedio', 2);
INSERT INTO curso_nivel (nombre, orden) VALUES ('Avanzado', 3);

INSERT INTO curso (titulo, autor, duracion, id_nivel) VALUES ('Java Básico', 'Cecilio Álvarez', 20, 1);
INSERT INTO curso (titulo, autor, duracion, id_nivel) VALUES ('Spring Boot', 'Cecilio Álvarez', 30, 2);
INSERT INTO curso (titulo, autor, duracion, id_nivel) VALUES ('Thymeleaf', 'Cecilio Álvarez', 10, 3);

INSERT INTO profesor (dni, nombre, apellidos) VALUES ('12345678Z', 'Laura', 'Martín García');
INSERT INTO profesor (dni, nombre, apellidos) VALUES ('87654321X', 'Ana', 'Peña López');

INSERT INTO imparticion (nombre, fecha_inicio, fecha_fin, curso_id, profesor_id) VALUES ('Java Básico - Enero', '2026-01-12', '2026-01-23', 1, 1);
INSERT INTO imparticion (nombre, fecha_inicio, fecha_fin, curso_id, profesor_id) VALUES ('Java Básico - Marzo', '2026-03-02', '2026-03-13', 1, 2);
INSERT INTO imparticion (nombre, fecha_inicio, fecha_fin, curso_id, profesor_id) VALUES ('Spring Boot - Febrero', '2026-02-09', '2026-02-27', 2, 1);
INSERT INTO imparticion (nombre, fecha_inicio, fecha_fin, curso_id, profesor_id) VALUES ('Thymeleaf - Abril', '2026-04-06', '2026-04-10', 3, 2);


INSERT INTO alumno (dni, nombre, apellidos, email) VALUES ('11223344B', 'Carlos', 'Díaz Romero', 'carlos.diaz@example.com');
INSERT INTO alumno (dni, nombre, apellidos, email) VALUES ('44332211C', 'Marta', 'Sánchez Gil', 'marta.sanchez@example.com');

INSERT INTO matricula (fecha, alumno_id, imparticion_id) VALUES ('2025-12-15', 1, 1);
INSERT INTO matricula (fecha, alumno_id, imparticion_id) VALUES ('2026-01-20', 1, 3);
INSERT INTO matricula (fecha, alumno_id, imparticion_id) VALUES ('2025-12-18', 2, 1);
INSERT INTO matricula (fecha, alumno_id, imparticion_id) VALUES ('2026-03-10', 2, 4);

INSERT INTO administrador (nombre, apellidos, email) VALUES ('Elena', 'Ruiz Torres', 'elena.ruiz@example.com');
INSERT INTO administrador (nombre, apellidos, email) VALUES ('Javier', 'Moreno Vidal', 'javier.moreno@example.com');
