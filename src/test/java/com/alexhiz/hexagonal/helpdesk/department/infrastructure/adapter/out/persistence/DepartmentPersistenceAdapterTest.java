package com.alexhiz.hexagonal.helpdesk.department.infrastructure.adapter.out.persistence;

import com.alexhiz.hexagonal.helpdesk.department.domain.exception.DepartmentNotFoundException;
import com.alexhiz.hexagonal.helpdesk.department.domain.model.Department;
import com.alexhiz.hexagonal.helpdesk.department.testdata.DepartmentMother;
import com.alexhiz.hexagonal.helpdesk.shared.domain.model.PageQuery;
import com.alexhiz.hexagonal.helpdesk.shared.domain.model.PageResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para DepartmentPersistenceAdapter (Adaptador de Persistencia Outbound).
 * Valida la correcta interacción con Spring Data JPA (DepartmentRepository) y
 * la transformación de modelos de dominio hacia entidades y viceversa.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("DepartmentPersistenceAdapter - Pruebas del Adaptador de Persistencia")
class DepartmentPersistenceAdapterTest {

    @Mock
    private DepartmentRepository departmentRepository;

    @Spy
    private DepartmentPersistenceMapper departmentPersistenceMapper = new DepartmentPersistenceMapper();

    @InjectMocks
    private DepartmentPersistenceAdapter departmentPersistenceAdapter;

    @Nested
    @DisplayName("Método: save")
    class SaveTests {

        @Test
        @DisplayName("Paso 1: Debe mapear el modelo de dominio a entidad, guardarlo en BD y retornar el dominio persistido")
        void shouldSaveAndReturnDomainDepartment() {
            // ==========================================
            // PASO 1 (GIVEN - DADO): Modelo de dominio y entidad guardada simulada
            // ==========================================
            Department domain = DepartmentMother.aDepartmentNamed("Sistemas");
            DepartmentEntity savedEntity = DepartmentMother.aDepartmentEntityBuilder()
                    .name("Sistemas")
                    .build();

            when(departmentRepository.save(any(DepartmentEntity.class))).thenReturn(savedEntity);

            // ==========================================
            // PASO 2 (WHEN - CUANDO): Invocamos save
            // ==========================================
            Department result = departmentPersistenceAdapter.save(domain);

            // ==========================================
            // PASO 3 (THEN - ENTONCES): Verificaciones
            // ==========================================
            assertNotNull(result);
            assertEquals("Sistemas", result.getName());
            verify(departmentRepository, times(1)).save(any(DepartmentEntity.class));
            verify(departmentPersistenceMapper, times(1)).toEntity(domain);
            verify(departmentPersistenceMapper, times(1)).toDomain(savedEntity);
        }
    }

    @Nested
    @DisplayName("Método: findAll")
    class FindAllTests {

        @Test
        @DisplayName("Paso 1: Debe retornar la lista completa de departamentos convertidos a dominio")
        void shouldReturnAllDepartmentsAsDomainList() {
            // ==========================================
            // PASO 1 (GIVEN - DADO): Entidades retornadas por la BD
            // ==========================================
            DepartmentEntity entity1 = DepartmentMother.aDepartmentEntityBuilder().name("IT").build();
            DepartmentEntity entity2 = DepartmentMother.aDepartmentEntityBuilder().name("RRHH").build();

            when(departmentRepository.findAll()).thenReturn(List.of(entity1, entity2));

            // ==========================================
            // PASO 2 (WHEN - CUANDO): Invocamos findAll
            // ==========================================
            List<Department> results = departmentPersistenceAdapter.findAll();

            // ==========================================
            // PASO 3 (THEN - ENTONCES): Verificamos el tamaño y los datos mapeados
            // ==========================================
            assertEquals(2, results.size());
            assertEquals("IT", results.get(0).getName());
            assertEquals("RRHH", results.get(1).getName());
            verify(departmentRepository, times(1)).findAll();
        }
    }

    @Nested
    @DisplayName("Método: findById")
    class FindByIdTests {

        @Test
        @DisplayName("Paso 1: Debe retornar Optional con el departamento cuando existe por publicId")
        void shouldReturnDepartmentWhenFoundByPublicId() {
            // ==========================================
            // PASO 1 (GIVEN - DADO): Entidad encontrada en el repositorio por publicId
            // ==========================================
            UUID publicId = UUID.randomUUID();
            DepartmentEntity entity = DepartmentMother.aDepartmentEntityBuilder().publicId(publicId).build();

            when(departmentRepository.findByPublicId(publicId)).thenReturn(Optional.of(entity));

            // ==========================================
            // PASO 2 (WHEN - CUANDO): Buscamos por ID
            // ==========================================
            Optional<Department> result = departmentPersistenceAdapter.findById(publicId);

            // ==========================================
            // PASO 3 (THEN - ENTONCES): El resultado está presente y contiene el publicId
            // ==========================================
            assertTrue(result.isPresent());
            assertEquals(publicId, result.get().getPublicId());
            verify(departmentRepository, times(1)).findByPublicId(publicId);
        }

        @Test
        @DisplayName("Paso 2: Debe retornar Optional.empty() cuando no existe el publicId")
        void shouldReturnEmptyWhenNotFoundByPublicId() {
            // ==========================================
            // PASO 1 (GIVEN - DADO): Repositorio no encuentra la entidad
            // ==========================================
            UUID publicId = UUID.randomUUID();
            when(departmentRepository.findByPublicId(publicId)).thenReturn(Optional.empty());

            // ==========================================
            // PASO 2 (WHEN - CUANDO): Consultamos
            // ==========================================
            Optional<Department> result = departmentPersistenceAdapter.findById(publicId);

            // ==========================================
            // PASO 3 (THEN - ENTONCES): Resultado vacío
            // ==========================================
            assertTrue(result.isEmpty());
            verify(departmentRepository, times(1)).findByPublicId(publicId);
        }
    }

    @Nested
    @DisplayName("Métodos de existencia: existsById, existsByName, existsByNameAndIdNot")
    class ExistsTests {

        @Test
        @DisplayName("Paso 1: existsById debe delegar en departmentRepository.existsByPublicId")
        void shouldDelegateExistsById() {
            UUID id = UUID.randomUUID();
            when(departmentRepository.existsByPublicId(id)).thenReturn(true);

            boolean exists = departmentPersistenceAdapter.existsById(id);

            assertTrue(exists);
            verify(departmentRepository, times(1)).existsByPublicId(id);
        }

        @Test
        @DisplayName("Paso 2: existsByName debe delegar en departmentRepository.existsByName")
        void shouldDelegateExistsByName() {
            when(departmentRepository.existsByName("IT")).thenReturn(true);

            boolean exists = departmentPersistenceAdapter.existsByName("IT");

            assertTrue(exists);
            verify(departmentRepository, times(1)).existsByName("IT");
        }

        @Test
        @DisplayName("Paso 3: existsByNameAndIdNot debe delegar en departmentRepository.existsByNameAndPublicIdNot")
        void shouldDelegateExistsByNameAndIdNot() {
            UUID id = UUID.randomUUID();
            when(departmentRepository.existsByNameAndPublicIdNot("Ventas", id)).thenReturn(false);

            boolean exists = departmentPersistenceAdapter.existsByNameAndIdNot("Ventas", id);

            assertFalse(exists);
            verify(departmentRepository, times(1)).existsByNameAndPublicIdNot("Ventas", id);
        }
    }

    @Nested
    @DisplayName("Método: delete (Eliminación Lógica / Soft Delete)")
    class DeleteTests {

        @Test
        @DisplayName("Paso 1: Debe marcar active = false y deletedAt con la fecha actual al eliminar lógicamente")
        void shouldSoftDeleteDepartmentSuccessfully() {
            // ==========================================
            // PASO 1 (GIVEN - DADO): Entidad activa encontrada en BD
            // ==========================================
            UUID id = UUID.randomUUID();
            DepartmentEntity entity = DepartmentMother.aDepartmentEntityBuilder()
                    .publicId(id)
                    .active(true)
                    .deletedAt(null)
                    .build();

            when(departmentRepository.findByPublicId(id)).thenReturn(Optional.of(entity));

            // ==========================================
            // PASO 2 (WHEN - CUANDO): Invocamos delete
            // ==========================================
            departmentPersistenceAdapter.delete(id);

            // ==========================================
            // PASO 3 (THEN - ENTONCES):
            // Verificamos que se actualizó el estado a inactivo, se estableció deletedAt y se guardó
            // ==========================================
            ArgumentCaptor<DepartmentEntity> captor = ArgumentCaptor.forClass(DepartmentEntity.class);
            verify(departmentRepository, times(1)).saveAndFlush(captor.capture());

            DepartmentEntity capturedEntity = captor.getValue();
            assertFalse(capturedEntity.getActive(), "El departamento debe quedar inactivo");
            assertNotNull(capturedEntity.getDeletedAt(), "La fecha de eliminación deletedAt debe estar asignada");
        }

        @Test
        @DisplayName("Paso 2: Debe lanzar DepartmentNotFoundException si no existe el publicId a eliminar")
        void shouldThrowExceptionWhenDeletingNonExistentEntity() {
            // ==========================================
            // PASO 1 (GIVEN - DADO): Entidad no existe
            // ==========================================
            UUID id = UUID.randomUUID();
            when(departmentRepository.findByPublicId(id)).thenReturn(Optional.empty());

            // ==========================================
            // PASO 2 & 3 (WHEN & THEN): Comprobamos que lance la excepción
            // ==========================================
            assertThrows(DepartmentNotFoundException.class, () -> departmentPersistenceAdapter.delete(id));
            verify(departmentRepository, never()).saveAndFlush(any());
        }
    }

    @Nested
    @DisplayName("Método: findAllPages")
    class FindAllPagesTests {

        @Test
        @DisplayName("Paso 1: Debe consultar con Pageable y retornar PageResult con entidades mapeadas a dominio")
        void shouldReturnPageResultWithMappedDepartments() {
            // ==========================================
            // PASO 1 (GIVEN - DADO): PageQuery de página 0, tamaño 10
            // ==========================================
            PageQuery query = new PageQuery(0, 10);
            Pageable pageable = PageRequest.of(0, 10);

            DepartmentEntity entity = DepartmentMother.aDepartmentEntityBuilder().name("Operaciones").build();
            Page<DepartmentEntity> entityPage = new PageImpl<>(List.of(entity), pageable, 1);

            when(departmentRepository.findAll(pageable)).thenReturn(entityPage);

            // ==========================================
            // PASO 2 (WHEN - CUANDO): Ejecutamos la consulta paginada
            // ==========================================
            PageResult<Department> result = departmentPersistenceAdapter.findAllPages(query);

            // ==========================================
            // PASO 3 (THEN - ENTONCES): Verificamos los metadatos y contenido de la página
            // ==========================================
            assertNotNull(result);
            assertEquals(1, result.content().size());
            assertEquals("Operaciones", result.content().get(0).getName());
            assertEquals(0, result.page());
            assertEquals(10, result.size());
            assertEquals(1L, result.totalElements());
            assertEquals(1, result.totalPages());
            verify(departmentRepository, times(1)).findAll(pageable);
        }
    }

    @Nested
    @DisplayName("Método: restoreDepartment (Restaurar Departamento)")
    class RestoreDepartmentTests {

        @Test
        @DisplayName("Paso 1: Si ya estaba activo (deletedAt == null), retorna el dominio sin re-guardar")
        void shouldReturnDirectlyIfAlreadyNotDeleted() {
            // ==========================================
            // PASO 1 (GIVEN - DADO): Entidad con deletedAt == null
            // ==========================================
            UUID id = UUID.randomUUID();
            DepartmentEntity entity = DepartmentMother.aDepartmentEntityBuilder()
                    .publicId(id)
                    .active(true)
                    .deletedAt(null)
                    .build();

            when(departmentRepository.findByPublicId(id)).thenReturn(Optional.of(entity));

            // ==========================================
            // PASO 2 (WHEN - CUANDO): Restauramos
            // ==========================================
            Department result = departmentPersistenceAdapter.restoreDepartment(id);

            // ==========================================
            // PASO 3 (THEN - ENTONCES): No llama a saveAndFlush, solo mapea y retorna
            // ==========================================
            assertNotNull(result);
            assertTrue(result.getActive());
            assertNull(result.getDeletedAt());
            verify(departmentRepository, never()).saveAndFlush(any());
        }

        @Test
        @DisplayName("Paso 2: Si estaba eliminado (deletedAt != null), restaura active=true, deletedAt=null y guarda")
        void shouldRestoreDeletedDepartment() {
            // ==========================================
            // PASO 1 (GIVEN - DADO): Entidad eliminada lógicamente
            // ==========================================
            UUID id = UUID.randomUUID();
            DepartmentEntity deletedEntity = DepartmentMother.aDepartmentEntityBuilder()
                    .publicId(id)
                    .active(false)
                    .deletedAt(LocalDateTime.now())
                    .build();

            DepartmentEntity restoredEntity = DepartmentMother.aDepartmentEntityBuilder()
                    .publicId(id)
                    .active(true)
                    .deletedAt(null)
                    .build();

            when(departmentRepository.findByPublicId(id)).thenReturn(Optional.of(deletedEntity));
            when(departmentRepository.saveAndFlush(deletedEntity)).thenReturn(restoredEntity);

            // ==========================================
            // PASO 2 (WHEN - CUANDO): Invocamos restauración
            // ==========================================
            Department result = departmentPersistenceAdapter.restoreDepartment(id);

            // ==========================================
            // PASO 3 (THEN - ENTONCES): Verificamos activación y persistencia
            // ==========================================
            assertNotNull(result);
            assertTrue(result.getActive());
            assertNull(result.getDeletedAt());
            verify(departmentRepository, times(1)).saveAndFlush(deletedEntity);
        }

        @Test
        @DisplayName("Paso 3: Debe lanzar DepartmentNotFoundException si no existe la entidad a restaurar")
        void shouldThrowExceptionWhenRestoringNonExistent() {
            // ==========================================
            // PASO 1 (GIVEN - DADO): ID inexistente
            // ==========================================
            UUID id = UUID.randomUUID();
            when(departmentRepository.findByPublicId(id)).thenReturn(Optional.empty());

            // ==========================================
            // PASO 2 & 3 (WHEN & THEN): Excepción lanzada
            // ==========================================
            assertThrows(DepartmentNotFoundException.class, () -> departmentPersistenceAdapter.restoreDepartment(id));
            verify(departmentRepository, never()).saveAndFlush(any());
        }
    }
}
