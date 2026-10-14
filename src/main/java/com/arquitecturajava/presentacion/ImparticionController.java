package com.arquitecturajava.presentacion;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.arquitecturajava.mapper.CursoMapper;
import com.arquitecturajava.mapper.ImparticionMapper;
import com.arquitecturajava.servicios.CursoService;
import com.arquitecturajava.servicios.ImparticionService;
import com.arquitecturajava.viewmodels.ImparticionViewModel;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/cursos/{cursoId}/imparticiones")
public class ImparticionController {

	private final ImparticionService imparticionService;
	private final CursoService cursoService;

	public ImparticionController(ImparticionService imparticionService, CursoService cursoService) {
		this.imparticionService = imparticionService;
		this.cursoService = cursoService;
	}

	@GetMapping
	public String listar(@PathVariable Long cursoId, Model model) {
		model.addAttribute("curso", CursoMapper.toViewModel(cursoService.buscarPorId(cursoId)));
		model.addAttribute("imparticiones",
				ImparticionMapper.toViewModel(imparticionService.buscarPorCurso(cursoId)));
		return "imparticiones/listado";
	}

	@GetMapping("/nueva")
	public String formulario(@PathVariable Long cursoId, Model model) {
		model.addAttribute("imparticion", ImparticionViewModel.vacia());
		return mostrarFormulario(cursoId, null, model);
	}

	@PostMapping
	public String insertar(@PathVariable Long cursoId,
			@Valid @ModelAttribute("imparticion") ImparticionViewModel imparticion, BindingResult resultado,
			Model model) {
		validarFechas(imparticion, resultado);
		if (resultado.hasErrors()) {
			return mostrarFormulario(cursoId, null, model);
		}
		imparticionService.insertar(cursoId, ImparticionMapper.toImparticion(imparticion));
		return "redirect:/cursos/" + cursoId + "/imparticiones";
	}

	@GetMapping("/{id}/editar")
	public String editar(@PathVariable Long cursoId, @PathVariable Long id, Model model) {
		model.addAttribute("imparticion", ImparticionMapper.toViewModel(imparticionService.buscarPorId(cursoId, id)));
		return mostrarFormulario(cursoId, id, model);
	}

	@PostMapping("/{id}")
	public String actualizar(@PathVariable Long cursoId, @PathVariable Long id,
			@Valid @ModelAttribute("imparticion") ImparticionViewModel imparticion, BindingResult resultado,
			Model model) {
		validarFechas(imparticion, resultado);
		if (resultado.hasErrors()) {
			return mostrarFormulario(cursoId, id, model);
		}
		imparticionService.actualizar(cursoId, id, ImparticionMapper.toImparticion(imparticion));
		return "redirect:/cursos/" + cursoId + "/imparticiones";
	}

	@PostMapping("/{id}/borrar")
	public String borrar(@PathVariable Long cursoId, @PathVariable Long id) {
		imparticionService.borrar(cursoId, id);
		return "redirect:/cursos/" + cursoId + "/imparticiones";
	}

	private void validarFechas(ImparticionViewModel imparticion, BindingResult resultado) {
		if (!resultado.hasErrors() && !imparticion.fechasCoherentes()) {
			resultado.rejectValue("fechaFin", "fechas", "La fecha de fin no puede ser anterior a la de inicio");
		}
	}

	// el id va aparte del ViewModel: si el binding falla, el record "imparticion" llega a null
	private String mostrarFormulario(Long cursoId, Long idImparticion, Model model) {
		model.addAttribute("curso", CursoMapper.toViewModel(cursoService.buscarPorId(cursoId)));
		model.addAttribute("idImparticion", idImparticion);
		return "imparticiones/formulario";
	}

}
