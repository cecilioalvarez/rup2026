package com.arquitecturajava.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.arquitecturajava.negocio.Curso;
import com.arquitecturajava.viewmodels.CursoViewModel;

class CursoMapperTest {

	@Test
	void toViewModelCopiaLosDatosDelCurso() {
		Curso curso = new Curso(1L, "Java Básico", "Cecilio Álvarez", 20, "Básico");

		CursoViewModel vm = CursoMapper.toViewModel(curso);

		assertEquals(1L, vm.id());
		assertEquals("Java Básico", vm.titulo());
		assertEquals("Cecilio Álvarez", vm.autor());
		assertEquals(20, vm.duracion());
		assertEquals("Básico", vm.nivel());
	}

	@Test
	void toViewModelDeListaConvierteTodosLosCursosEnOrden() {
		List<Curso> cursos = List.of(new Curso(1L, "Java", "A", 20, "Básico"),
				new Curso(2L, "Spring", "B", 30, "Intermedio"));

		List<CursoViewModel> vms = CursoMapper.toViewModel(cursos);

		assertEquals(2, vms.size());
		assertEquals("Java", vms.get(0).titulo());
		assertEquals("Spring", vms.get(1).titulo());
	}

	@Test
	void toCursoCopiaLosDatosAlObjetoDeNegocio() {
		CursoViewModel vm = new CursoViewModel(2L, "Spring Boot", "Ana Peña", 30, "Intermedio");

		Curso curso = CursoMapper.toCurso(vm);

		assertEquals(2L, curso.getId());
		assertEquals("Spring Boot", curso.getTitulo());
		assertEquals("Ana Peña", curso.getAutor());
		assertEquals(30, curso.getDuracion());
		assertEquals("Intermedio", curso.getNivel());
	}

}
