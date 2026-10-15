package com.arquitecturajava.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import com.arquitecturajava.negocio.Alumno;
import com.arquitecturajava.negocio.Curso;
import com.arquitecturajava.negocio.CursoNivel;
import com.arquitecturajava.negocio.Imparticion;
import com.arquitecturajava.negocio.Matricula;
import com.arquitecturajava.viewmodels.MatriculaViewModel;

class MatriculaMapperTest {

	@Test
	void toViewModelCopiaLaMatriculaConSuAlumnoImparticionYCurso() {
		Curso curso = new Curso(1L, "Java Básico", "Cecilio Álvarez", 20, new CursoNivel(1L, "Básico", 1));
		Imparticion imparticion = new Imparticion(3L, "Java - Enero", LocalDate.of(2026, 1, 12),
				LocalDate.of(2026, 1, 23));
		curso.addImparticion(imparticion);
		Matricula matricula = new Matricula(8L, LocalDate.of(2026, 1, 2));
		imparticion.addMatricula(matricula);
		new Alumno(5L, "11223344B", "Carlos", "Díaz Romero", "carlos@example.com").addMatricula(matricula);

		MatriculaViewModel vm = MatriculaMapper.toViewModel(matricula);

		assertEquals(8L, vm.id());
		assertEquals(LocalDate.of(2026, 1, 2), vm.fecha());
		assertEquals(5L, vm.idAlumno());
		assertEquals("Carlos Díaz Romero", vm.alumno());
		assertEquals(3L, vm.idImparticion());
		assertEquals("Java - Enero", vm.imparticion());
		assertEquals(1L, vm.idCurso());
		assertEquals("Java Básico", vm.curso());
	}

	@Test
	void toViewModelSinRelacionesDejaSusDatosANull() {
		MatriculaViewModel vm = MatriculaMapper.toViewModel(new Matricula(8L, LocalDate.of(2026, 1, 2)));

		assertNull(vm.alumno());
		assertNull(vm.imparticion());
		assertNull(vm.curso());
	}

	@Test
	void toMatriculaCopiaIdYFecha() {
		Matricula matricula = MatriculaMapper.toMatricula(
				new MatriculaViewModel(8L, LocalDate.of(2026, 1, 2), 5L, null, null, null, null, null));

		assertEquals(8L, matricula.getId());
		assertEquals(LocalDate.of(2026, 1, 2), matricula.getFecha());
		assertNull(matricula.getAlumno());
	}

}
