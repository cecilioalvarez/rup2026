package com.arquitecturajava.presentacion;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.arquitecturajava.servicios.AlumnoNoEncontradoException;
import com.arquitecturajava.servicios.CursoNoEncontradoException;
import com.arquitecturajava.servicios.ImparticionNoEncontradaException;
import com.arquitecturajava.servicios.ProfesorNoEncontradoException;

import jakarta.servlet.http.HttpServletResponse;

@ControllerAdvice
public class ManejadorErrores {

	@ExceptionHandler(CursoNoEncontradoException.class)
	public void cursoNoEncontrado(CursoNoEncontradoException e, HttpServletResponse response) throws IOException {
		response.sendError(HttpStatus.NOT_FOUND.value(), e.getMessage());
	}

	@ExceptionHandler(ImparticionNoEncontradaException.class)
	public void imparticionNoEncontrada(ImparticionNoEncontradaException e, HttpServletResponse response)
			throws IOException {
		response.sendError(HttpStatus.NOT_FOUND.value(), e.getMessage());
	}

	@ExceptionHandler(ProfesorNoEncontradoException.class)
	public void profesorNoEncontrado(ProfesorNoEncontradoException e, HttpServletResponse response)
			throws IOException {
		response.sendError(HttpStatus.NOT_FOUND.value(), e.getMessage());
	}

	@ExceptionHandler(AlumnoNoEncontradoException.class)
	public void alumnoNoEncontrado(AlumnoNoEncontradoException e, HttpServletResponse response) throws IOException {
		response.sendError(HttpStatus.NOT_FOUND.value(), e.getMessage());
	}

}
