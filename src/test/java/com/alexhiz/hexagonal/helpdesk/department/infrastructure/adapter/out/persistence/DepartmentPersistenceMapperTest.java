package com.alexhiz.hexagonal.helpdesk.department.infrastructure.adapter.out.persistence;

import com.alexhiz.hexagonal.helpdesk.department.domain.model.Department;
import com.alexhiz.hexagonal.helpdesk.department.testdata.DepartmentMother;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para DepartmentPersistenceMapper.
 * Valida la correcta transformación bidireccional entre la entidad JPA (DepartmentEntity)
 * y el modelo de dominio hexagonal (Department).
 */
@DisplayName("DepartmentPersistenceMapper - Pruebas del Mapper de Persistencia")
class DepartmentPersistenceMapperTest {

    private DepartmentPersistenceMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new DepartmentPersistenceMapper();
    }

    @Nested
    @DisplayName("Mapeo: toDomain (DepartmentEntity -> Department)")
    class ToDomainTests {

        @Test
        @DisplayName("Paso 1: Debe mapear correctamente todos los campos de DepartmentEntity a Department")
        void shouldMapEntityToDomainCorrectly() {
            // ==========================================
            // PASO 1 (GIVEN - DADO): Entidad completa
            // ==========================================
            Long id = 5L;
            UUID publicId = UUID.randomUUID();
            LocalDateTime now = LocalDateTime.now();

            DepartmentEntity entity = DepartmentEntity.builder()
                    .id(id)
                    .publicId(publicId)
                    .name("Contabilidad")
                    .active(true)
                    .createdAt(now)
                    .updatedAt(now)
                    .deletedAt(null)
                    .build();

            // ==========================================
            // PASO 2 (WHEN - CUANDO): Mapeamos hacia dominio
            // ==========================================
            Department domain = mapper.toDomain(entity);

            // ==========================================
            // PASO 3 (THEN - ENTONCES): Verificamos todos los campos en el objeto de dominio
            // ==========================================
            assertNotNull(domain);
            assertEquals(id, domain.getId());
            assertEquals(publicId, domain.getPublicId());
            assertEquals("Contabilidad", domain.getName());
            assertTrue(domain.getActive());
            assertEquals(now, domain.getCreatedAt());
            assertEquals(now, domain.getUpdatedAt());
            assertNull(domain.getDeletedAt());
        }

        @Test
        @DisplayName("Paso 2: Debe retornar null si la entidad es nula")
        void shouldReturnNullWhenEntityIsNull() {
            // ==========================================
            // PASO 1, 2 & 3: Comprobamos el manejo seguro de nulls
            // ==========================================
            assertNull(mapper.toDomain(null));
        }
    }

    @Nested
    @DisplayName("Mapeo: toEntity (Department -> DepartmentEntity)")
    class ToEntityTests {

        @Test
        @DisplayName("Paso 1: Debe mapear correctamente todos los campos de Department a DepartmentEntity")
        void shouldMapDomainToEntityCorrectly() {
            // ==========================================
            // PASO 1 (GIVEN - DADO): Objeto de dominio completo
            // ==========================================
            Long id = 12L;
            UUID publicId = UUID.randomUUID();
            LocalDateTime now = LocalDateTime.now();

            Department domain = Department.builder()
                    .id(id)
                    .publicId(publicId)
                    .name("Seguridad")
                    .active(false)
                    .createdAt(now)
                    .updatedAt(now)
                    .deletedAt(now)
                    .build();

            // ==========================================
            // PASO 2 (WHEN - CUANDO): Mapeamos hacia entidad
            // ==========================================
            DepartmentEntity entity = mapper.toEntity(domain);

            // ==========================================
            // PASO 3 (THEN - ENTONCES): Verificamos los campos de la entidad resultante
            // ==========================================
            assertNotNull(entity);
            assertEquals(id, entity.getId());
            assertEquals(publicId, entity.getPublicId());
            assertEquals("Seguridad", entity.getName());
            assertFalse(entity.getActive());
            assertEquals(now, entity.getCreatedAt());
            assertEquals(now, entity.getUpdatedAt());
            assertEquals(now, entity.getDeletedAt());
        }

        @Test
        @DisplayName("Paso 2: Debe retornar null si el modelo de dominio es nulo")
        void shouldReturnNullWhenDomainIsNull() {
            // ==========================================
            // PASO 1, 2 & 3: Comprobamos manejo de null
            // ==========================================
            assertNull(mapper.toEntity(null));
        }
    }
}
