package com.arquitecturajava.mapper;

import java.util.List;

import com.arquitecturajava.negocio.Alumno;
import com.arquitecturajava.viewmodels.AlumnoViewModel;

public final class AlumnoMapper {

	private AlumnoMapper() {
	}

	public static AlumnoViewModel toViewModel(Alumno alumno) {
		return new AlumnoViewModel(alumno.getId(), alumno.getDni(), alumno.getNombre(), alumno.getApellidos(),
				alumno.getEmail());
	}

	public static List<AlumnoViewModel> toViewModel(List<Alumno> alumnos) {
		return alumnos.stream().map(AlumnoMapper::toViewModel).toList();
	}

	public static Alumno toAlumno(AlumnoViewModel vm) {
		return new Alumno(vm.id(), vm.dni(), vm.nombre(), vm.apellidos(), vm.email());
	}

}
