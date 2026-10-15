package com.arquitecturajava.mapper;

import java.util.List;

import com.arquitecturajava.negocio.Alumno;
import com.arquitecturajava.negocio.Curso;
import com.arquitecturajava.negocio.Imparticion;
import com.arquitecturajava.negocio.Matricula;
import com.arquitecturajava.viewmodels.MatriculaViewModel;

public final class MatriculaMapper {

	private MatriculaMapper() {
	}

	public static MatriculaViewModel toViewModel(Matricula matricula) {
		Alumno alumno = matricula.getAlumno();
		Imparticion imparticion = matricula.getImparticion();
		Curso curso = imparticion == null ? null : imparticion.getCurso();
		return new MatriculaViewModel(matricula.getId(), matricula.getFecha(),
				alumno == null ? null : alumno.getId(), alumno == null ? null : alumno.getNombreCompleto(),
				imparticion == null ? null : imparticion.getId(), imparticion == null ? null : imparticion.getNombre(),
				curso == null ? null : curso.getId(), curso == null ? null : curso.getTitulo());
	}

	public static List<MatriculaViewModel> toViewModel(List<Matricula> matriculas) {
		return matriculas.stream().map(MatriculaMapper::toViewModel).toList();
	}

	// el alumno y la impartición no se copian: los carga el servicio a partir de sus ids
	public static Matricula toMatricula(MatriculaViewModel vm) {
		return new Matricula(vm.id(), vm.fecha());
	}

}
