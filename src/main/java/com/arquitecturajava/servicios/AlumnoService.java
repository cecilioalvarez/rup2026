package com.arquitecturajava.servicios;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.arquitecturajava.negocio.Alumno;
import com.arquitecturajava.persistencia.AlumnoRepository;

@Service
@Transactional
public class AlumnoService {

	private final AlumnoRepository alumnoRepository;

	public AlumnoService(AlumnoRepository alumnoRepository) {
		this.alumnoRepository = alumnoRepository;
	}

	@Transactional(readOnly = true)
	public List<Alumno> buscarTodos() {
		return alumnoRepository.findAllByOrderByApellidosAscNombreAsc();
	}

	@Transactional(readOnly = true)
	public Alumno buscarPorId(Long id) {
		return alumnoRepository.findById(id).orElseThrow(() -> new AlumnoNoEncontradoException(id));
	}

	public Alumno insertar(Alumno alumno) {
		// un alta nunca debe sobrescribir un alumno existente
		alumno.setId(null);
		if (alumnoRepository.existsByDniIgnoreCase(alumno.getDni())) {
			throw new DniDuplicadoException("alumno", alumno.getDni());
		}
		return alumnoRepository.save(alumno);
	}

	public Alumno actualizar(Long id, Alumno alumno) {
		if (!alumnoRepository.existsById(id)) {
			throw new AlumnoNoEncontradoException(id);
		}
		// el propio alumno puede conservar su DNI
		if (alumnoRepository.existsByDniIgnoreCaseAndIdNot(alumno.getDni(), id)) {
			throw new DniDuplicadoException("alumno", alumno.getDni());
		}
		alumno.setId(id);
		return alumnoRepository.save(alumno);
	}

	public void borrar(Long id) {
		Alumno alumno = alumnoRepository.findById(id).orElse(null);
		if (alumno == null) {
			return;
		}
		// la FK de matricula impediría el borrado: se avisa con un error de negocio
		if (!alumno.getMatriculas().isEmpty()) {
			throw new AlumnoConMatriculasException(alumno.getNombreCompleto());
		}
		alumnoRepository.delete(alumno);
	}

}
