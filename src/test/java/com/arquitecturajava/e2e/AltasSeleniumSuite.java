package com.arquitecturajava.e2e;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

// Suite de JUnit que agrupa los tests de extremo a extremo de las altas.
// @Suite marca la clase como suite y @SelectClasses indica qué clases de test se ejecutan, en ese orden;
// para añadir más tests a la suite basta con añadir su clase a la lista.
// La clase no tiene código: solo sirve para lanzar varios tests juntos.
//
// Se ejecuta con: .\mvnw.cmd test -Dtest=AltasSeleniumSuite
// (su nombre no termina en "Test", así que un "mvnw test" normal no la lanza y no repite los tests)
@Suite
@SuiteDisplayName("Altas de extremo a extremo (Selenium)")
@SelectClasses({ AltaCursoConImparticionSeleniumTest.class })
class AltasSeleniumSuite {

}
