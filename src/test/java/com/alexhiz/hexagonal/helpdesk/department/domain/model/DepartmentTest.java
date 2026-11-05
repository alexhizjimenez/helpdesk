package com.alexhiz.hexagonal.helpdesk.department.domain.model;

import com.alexhiz.hexagonal.helpdesk.department.testdata.DepartmentMother;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Department - Pruebas Unitarias del Modelo de Dominio")
class DepartmentTest {

    @Nested
    @DisplayName("Pruebas de Creación e Instanciación")
    class CreationTests {

        @Test
        @DisplayName("Paso 1: Debe crear una instancia válida de Department usando el Builder")
        void shouldCreateDepartmentUsingBuilder() {
            // ==========================================
            // PASO 1 (GIVEN - DADO): Definimos los datos de prueba
            // ==========================================
            Long id = 10L;
            UUID publicId = UUID.randomUUID();
            String name = "Recursos Humanos";
            Boolean active = true;
            LocalDateTime now = LocalDateTime.now();

            // ==========================================
            // PASO 2 (WHEN - CUANDO): Construimos el objeto usando el patrón Builder
            // ==========================================
            Department department = Department.builder()
                    .id(id)
                    .publicId(publicId)
                    .name(name)
                    .active(active)
                    .createdAt(now)
                    .updatedAt(now)
                    .deletedAt(null)
                    .build();

            // ==========================================
            // PASO 3 (THEN - ENTONCES): Verificamos que los getters devuelvan los valores correctos
            // ==========================================
            assertNotNull(department, "El objeto Department no debe ser nulo");
            assertEquals(id, department.getId());
            assertEquals(publicId, department.getPublicId());
            assertEquals(name, department.getName());
            assertTrue(department.getActive());
            assertEquals(now, department.getCreatedAt());
            assertEquals(now, department.getUpdatedAt());
            assertNull(department.getDeletedAt());
        }

        @Test
        @DisplayName("Paso 2: Debe instanciar con constructor vacío y modificar propiedades con Setters")
        void shouldCreateDepartmentWithNoArgsConstructorAndSetters() {
            // ==========================================
            // PASO 1 (GIVEN - DADO): Inicializamos mediante constructor sin argumentos
            // ==========================================
            Department department = new Department();

            // ==========================================
            // PASO 2 (WHEN - CUANDO): Asignamos valores a través de sus Setters
            // ==========================================
            Long id = 20L;
            UUID publicId = UUID.randomUUID();
            department.setId(id);
            department.setPublicId(publicId);
            department.setName("Finanzas");
            department.setActive(false);
            LocalDateTime date = LocalDateTime.now();
            department.setCreatedAt(date);
            department.setUpdatedAt(date);
            department.setDeletedAt(date);

            // ==========================================
            // PASO 3 (THEN - ENTONCES): Verificamos que los valores asignados sean correctos
            // ==========================================
            assertEquals(id, department.getId());
            assertEquals(publicId, department.getPublicId());
            assertEquals("Finanzas", department.getName());
            assertFalse(department.getActive());
            assertEquals(date, department.getCreatedAt());
            assertEquals(date, department.getUpdatedAt());
            assertEquals(date, department.getDeletedAt());
        }

        @Test
        @DisplayName("Paso 3: Debe crear instancia completa con el constructor con todos los argumentos")
        void shouldCreateDepartmentWithAllArgsConstructor() {
            // ==========================================
            // PASO 1 (GIVEN - DADO): Preparamos valores completos
            // ==========================================
            Long id = 30L;
            UUID publicId = UUID.randomUUID();
            LocalDateTime now = LocalDateTime.now();

            // ==========================================
            // PASO 2 (WHEN - CUANDO): Usamos AllArgsConstructor
            // ==========================================
            Department department = new Department(id, publicId, "Soporte Técnico", true, now, now, null);

            // ==========================================
            // PASO 3 (THEN - ENTONCES): Comprobamos que todos los campos se asignaron
            // ==========================================
            assertEquals(id, department.getId());
            assertEquals(publicId, department.getPublicId());
            assertEquals("Soporte Técnico", department.getName());
            assertTrue(department.getActive());
            assertEquals(now, department.getCreatedAt());
            assertEquals(now, department.getUpdatedAt());
            assertNull(department.getDeletedAt());
        }
    }

    @Nested
    @DisplayName("Pruebas de Equals y HashCode")
    class EqualsAndHashCodeTests {

        @Test
        @DisplayName("Paso 1: Dos departamentos con el mismo ID deben ser iguales (EqualsAndHashCode explícito en ID)")
        void shouldBeEqualWhenSameId() {
            // ==========================================
            // PASO 1 (GIVEN - DADO): Dos departamentos con el mismo ID pero nombres y fechas distintas
            // ==========================================
            Department department1 = DepartmentMother.aDepartmentBuilder()
                    .id(1L)
                    .name("IT")
                    .build();

            Department department2 = DepartmentMother.aDepartmentBuilder()
                    .id(1L)
                    .name("Tecnología e Información") // Distinto nombre, pero mismo ID
                    .build();

            // ==========================================
            // PASO 2 & 3 (WHEN & THEN): Comprobamos igualdad y coherencia de hashCode
            // ==========================================
            assertEquals(department1, department2, "Deben ser iguales porque comparten el mismo ID");
            assertEquals(department1.hashCode(), department2.hashCode(), "El hashCode debe coincidir si son iguales");
        }

        @Test
        @DisplayName("Paso 2: Dos departamentos con distinto ID no deben ser iguales")
        void shouldNotBeEqualWhenDifferentId() {
            // ==========================================
            // PASO 1 (GIVEN - DADO): Dos departamentos con IDs distintos
            // ==========================================
            Department department1 = DepartmentMother.aDepartmentBuilder().id(1L).build();
            Department department2 = DepartmentMother.aDepartmentBuilder().id(2L).build();

            // ==========================================
            // PASO 2 & 3 (WHEN & THEN): Comprobamos que no son iguales
            // ==========================================
            assertNotEquals(department1, department2, "No deben ser iguales al tener IDs diferentes");
            assertNotEquals(department1.hashCode(), department2.hashCode());
        }

        @Test
        @DisplayName("Paso 3: Un departamento no debe ser igual a null ni a otro tipo de objeto")
        void shouldNotBeEqualToNullOrOtherClasses() {
            // ==========================================
            // PASO 1 (GIVEN - DADO): Un departamento válido
            // ==========================================
            Department department = DepartmentMother.aDepartment();

            // ==========================================
            // PASO 2 & 3 (WHEN & THEN): Comprobaciones de null y tipos incompatibles
            // ==========================================
            assertNotEquals(null, department, "Un departamento no puede ser igual a null");
            assertNotEquals("un string cualquiera", department, "No puede ser igual a un objeto de otra clase");
            assertEquals(department, department, "Debe ser igual a sí mismo (reflexivo)");
        }
    }
}
