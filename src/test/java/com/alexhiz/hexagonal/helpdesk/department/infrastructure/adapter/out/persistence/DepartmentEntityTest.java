package com.alexhiz.hexagonal.helpdesk.department.infrastructure.adapter.out.persistence;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para DepartmentEntity.
 * Valida los métodos de ciclo de vida JPA (@PrePersist y @PreUpdate)
 * y la integridad de los valores por defecto al persistir.
 */
@DisplayName("DepartmentEntity - Pruebas de la Entidad JPA y Callbacks")
class DepartmentEntityTest {

    @Nested
    @DisplayName("Callback: @PrePersist")
    class PrePersistTests {

        @Test
        @DisplayName("Paso 1: Debe autogenerar publicId, createdAt, updatedAt y default active=true si vienen nulos")
        void shouldSetDefaultValuesOnPrePersist() {
            // ==========================================
            // PASO 1 (GIVEN - DADO): Entidad recién creada sin valores por defecto
            // ==========================================
            DepartmentEntity entity = DepartmentEntity.builder()
                    .name("Operaciones")
                    .build();

            assertNull(entity.getPublicId());
            assertNull(entity.getActive());
            assertNull(entity.getCreatedAt());
            assertNull(entity.getUpdatedAt());

            // ==========================================
            // PASO 2 (WHEN - CUANDO): Se invoca el callback @PrePersist de JPA
            // ==========================================
            entity.prePersist();

            // ==========================================
            // PASO 3 (THEN - ENTONCES): Se asignan los valores automáticos
            // ==========================================
            assertNotNull(entity.getPublicId(), "El publicId debe ser generado automáticamente");
            assertTrue(entity.getActive(), "Por defecto el departamento debe ser activo");
            assertNotNull(entity.getCreatedAt(), "La fecha createdAt debe ser asignada");
            assertNotNull(entity.getUpdatedAt(), "La fecha updatedAt debe ser asignada");
        }

        @Test
        @DisplayName("Paso 2: Debe respetar publicId y active si ya fueron provistos explícitamente")
        void shouldRespectExplicitValuesOnPrePersist() {
            // ==========================================
            // PASO 1 (GIVEN - DADO): Entidad con valores explícitos
            // ==========================================
            UUID customUuid = UUID.randomUUID();
            DepartmentEntity entity = DepartmentEntity.builder()
                    .publicId(customUuid)
                    .name("Auditoría")
                    .active(false)
                    .build();

            // ==========================================
            // PASO 2 (WHEN - CUANDO): Se ejecuta prePersist
            // ==========================================
            entity.prePersist();

            // ==========================================
            // PASO 3 (THEN - ENTONCES): Se preservan los valores explícitos
            // ==========================================
            assertEquals(customUuid, entity.getPublicId());
            assertFalse(entity.getActive());
        }
    }

    @Nested
    @DisplayName("Callback: @PreUpdate")
    class PreUpdateTests {

        @Test
        @DisplayName("Paso 1: Debe actualizar el timestamp de updatedAt al ejecutar preUpdate")
        void shouldUpdateTimestampOnPreUpdate() {
            // ==========================================
            // PASO 1 (GIVEN - DADO): Entidad con updatedAt previo
            // ==========================================
            LocalDateTime oldDate = LocalDateTime.of(2020, 1, 1, 0, 0);
            DepartmentEntity entity = DepartmentEntity.builder()
                    .name("IT")
                    .updatedAt(oldDate)
                    .build();

            // ==========================================
            // PASO 2 (WHEN - CUANDO): Se ejecuta el callback @PreUpdate
            // ==========================================
            entity.preUpdate();

            // ==========================================
            // PASO 3 (THEN - ENTONCES): updatedAt se actualizó
            // ==========================================
            assertNotEquals(oldDate, entity.getUpdatedAt());
            assertTrue(entity.getUpdatedAt().isAfter(oldDate));
        }
    }
}
