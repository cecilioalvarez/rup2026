package com.arquitecturajava.presentacion;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.arquitecturajava.mapper.AlumnoMapper;
import com.arquitecturajava.mapper.MatriculaMapper;
import com.arquitecturajava.servicios.AlumnoConMatriculasException;
import com.arquitecturajava.servicios.AlumnoService;
import com.arquitecturajava.servicios.DniDuplicadoException;
import com.arquitecturajava.servicios.MatriculaService;
import com.arquitecturajava.viewmodels.AlumnoViewModel;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/alumnos")
public class AlumnoController {

	private final AlumnoService alumnoService;
	private final MatriculaService matriculaService;

	public AlumnoController(AlumnoService alumnoService, MatriculaService matriculaService) {
		this.alumnoService = alumnoService;
		this.matriculaService = matriculaService;
	}

	@GetMapping
	public String listar(Model model) {
		model.addAttribute("alumnos", AlumnoMapper.toViewModel(alumnoService.buscarTodos()));
		return "alumnos/listado";
	}

	@GetMapping("/{id}/matriculas")
	public String matriculas(@PathVariable Long id, Model model) {
		model.addAttribute("alumno", AlumnoMapper.toViewModel(alumnoService.buscarPorId(id)));
		model.addAttribute("matriculas", MatriculaMapper.toViewModel(matriculaService.buscarPorAlumno(id)));
		return "alumnos/matriculas";
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
	public String borrar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
		try {
			alumnoService.borrar(id);
		} catch (AlumnoConMatriculasException e) {
			redirectAttributes.addFlashAttribute("error", e.getMessage());
		}
		return "redirect:/alumnos";
	}

	// el id va aparte del ViewModel: si el binding falla, el record "alumno" llega a null
	private String mostrarFormulario(Model model, Long idAlumno) {
		model.addAttribute("idAlumno", idAlumno);
		return "alumnos/formulario";
	}

}
