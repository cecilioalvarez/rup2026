package com.arquitecturajava.arquitectura;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

import org.springframework.data.repository.Repository;
import org.springframework.stereotype.Controller;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

// Test de arquitectura con ArchUnit: no ejecuta la aplicación, analiza el bytecode de las clases.
// @AnalyzeClasses indica qué clases se importan para comprobar las reglas: todo com.arquitecturajava
// sin las clases de test (DoNotIncludeTests), porque las reglas son sobre el código de producción.
@AnalyzeClasses(packages = "com.arquitecturajava", importOptions = ImportOption.DoNotIncludeTests.class)
class ControladoresArquitecturaTest {

	// Cada campo @ArchTest es una regla que el motor de ArchUnit para JUnit ejecuta como un test.
	// Las clases marcadas como controladores de Spring MVC deben estar en un paquete "controllers".
	// areMetaAnnotatedWith cubre tanto @Controller como las anotaciones que lo incluyen (@RestController).
	@ArchTest
	static final ArchRule los_controladores_estan_en_el_paquete_controllers = classes()
			.that().areMetaAnnotatedWith(Controller.class)
			.should().resideInAPackage("..controllers..")
			.because("los controladores de Spring MVC se agrupan en un paquete propio de controllers");

	// Las entidades de negocio no dependen de ningún otro paquete de la aplicación:
	// solo pueden usar otras clases de negocio, el JDK (java..) y las anotaciones de JPA y Bean Validation (jakarta..).
	@ArchTest
	static final ArchRule las_entidades_de_negocio_no_dependen_de_otros_paquetes = classes()
			.that().resideInAPackage("..negocio..")
			.should().onlyDependOnClassesThat().resideInAnyPackage("..negocio..", "java..", "jakarta..")
			.because("el negocio es el núcleo de la aplicación y no debe conocer controllers, servicios, repositories, mappers ni viewmodels");

	// Los repositorios de Spring Data deben estar en un paquete "repositories".
	// areAssignableTo(Repository.class) cubre cualquier interfaz que herede de JpaRepository, CrudRepository...
	@ArchTest
	static final ArchRule los_repositorios_estan_en_el_paquete_repositories = classes()
			.that().areAssignableTo(Repository.class)
			.should().resideInAPackage("..repositories..")
			.because("los repositorios de Spring Data se agrupan en un paquete propio de repositories");

}
