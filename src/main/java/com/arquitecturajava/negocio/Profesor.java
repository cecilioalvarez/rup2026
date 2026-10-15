package com.arquitecturajava.negocio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class Profesor {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(unique = true)
	private String dni;
	private String nombre;
	private String apellidos;

	// sin cascada: un profesor con imparticiones no se puede borrar
	@OneToMany(mappedBy = "profesor")
	private List<Imparticion> imparticiones = new ArrayList<>();

	public Profesor() {
	}

	public Profesor(Long id, String dni, String nombre, String apellidos) {
		this.id = id;
		this.dni = dni;
		this.nombre = nombre;
		this.apellidos = apellidos;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDni() {
		return dni;
	}

	public void setDni(String dni) {
		this.dni = dni;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellidos() {
		return apellidos;
	}

	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}

	public String getNombreCompleto() {
		return nombre + " " + apellidos;
	}

	public List<Imparticion> getImparticiones() {
		return Collections.unmodifiableList(imparticiones);
	}

	public void addImparticion(Imparticion imparticion) {
		imparticiones.add(imparticion);
		imparticion.setProfesor(this);
	}

	public void removeImparticion(Imparticion imparticion) {
		imparticiones.remove(imparticion);
		imparticion.setProfesor(null);
	}

}
