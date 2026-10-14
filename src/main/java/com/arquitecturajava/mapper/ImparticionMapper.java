package com.arquitecturajava.mapper;

import java.util.List;

import com.arquitecturajava.negocio.Imparticion;
import com.arquitecturajava.viewmodels.ImparticionViewModel;

public final class ImparticionMapper {

	private ImparticionMapper() {
	}

	public static ImparticionViewModel toViewModel(Imparticion imparticion) {
		return new ImparticionViewModel(imparticion.getId(), imparticion.getNombre(), imparticion.getFechaInicio(),
				imparticion.getFechaFin());
	}

	public static List<ImparticionViewModel> toViewModel(List<Imparticion> imparticiones) {
		return imparticiones.stream().map(ImparticionMapper::toViewModel).toList();
	}

	public static Imparticion toImparticion(ImparticionViewModel vm) {
		return new Imparticion(vm.id(), vm.nombre(), vm.fechaInicio(), vm.fechaFin());
	}

}
