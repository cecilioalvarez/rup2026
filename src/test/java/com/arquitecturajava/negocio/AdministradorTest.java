package com.arquitecturajava.negocio;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class AdministradorTest {

	@Test
	void getNombreCompletoUneNombreYApellidos() {
		Administrador administrador = new Administrador(1L, "Elena", "Ruiz Torres", "elena.ruiz@example.com");

		assertEquals("Elena Ruiz Torres", administrador.getNombreCompleto());
	}

}
