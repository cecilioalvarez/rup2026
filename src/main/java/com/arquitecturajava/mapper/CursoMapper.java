package com.arquitecturajava.mapper;

import java.util.List;

import com.arquitecturajava.negocio.Curso;
import com.arquitecturajava.viewmodels.CursoViewModel;

public final class CursoMapper {

	private CursoMapper() {
	}

	public static CursoViewModel toViewModel(Curso curso) {
		return new CursoViewModel(curso.getId(), curso.getTitulo(), curso.getAutor(), curso.getDuracion(),
				curso.getNivel());
	}

	public static List<CursoViewModel> toViewModel(List<Curso> cursos) {
		return cursos.stream().map(CursoMapper::toViewModel).toList();
	}

	public static Curso toCurso(CursoViewModel vm) {
		return new Curso(vm.id(), vm.titulo(), vm.autor(), vm.duracion(), vm.nivel());
	}

}
