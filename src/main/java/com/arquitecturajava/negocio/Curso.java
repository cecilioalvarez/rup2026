package com.arquitecturajava.negocio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Max;

@Entity
public class Curso {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String titulo;
	private String autor;
	@Max(value = 200, message = "La duración no puede superar las 200 horas")
	private int duracion;
	private String nivel;

	@OneToMany(mappedBy = "curso", cascade = CascadeType.REMOVE)
	private List<Imparticion> imparticiones = new ArrayList<>();

	public Curso() {
	}

	public Curso(Long id, String titulo, String autor, int duracion, String nivel) {
		this.id = id;
		this.titulo = titulo;
		this.autor = autor;
		this.duracion = duracion;
		this.nivel = nivel;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getAutor() {
		return autor;
	}

	public void setAutor(String autor) {
		this.autor = autor;
	}

	public int getDuracion() {
		return duracion;
	}

	public void setDuracion(int duracion) {
		this.duracion = duracion;
	}

	public String getNivel() {
		return nivel;
	}

	public void setNivel(String nivel) {
		this.nivel = nivel;
	}

	public List<Imparticion> getImparticiones() {
		return Collections.unmodifiableList(imparticiones);
	}

	public void addImparticion(Imparticion imparticion) {
		imparticiones.add(imparticion);
		imparticion.setCurso(this);
	}

	public void removeImparticion(Imparticion imparticion) {
		imparticiones.remove(imparticion);
		imparticion.setCurso(null);
	}

}
