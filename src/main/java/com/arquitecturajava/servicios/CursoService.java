package com.arquitecturajava.servicios;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.arquitecturajava.negocio.Curso;
import com.arquitecturajava.negocio.CursoNivel;
import com.arquitecturajava.persistencia.CursoNivelRepository;
import com.arquitecturajava.persistencia.CursoRepository;

@Service
@Transactional
public class CursoService {

	private static final List<String> CAMPOS_BUSQUEDA = List.of("titulo", "autor", "nivel");

	private final CursoRepository cursoRepository;
	private final CursoNivelRepository cursoNivelRepository;

	public CursoService(CursoRepository cursoRepository, CursoNivelRepository cursoNivelRepository) {
		this.cursoRepository = cursoRepository;
		this.cursoNivelRepository = cursoNivelRepository;
	}

	@Transactional(readOnly = true)
	public List<CursoNivel> getNiveles() {
		return cursoNivelRepository.findAllByOrderByOrdenAsc();
	}

	@Transactional(readOnly = true)
	public List<Curso> buscarTodos() {
		return cursoRepository.findAll();
	}

	public List<String> getCamposBusqueda() {
		return CAMPOS_BUSQUEDA;
	}

	@Transactional(readOnly = true)
	public List<Curso> buscarPor(String campo, String texto) {
		if (texto == null || texto.isBlank()) {
			return buscarTodos();
		}
		String prefijo = texto.strip();
		return switch (campo) {
		case "titulo" -> cursoRepository.findByTituloStartingWithIgnoreCase(prefijo);
		case "autor" -> cursoRepository.findByAutorStartingWithIgnoreCase(prefijo);
		case "nivel" -> cursoRepository.findByNivelNombreStartingWithIgnoreCase(prefijo);
		default -> throw new IllegalArgumentException("Campo de búsqueda no válido: " + campo);
		};
	}

	@Transactional(readOnly = true)
	public Curso buscarPorId(Long id) {
		return cursoRepository.findById(id).orElseThrow(() -> new CursoNoEncontradoException(id));
	}

	public Curso insertar(Curso curso) {
		// un alta nunca debe sobrescribir un curso existente
		curso.setId(null);
		return cursoRepository.save(curso);
	}

	public Curso actualizar(Long id, Curso curso) {
		if (!cursoRepository.existsById(id)) {
			throw new CursoNoEncontradoException(id);
		}
		curso.setId(id);
		return cursoRepository.save(curso);
	}

	public void borrar(Long id) {
		cursoRepository.deleteById(id);
	}

}
