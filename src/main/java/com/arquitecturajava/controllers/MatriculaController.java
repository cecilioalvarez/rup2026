package com.arquitecturajava.controllers;

import java.time.LocalDate;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.arquitecturajava.mapper.AlumnoMapper;
import com.arquitecturajava.mapper.CursoMapper;
import com.arquitecturajava.mapper.ImparticionMapper;
import com.arquitecturajava.mapper.MatriculaMapper;
import com.arquitecturajava.servicios.AlumnoService;
import com.arquitecturajava.servicios.AlumnoYaMatriculadoException;
import com.arquitecturajava.servicios.CursoService;
import com.arquitecturajava.servicios.ImparticionService;
import com.arquitecturajava.servicios.MatriculaService;
import com.arquitecturajava.viewmodels.MatriculaViewModel;

import jakarta.validation.Valid;

// el listado y el formulario de alta comparten página: se matricula desde la propia lista
@Controller
@RequestMapping("/cursos/{cursoId}/imparticiones/{imparticionId}/matriculas")
public class MatriculaController {

	private final MatriculaService matriculaService;
	private final ImparticionService imparticionService;
	private final CursoService cursoService;
	private final AlumnoService alumnoService;

	public MatriculaController(MatriculaService matriculaService, ImparticionService imparticionService,
			CursoService cursoService, AlumnoService alumnoService) {
		this.matriculaService = matriculaService;
		this.imparticionService = imparticionService;
		this.cursoService = cursoService;
		this.alumnoService = alumnoService;
	}

	@GetMapping
	public String listar(@PathVariable Long cursoId, @PathVariable Long imparticionId, Model model) {
		model.addAttribute("matricula", MatriculaViewModel.vacia(LocalDate.now()));
		return mostrarListado(cursoId, imparticionId, model);
	}

	@PostMapping
	public String matricular(@PathVariable Long cursoId, @PathVariable Long imparticionId,
			@Valid @ModelAttribute("matricula") MatriculaViewModel matricula, BindingResult resultado, Model model) {
		if (resultado.hasErrors()) {
			return mostrarListado(cursoId, imparticionId, model);
		}
		try {
			matriculaService.matricular(cursoId, imparticionId, matricula.idAlumno(),
					MatriculaMapper.toMatricula(matricula));
		} catch (AlumnoYaMatriculadoException e) {
			resultado.rejectValue("idAlumno", "duplicado", e.getMessage());
			return mostrarListado(cursoId, imparticionId, model);
		}
		return redirectListado(cursoId, imparticionId);
	}

	@PostMapping("/{id}/borrar")
	public String borrar(@PathVariable Long cursoId, @PathVariable Long imparticionId, @PathVariable Long id) {
		matriculaService.borrar(cursoId, imparticionId, id);
		return redirectListado(cursoId, imparticionId);
	}

	private String mostrarListado(Long cursoId, Long imparticionId, Model model) {
		model.addAttribute("curso", CursoMapper.toViewModel(cursoService.buscarPorId(cursoId)));
		model.addAttribute("imparticion",
				ImparticionMapper.toViewModel(imparticionService.buscarPorId(cursoId, imparticionId)));
		model.addAttribute("matriculas",
				MatriculaMapper.toViewModel(matriculaService.buscarPorImparticion(cursoId, imparticionId)));
		model.addAttribute("alumnos", AlumnoMapper.toViewModel(alumnoService.buscarTodos()));
		return "matriculas/listado";
	}

	private String redirectListado(Long cursoId, Long imparticionId) {
		return "redirect:/cursos/" + cursoId + "/imparticiones/" + imparticionId + "/matriculas";
	}

}
