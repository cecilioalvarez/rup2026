package com.arquitecturajava.negocio;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class Imparticion {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String nombre;
	private LocalDate fechaInicio;
	private LocalDate fechaFin;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "curso_id")
	private Curso curso;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "profesor_id", nullable = false)
	private Profesor profesor;

	// las matrículas no tienen sentido sin su impartición: se borran con ella
	@OneToMany(mappedBy = "imparticion", cascade = CascadeType.REMOVE)
	private List<Matricula> matriculas = new ArrayList<>();

	public Imparticion() {
	}

	public Imparticion(Long id, String nombre, LocalDate fechaInicio, LocalDate fechaFin) {
		this.id = id;
		this.nombre = nombre;
		this.fechaInicio = fechaInicio;
		this.fechaFin = fechaFin;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public LocalDate getFechaInicio() {
		return fechaInicio;
	}

	public void setFechaInicio(LocalDate fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	public LocalDate getFechaFin() {
		return fechaFin;
	}

	public void setFechaFin(LocalDate fechaFin) {
		this.fechaFin = fechaFin;
	}

	public Curso getCurso() {
		return curso;
	}

	public void setCurso(Curso curso) {
		this.curso = curso;
	}

	public Profesor getProfesor() {
		return profesor;
	}

	public void setProfesor(Profesor profesor) {
		this.profesor = profesor;
	}

	public List<Matricula> getMatriculas() {
		return Collections.unmodifiableList(matriculas);
	}

	public void addMatricula(Matricula matricula) {
		matriculas.add(matricula);
		matricula.setImparticion(this);
	}

	public void removeMatricula(Matricula matricula) {
		matriculas.remove(matricula);
		matricula.setImparticion(null);
	}

}
