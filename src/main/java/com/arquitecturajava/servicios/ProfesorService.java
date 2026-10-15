package com.arquitecturajava.servicios;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.arquitecturajava.negocio.Profesor;
import com.arquitecturajava.repositories.ProfesorRepository;

@Service
@Transactional
public class ProfesorService {

	private final ProfesorRepository profesorRepository;

	public ProfesorService(ProfesorRepository profesorRepository) {
		this.profesorRepository = profesorRepository;
	}

	@Transactional(readOnly = true)
	public List<Profesor> buscarTodos() {
		return profesorRepository.findAllByOrderByApellidosAscNombreAsc();
	}

	@Transactional(readOnly = true)
	public List<Profesor> buscar(CampoBusquedaProfesor campo, String texto) {
		// sin texto no hay filtro: se devuelve el listado completo
		if (campo == null || texto == null || texto.isBlank()) {
			return buscarTodos();
		}
		String filtro = texto.strip();
		return switch (campo) {
			case NOMBRE -> profesorRepository.findByNombreContainingIgnoreCaseOrderByApellidosAscNombreAsc(filtro);
			case APELLIDOS -> profesorRepository.findByApellidosContainingIgnoreCaseOrderByApellidosAscNombreAsc(filtro);
		};
	}

	@Transactional(readOnly = true)
	public Profesor buscarPorId(Long id) {
		return profesorRepository.findById(id).orElseThrow(() -> new ProfesorNoEncontradoException(id));
	}

	public Profesor insertar(Profesor profesor) {
		// un alta nunca debe sobrescribir un profesor existente
		profesor.setId(null);
		if (profesorRepository.existsByDniIgnoreCase(profesor.getDni())) {
			throw new DniDuplicadoException("profesor", profesor.getDni());
		}
		return profesorRepository.save(profesor);
	}

	public Profesor actualizar(Long id, Profesor profesor) {
		if (!profesorRepository.existsById(id)) {
			throw new ProfesorNoEncontradoException(id);
		}
		// el propio profesor puede conservar su DNI
		if (profesorRepository.existsByDniIgnoreCaseAndIdNot(profesor.getDni(), id)) {
			throw new DniDuplicadoException("profesor", profesor.getDni());
		}
		profesor.setId(id);
		return profesorRepository.save(profesor);
	}

	public void borrar(Long id) {
		Profesor profesor = profesorRepository.findById(id).orElse(null);
		if (profesor == null) {
			return;
		}
		// la FK de imparticion impediría el borrado: se avisa con un error de negocio
		if (!profesor.getImparticiones().isEmpty()) {
			throw new ProfesorConImparticionesException(profesor.getNombreCompleto());
		}
		profesorRepository.delete(profesor);
	}

}
