package com.arquitecturajava.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.arquitecturajava.negocio.Imparticion;
import com.arquitecturajava.negocio.Profesor;
import com.arquitecturajava.viewmodels.ImparticionViewModel;

class ImparticionMapperTest {

	@Test
	void toViewModelCopiaLosDatosDeLaImparticion() {
		Imparticion imparticion = new Imparticion(1L, "Java - Enero", LocalDate.of(2026, 1, 12),
				LocalDate.of(2026, 1, 23));
		new Profesor(2L, "87654321X", "Ana", "Peña López").addImparticion(imparticion);

		ImparticionViewModel vm = ImparticionMapper.toViewModel(imparticion);

		assertEquals(1L, vm.id());
		assertEquals("Java - Enero", vm.nombre());
		assertEquals(LocalDate.of(2026, 1, 12), vm.fechaInicio());
		assertEquals(LocalDate.of(2026, 1, 23), vm.fechaFin());
		assertEquals(2L, vm.idProfesor());
		assertEquals("Ana Peña López", vm.profesor());
	}

	@Test
	void toViewModelSinProfesorDejaLosDatosDelProfesorANull() {
		Imparticion imparticion = new Imparticion(1L, "Java - Enero", LocalDate.of(2026, 1, 12),
				LocalDate.of(2026, 1, 23));

		ImparticionViewModel vm = ImparticionMapper.toViewModel(imparticion);

		assertNull(vm.idProfesor());
		assertNull(vm.profesor());
	}

	@Test
	void toViewModelDeListaConvierteTodasLasImparticionesEnOrden() {
		List<Imparticion> imparticiones = List.of(
				new Imparticion(1L, "Enero", LocalDate.of(2026, 1, 12), LocalDate.of(2026, 1, 23)),
				new Imparticion(2L, "Marzo", LocalDate.of(2026, 3, 2), LocalDate.of(2026, 3, 13)));

		List<ImparticionViewModel> vms = ImparticionMapper.toViewModel(imparticiones);

		assertEquals(2, vms.size());
		assertEquals("Enero", vms.get(0).nombre());
		assertEquals("Marzo", vms.get(1).nombre());
	}

	@Test
	void toImparticionCopiaLosDatosAlObjetoDeNegocio() {
		ImparticionViewModel vm = new ImparticionViewModel(5L, "Docker - Mayo", LocalDate.of(2026, 5, 4),
				LocalDate.of(2026, 5, 8), 2L, "Ana Peña López");

		Imparticion imparticion = ImparticionMapper.toImparticion(vm);

		assertEquals(5L, imparticion.getId());
		assertEquals("Docker - Mayo", imparticion.getNombre());
		assertEquals(LocalDate.of(2026, 5, 4), imparticion.getFechaInicio());
		assertEquals(LocalDate.of(2026, 5, 8), imparticion.getFechaFin());
	}

}
