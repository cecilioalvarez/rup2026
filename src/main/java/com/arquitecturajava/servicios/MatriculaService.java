package com.arquitecturajava.servicios;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.arquitecturajava.negocio.Alumno;
import com.arquitecturajava.negocio.Imparticion;
import com.arquitecturajava.negocio.Matricula;
import com.arquitecturajava.repositories.AlumnoRepository;
import com.arquitecturajava.repositories.ImparticionRepository;
import com.arquitecturajava.repositories.MatriculaRepository;

@Service
@Transactional
public class MatriculaService {

	private final MatriculaRepository matriculaRepository;
	private final ImparticionRepository imparticionRepository;
	private final AlumnoRepository alumnoRepository;

	public MatriculaService(MatriculaRepository matriculaRepository, ImparticionRepository imparticionRepository,
			AlumnoRepository alumnoRepository) {
		this.matriculaRepository = matriculaRepository;
		this.imparticionRepository = imparticionRepository;
		this.alumnoRepository = alumnoRepository;
	}

	@Transactional(readOnly = true)
	public List<Matricula> buscarPorImparticion(Long cursoId, Long imparticionId) {
		Imparticion imparticion = buscarImparticion(cursoId, imparticionId);
		return matriculaRepository.findByImparticionIdOrderByAlumnoApellidosAscAlumnoNombreAsc(imparticion.getId());
	}

	@Transactional(readOnly = true)
	public List<Matricula> buscarPorAlumno(Long alumnoId) {
		if (!alumnoRepository.existsById(alumnoId)) {
			throw new AlumnoNoEncontradoException(alumnoId);
		}
		return matriculaRepository.findByAlumnoIdOrderByFechaDesc(alumnoId);
	}

	public Matricula matricular(Long cursoId, Long imparticionId, Long alumnoId, Matricula matricula) {
		Imparticion imparticion = buscarImparticion(cursoId, imparticionId);
		Alumno alumno = alumnoRepository.findById(alumnoId).orElseThrow(() -> new AlumnoNoEncontradoException(alumnoId));
		if (matriculaRepository.existsByAlumnoIdAndImparticionId(alumnoId, imparticionId)) {
			throw new AlumnoYaMatriculadoException(alumno.getNombreCompleto());
		}
		// un alta nunca debe sobrescribir una matrícula existente
		matricula.setId(null);
		imparticion.addMatricula(matricula);
		alumno.addMatricula(matricula);
		return matriculaRepository.save(matricula);
	}

	public void borrar(Long cursoId, Long imparticionId, Long id) {
		buscarImparticion(cursoId, imparticionId);
		// se filtra también por impartición para no borrar matrículas de otra impartición desde la URL
		Matricula matricula = matriculaRepository.findByIdAndImparticionId(id, imparticionId)
				.orElseThrow(() -> new MatriculaNoEncontradaException(imparticionId, id));
		matriculaRepository.delete(matricula);
	}

	private Imparticion buscarImparticion(Long cursoId, Long imparticionId) {
		return imparticionRepository.findByIdAndCursoId(imparticionId, cursoId)
				.orElseThrow(() -> new ImparticionNoEncontradaException(cursoId, imparticionId));
	}

}
