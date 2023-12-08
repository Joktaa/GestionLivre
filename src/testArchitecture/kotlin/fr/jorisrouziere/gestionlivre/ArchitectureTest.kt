package fr.jorisrouziere.gestionlivre

import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.library.Architectures
import org.junit.Test

class ArchitectureTest {
    val basePackage = "fr.jorisrouziere.gestionlivre"

    @Test
    fun `it should respect the clean architecture concept`() {
        val importedClass: JavaClasses = ClassFileImporter()
            .withImportOption(ImportOption.DoNotIncludeTests())
            .importPackages(basePackage)

        val rule = Architectures.layeredArchitecture().consideringAllDependencies()
                .layer("domain").definedBy("$basePackage.domain..")
                .layer("driver").definedBy("$basePackage.infrastructure.driver..")
                .layer("driven").definedBy("$basePackage.infrastructure.driven..")
                .layer("application").definedBy("$basePackage.infrastructure.application..")
                .layer("Standard API").definedBy("kotlin..", "java..", "org.jetbrains..")
                .withOptionalLayers(true)
                .whereLayer("domain").mayOnlyAccessLayers("Standard API")
                .whereLayer("driver").mayNotBeAccessedByAnyLayer()
                .whereLayer("driver").mayNotBeAccessedByAnyLayer()
        rule.check(importedClass)
    }
}