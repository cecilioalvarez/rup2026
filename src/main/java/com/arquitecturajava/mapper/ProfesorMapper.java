package com.arquitecturajava.mapper;

import java.util.List;

import com.arquitecturajava.negocio.Profesor;
import com.arquitecturajava.viewmodels.ProfesorViewModel;

public final class ProfesorMapper {

	private ProfesorMapper() {
	}

	public static ProfesorViewModel toViewModel(Profesor profesor) {
		return new ProfesorViewModel(profesor.getId(), profesor.getDni(), profesor.getNombre(),
				profesor.getApellidos());
	}

	public static List<ProfesorViewModel> toViewModel(List<Profesor> profesores) {
		return profesores.stream().map(ProfesorMapper::toViewModel).toList();
	}

	public static Profesor toProfesor(ProfesorViewModel vm) {
		return new Profesor(vm.id(), vm.dni(), vm.nombre(), vm.apellidos());
	}

}
