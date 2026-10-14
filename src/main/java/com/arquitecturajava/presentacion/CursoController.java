package com.arquitecturajava.presentacion;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.arquitecturajava.mapper.CursoMapper;
import com.arquitecturajava.servicios.CursoService;
import com.arquitecturajava.viewmodels.CursoViewModel;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/cursos")
public class CursoController {

	private final CursoService cursoService;

	public CursoController(CursoService cursoService) {
		this.cursoService = cursoService;
	}

	@GetMapping
	public String listar(@RequestParam(defaultValue = "titulo") String campo,
			@RequestParam(defaultValue = "") String texto, Model model) {
		// un campo manipulado en la URL no debe romper el listado: se busca por título
		if (!cursoService.getCamposBusqueda().contains(campo)) {
			campo = "titulo";
		}
		model.addAttribute("cursos", CursoMapper.toViewModel(cursoService.buscarPor(campo, texto)));
		model.addAttribute("campo", campo);
		model.addAttribute("texto", texto);
		return "cursos/listado";
	}

	@GetMapping("/nuevo")
	public String formulario(Model model) {
		model.addAttribute("curso", CursoViewModel.vacio());
		return mostrarFormulario(model, null);
	}

	@PostMapping
	public String insertar(@Valid @ModelAttribute("curso") CursoViewModel curso, BindingResult resultado,
			Model model) {
		if (resultado.hasErrors()) {
			return mostrarFormulario(model, null);
		}
		cursoService.insertar(CursoMapper.toCurso(curso));
		return "redirect:/cursos";
	}

	@GetMapping("/{id}/editar")
	public String editar(@PathVariable Long id, Model model) {
		model.addAttribute("curso", CursoMapper.toViewModel(cursoService.buscarPorId(id)));
		return mostrarFormulario(model, id);
	}

	@PostMapping("/{id}")
	public String actualizar(@PathVariable Long id, @Valid @ModelAttribute("curso") CursoViewModel curso,
			BindingResult resultado, Model model) {
		if (resultado.hasErrors()) {
			return mostrarFormulario(model, id);
		}
		cursoService.actualizar(id, CursoMapper.toCurso(curso.conId(id)));
		return "redirect:/cursos";
	}

	@PostMapping("/{id}/borrar")
	public String borrar(@PathVariable Long id) {
		cursoService.borrar(id);
		return "redirect:/cursos";
	}

	// el id va aparte del ViewModel: si el binding falla, el record "curso" llega a null
	private String mostrarFormulario(Model model, Long idCurso) {
		model.addAttribute("idCurso", idCurso);
		model.addAttribute("niveles", cursoService.getNiveles());
		return "cursos/formulario";
	}

}
