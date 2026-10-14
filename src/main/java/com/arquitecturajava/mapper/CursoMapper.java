package com.arquitecturajava.mapper;

import java.util.List;

import com.arquitecturajava.negocio.Curso;
import com.arquitecturajava.negocio.CursoNivel;
import com.arquitecturajava.viewmodels.CursoViewModel;

public final class CursoMapper {

	private CursoMapper() {
	}

	public static CursoViewModel toViewModel(Curso curso) {
		CursoNivel nivel = curso.getNivel();
		return new CursoViewModel(curso.getId(), curso.getTitulo(), curso.getAutor(), curso.getDuracion(),
				nivel == null ? null : nivel.getId(), nivel == null ? null : nivel.getNombre());
	}

	public static List<CursoViewModel> toViewModel(List<Curso> cursos) {
		return cursos.stream().map(CursoMapper::toViewModel).toList();
	}

	// basta con el id del nivel: JPA solo necesita la clave foránea para guardar el curso
	public static Curso toCurso(CursoViewModel vm) {
		CursoNivel nivel = vm.idNivel() == null ? null : new CursoNivel(vm.idNivel(), null, 0);
		return new Curso(vm.id(), vm.titulo(), vm.autor(), vm.duracion(), nivel);
	}

}
