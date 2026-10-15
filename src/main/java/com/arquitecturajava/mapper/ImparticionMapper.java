package com.arquitecturajava.mapper;

import java.util.List;

import com.arquitecturajava.negocio.Imparticion;
import com.arquitecturajava.negocio.Profesor;
import com.arquitecturajava.viewmodels.ImparticionViewModel;

public final class ImparticionMapper {

	private ImparticionMapper() {
	}

	public static ImparticionViewModel toViewModel(Imparticion imparticion) {
		Profesor profesor = imparticion.getProfesor();
		return new ImparticionViewModel(imparticion.getId(), imparticion.getNombre(), imparticion.getFechaInicio(),
				imparticion.getFechaFin(), profesor == null ? null : profesor.getId(),
				profesor == null ? null : profesor.getNombreCompleto());
	}

	public static List<ImparticionViewModel> toViewModel(List<Imparticion> imparticiones) {
		return imparticiones.stream().map(ImparticionMapper::toViewModel).toList();
	}

	// el profesor no se copia: lo carga el servicio a partir de idProfesor
	public static Imparticion toImparticion(ImparticionViewModel vm) {
		return new Imparticion(vm.id(), vm.nombre(), vm.fechaInicio(), vm.fechaFin());
	}

}
