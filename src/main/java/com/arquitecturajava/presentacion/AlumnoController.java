package com.arquitecturajava.presentacion;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.arquitecturajava.mapper.AlumnoMapper;
import com.arquitecturajava.servicios.AlumnoService;
import com.arquitecturajava.servicios.DniDuplicadoException;
import com.arquitecturajava.viewmodels.AlumnoViewModel;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/alumnos")
public class AlumnoController {

	private final AlumnoService alumnoService;

	public AlumnoController(AlumnoService alumnoService) {
		this.alumnoService = alumnoService;
	}

	@GetMapping
	public String listar(Model model) {
		model.addAttribute("alumnos", AlumnoMapper.toViewModel(alumnoService.buscarTodos()));
		return "alumnos/listado";
	}

	@GetMapping("/nuevo")
	public String formulario(Model model) {
		model.addAttribute("alumno", AlumnoViewModel.vacio());
		return mostrarFormulario(model, null);
	}

	@PostMapping
	public String insertar(@Valid @ModelAttribute("alumno") AlumnoViewModel alumno, BindingResult resultado,
			Model model) {
		if (resultado.hasErrors()) {
			return mostrarFormulario(model, null);
		}
		try {
			alumnoService.insertar(AlumnoMapper.toAlumno(alumno));
		} catch (DniDuplicadoException e) {
			resultado.rejectValue("dni", "duplicado", e.getMessage());
			return mostrarFormulario(model, null);
		}
		return "redirect:/alumnos";
	}

	@GetMapping("/{id}/editar")
	public String editar(@PathVariable Long id, Model model) {
		model.addAttribute("alumno", AlumnoMapper.toViewModel(alumnoService.buscarPorId(id)));
		return mostrarFormulario(model, id);
	}

	@PostMapping("/{id}")
	public String actualizar(@PathVariable Long id, @Valid @ModelAttribute("alumno") AlumnoViewModel alumno,
			BindingResult resultado, Model model) {
		if (resultado.hasErrors()) {
			return mostrarFormulario(model, id);
		}
		try {
			alumnoService.actualizar(id, AlumnoMapper.toAlumno(alumno.conId(id)));
		} catch (DniDuplicadoException e) {
			resultado.rejectValue("dni", "duplicado", e.getMessage());
			return mostrarFormulario(model, id);
		}
		return "redirect:/alumnos";
	}

	@PostMapping("/{id}/borrar")
	public String borrar(@PathVariable Long id) {
		alumnoService.borrar(id);
		return "redirect:/alumnos";
	}

	// el id va aparte del ViewModel: si el binding falla, el record "alumno" llega a null
	private String mostrarFormulario(Model model, Long idAlumno) {
		model.addAttribute("idAlumno", idAlumno);
		return "alumnos/formulario";
	}

}
