package com.arquitecturajava.servicios;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.arquitecturajava.negocio.Curso;
import com.arquitecturajava.negocio.Imparticion;
import com.arquitecturajava.negocio.Profesor;
import com.arquitecturajava.persistencia.CursoRepository;
import com.arquitecturajava.persistencia.ImparticionRepository;
import com.arquitecturajava.persistencia.ProfesorRepository;

@Service
@Transactional
public class ImparticionService {

	private final ImparticionRepository imparticionRepository;
	private final CursoRepository cursoRepository;
	private final ProfesorRepository profesorRepository;

	public ImparticionService(ImparticionRepository imparticionRepository, CursoRepository cursoRepository,
			ProfesorRepository profesorRepository) {
		this.imparticionRepository = imparticionRepository;
		this.cursoRepository = cursoRepository;
		this.profesorRepository = profesorRepository;
	}

	@Transactional(readOnly = true)
	public List<Imparticion> buscarPorCurso(Long cursoId) {
		if (!cursoRepository.existsById(cursoId)) {
			throw new CursoNoEncontradoException(cursoId);
		}
		return imparticionRepository.findByCursoIdOrderByFechaInicioAsc(cursoId);
	}

	public Imparticion insertar(Long cursoId, Long profesorId, Imparticion imparticion) {
		Curso curso = cursoRepository.findById(cursoId).orElseThrow(() -> new CursoNoEncontradoException(cursoId));
		Profesor profesor = buscarProfesor(profesorId);
		if (imparticion.getFechaFin().isBefore(imparticion.getFechaInicio())) {
			throw new IllegalArgumentException("La fecha de fin no puede ser anterior a la de inicio");
		}
		// un alta nunca debe sobrescribir una imparticion existente
		imparticion.setId(null);
		curso.addImparticion(imparticion);
		profesor.addImparticion(imparticion);
		return imparticionRepository.save(imparticion);
	}

	@Transactional(readOnly = true)
	public Imparticion buscarPorId(Long cursoId, Long id) {
		// se filtra también por curso para no acceder a imparticiones de otro curso desde la URL
		return imparticionRepository.findByIdAndCursoId(id, cursoId)
				.orElseThrow(() -> new ImparticionNoEncontradaException(cursoId, id));
	}

	public Imparticion actualizar(Long cursoId, Long id, Long profesorId, Imparticion datos) {
		Imparticion imparticion = buscarPorId(cursoId, id);
		Profesor profesor = buscarProfesor(profesorId);
		if (datos.getFechaFin().isBefore(datos.getFechaInicio())) {
			throw new IllegalArgumentException("La fecha de fin no puede ser anterior a la de inicio");
		}
		// se modifica la entidad cargada para conservar su relación con el curso
		imparticion.setNombre(datos.getNombre());
		imparticion.setFechaInicio(datos.getFechaInicio());
		imparticion.setFechaFin(datos.getFechaFin());
		if (imparticion.getProfesor() != profesor) {
			if (imparticion.getProfesor() != null) {
				imparticion.getProfesor().removeImparticion(imparticion);
			}
			profesor.addImparticion(imparticion);
		}
		return imparticionRepository.save(imparticion);
	}

	public void borrar(Long cursoId, Long id) {
		imparticionRepository.delete(buscarPorId(cursoId, id));
	}

	private Profesor buscarProfesor(Long profesorId) {
		return profesorRepository.findById(profesorId).orElseThrow(() -> new ProfesorNoEncontradoException(profesorId));
	}

}
