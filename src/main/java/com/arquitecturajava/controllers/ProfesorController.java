package com.arquitecturajava.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.arquitecturajava.mapper.ProfesorMapper;
import com.arquitecturajava.servicios.CampoBusquedaProfesor;
import com.arquitecturajava.servicios.DniDuplicadoException;
import com.arquitecturajava.servicios.ProfesorConImparticionesException;
import com.arquitecturajava.servicios.ProfesorService;
import com.arquitecturajava.viewmodels.ProfesorViewModel;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/profesores")
public class ProfesorController {

	private final ProfesorService profesorService;

	public ProfesorController(ProfesorService profesorService) {
		this.profesorService = profesorService;
	}

	@GetMapping
	public String listar(@RequestParam(defaultValue = "NOMBRE") CampoBusquedaProfesor campo,
			@RequestParam(defaultValue = "") String texto, Model model) {
		model.addAttribute("profesores", ProfesorMapper.toViewModel(profesorService.buscar(campo, texto)));
		model.addAttribute("campos", CampoBusquedaProfesor.values());
		model.addAttribute("campo", campo);
		model.addAttribute("texto", texto);
		return "profesores/listado";
	}

	@GetMapping("/nuevo")
	public String formulario(Model model) {
		model.addAttribute("profesor", ProfesorViewModel.vacio());
		return mostrarFormulario(model, null);
	}

	@PostMapping
	public String insertar(@Valid @ModelAttribute("profesor") ProfesorViewModel profesor, BindingResult resultado,
			Model model) {
		if (resultado.hasErrors()) {
			return mostrarFormulario(model, null);
		}
		try {
			profesorService.insertar(ProfesorMapper.toProfesor(profesor));
		} catch (DniDuplicadoException e) {
			resultado.rejectValue("dni", "duplicado", e.getMessage());
			return mostrarFormulario(model, null);
		}
		return "redirect:/profesores";
	}

	@GetMapping("/{id}/editar")
	public String editar(@PathVariable Long id, Model model) {
		model.addAttribute("profesor", ProfesorMapper.toViewModel(profesorService.buscarPorId(id)));
		return mostrarFormulario(model, id);
	}

	@PostMapping("/{id}")
	public String actualizar(@PathVariable Long id, @Valid @ModelAttribute("profesor") ProfesorViewModel profesor,
			BindingResult resultado, Model model) {
		if (resultado.hasErrors()) {
			return mostrarFormulario(model, id);
		}
		try {
			profesorService.actualizar(id, ProfesorMapper.toProfesor(profesor.conId(id)));
		} catch (DniDuplicadoException e) {
			resultado.rejectValue("dni", "duplicado", e.getMessage());
			return mostrarFormulario(model, id);
		}
		return "redirect:/profesores";
	}

	@PostMapping("/{id}/borrar")
	public String borrar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
		try {
			profesorService.borrar(id);
		} catch (ProfesorConImparticionesException e) {
			redirectAttributes.addFlashAttribute("error", e.getMessage());
		}
		return "redirect:/profesores";
	}

	// el id va aparte del ViewModel: si el binding falla, el record "profesor" llega a null
	private String mostrarFormulario(Model model, Long idProfesor) {
		model.addAttribute("idProfesor", idProfesor);
		return "profesores/formulario";
	}

}
