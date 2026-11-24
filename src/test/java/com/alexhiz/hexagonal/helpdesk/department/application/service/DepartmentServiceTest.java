package com.alexhiz.hexagonal.helpdesk.department.application.service;

import com.alexhiz.hexagonal.helpdesk.department.application.port.out.DepartmentRepositoryPort;
import com.alexhiz.hexagonal.helpdesk.department.domain.exception.DepartmentAlreadyExistsException;
import com.alexhiz.hexagonal.helpdesk.department.domain.exception.DepartmentNotFoundException;
import com.alexhiz.hexagonal.helpdesk.department.domain.model.Department;
import com.alexhiz.hexagonal.helpdesk.department.testdata.DepartmentMother;
import com.alexhiz.hexagonal.helpdesk.shared.domain.exception.BusinessException;
import com.alexhiz.hexagonal.helpdesk.shared.domain.model.PageQuery;
import com.alexhiz.hexagonal.helpdesk.shared.domain.model.PageResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para DepartmentService (Capa de Aplicación).
 * Se utiliza Mockito para aislar el servicio de sus dependencias externas (Puerto de repositorio y Redis).
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("DepartmentService - Pruebas Unitarias de Casos de Uso")
class DepartmentServiceTest {

    @Mock
    private DepartmentRepositoryPort departmentRepositoryPort;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @InjectMocks
    private DepartmentService departmentService;

    private static final String CACHE_KEY = "departments";

    @Nested
    @DisplayName("Caso de Uso: Crear Departamento (create)")
    class CreateDepartmentTests {

        @Test
        @DisplayName("Paso 1: Debe crear un departamento exitosamente, recortar espacios y limpiar la caché")
        void shouldCreateDepartmentSuccessfully() {
            // ==========================================
            // PASO 1 (GIVEN - DADO):
            // Configuramos un departamento con espacios en el nombre para verificar que haga trim(),
            // y configuramos el repositorio para indicar que no existe duplicado y retorne el guardado.
            // ==========================================
            Department departmentToCreate = DepartmentMother.aNewDepartment("   Recursos Humanos   ");
            Department expectedSaved = DepartmentMother.aDepartmentNamed("Recursos Humanos");

            when(departmentRepositoryPort.existsByName("Recursos Humanos")).thenReturn(false);
            when(departmentRepositoryPort.save(any(Department.class))).thenReturn(expectedSaved);
            when(redisTemplate.delete(CACHE_KEY)).thenReturn(Boolean.TRUE);

            // ==========================================
            // PASO 2 (WHEN - CUANDO): Ejecutamos el método a probar
            // ==========================================
            Department result = departmentService.create(departmentToCreate);

            // ==========================================
            // PASO 3 (THEN - ENTONCES): Verificaciones
            // 1. El departamento devuelto no es nulo y tiene el nombre formateado.
            // 2. Se verificó que se haya persistido en el repositorio.
            // 3. Se invalidó la clave de caché en Redis para evitar datos obsoletos.
            // ==========================================
            assertNotNull(result, "El departamento guardado no debe ser nulo");
            assertEquals("Recursos Humanos", result.getName());
            verify(departmentRepositoryPort, times(1)).existsByName("Recursos Humanos");
            verify(departmentRepositoryPort, times(1)).save(departmentToCreate);
            verify(redisTemplate, times(1)).delete(CACHE_KEY);
        }

        @Test
        @DisplayName("Paso 2: Debe lanzar BusinessException cuando el nombre es nulo")
        void shouldThrowBusinessExceptionWhenNameIsNull() {
            // ==========================================
            // PASO 1 (GIVEN - DADO): Departamento con nombre null
            // ==========================================
            Department department = DepartmentMother.aDepartmentBuilder().name(null).build();

            // ==========================================
            // PASO 2 & 3 (WHEN & THEN): Comprobamos que lance la excepción y no persista
            // ==========================================
            BusinessException exception = assertThrows(BusinessException.class, () -> departmentService.create(department));
            assertEquals("Department name cannot be empty", exception.getMessage());

            // Verificamos que no se interactúe con el repositorio ni con la caché
            verifyNoInteractions(departmentRepositoryPort);
            verifyNoInteractions(redisTemplate);
        }

        @Test
        @DisplayName("Paso 3: Debe lanzar BusinessException cuando el nombre está compuesto sólo de espacios en blanco")
        void shouldThrowBusinessExceptionWhenNameIsBlank() {
            // ==========================================
            // PASO 1 (GIVEN - DADO): Departamento con espacios en blanco
            // ==========================================
            Department department = DepartmentMother.aDepartmentBuilder().name("    ").build();

            // ==========================================
            // PASO 2 & 3 (WHEN & THEN): Verificamos el lanzamiento de BusinessException
            // ==========================================
            BusinessException exception = assertThrows(BusinessException.class, () -> departmentService.create(department));
            assertEquals("Department name cannot be empty", exception.getMessage());

            verifyNoInteractions(departmentRepositoryPort);
            verifyNoInteractions(redisTemplate);
        }

        @Test
        @DisplayName("Paso 4: Debe lanzar DepartmentAlreadyExistsException si el departamento ya existe")
        void shouldThrowDepartmentAlreadyExistsExceptionWhenNameAlreadyExists() {
            // ==========================================
            // PASO 1 (GIVEN - DADO): El repositorio indica que ya existe un departamento con ese nombre
            // ==========================================
            Department department = DepartmentMother.aDepartmentNamed("IT");
            when(departmentRepositoryPort.existsByName("IT")).thenReturn(true);

            // ==========================================
            // PASO 2 & 3 (WHEN & THEN): Comprobamos la excepción y que no se guarde nada
            // ==========================================
            assertThrows(DepartmentAlreadyExistsException.class, () -> departmentService.create(department));
            verify(departmentRepositoryPort, times(1)).existsByName("IT");
            verify(departmentRepositoryPort, never()).save(any());
            verifyNoInteractions(redisTemplate);
        }
    }

    @Nested
    @DisplayName("Caso de Uso: Listar Departamentos con Caché (getAllDepartments)")
    class GetAllDepartmentsTests {

        @Test
        @DisplayName("Paso 1: Cache HIT - Debe devolver los departamentos directamente desde Redis sin consultar la BD")
        void shouldReturnDepartmentsFromCacheWhenCacheHit() {
            // ==========================================
            // PASO 1 (GIVEN - DADO): Redis contiene la lista de departamentos en memoria
            // ==========================================
            List<Department> cachedList = List.of(
                    DepartmentMother.aDepartmentNamed("IT"),
                    DepartmentMother.aDepartmentNamed("RRHH")
            );

            when(redisTemplate.opsForValue()).thenReturn(valueOperations);
            when(valueOperations.get(CACHE_KEY)).thenReturn(cachedList);

            // ==========================================
            // PASO 2 (WHEN - CUANDO): Solicitamos todos los departamentos
            // ==========================================
            List<Department> result = departmentService.getAllDepartments();

            // ==========================================
            // PASO 3 (THEN - ENTONCES):
            // Comprobamos que devuelve la lista de la caché y que el repositorio NUNCA fue llamado
            // ==========================================
            assertEquals(2, result.size());
            assertEquals("IT", result.get(0).getName());
            verify(redisTemplate.opsForValue(), times(1)).get(CACHE_KEY);
            verify(departmentRepositoryPort, never()).findAll();
        }

        @Test
        @DisplayName("Paso 2: Cache MISS - Debe consultar la BD y guardar el resultado en Redis con TTL de 10 minutos")
        void shouldFetchFromRepositoryAndPopulateCacheWhenCacheMiss() {
            // ==========================================
            // PASO 1 (GIVEN - DADO): La caché no tiene los datos (retorna null) y la BD tiene 1 registro
            // ==========================================
            List<Department> dbList = List.of(DepartmentMother.aDepartmentNamed("Finanzas"));

            when(redisTemplate.opsForValue()).thenReturn(valueOperations);
            when(valueOperations.get(CACHE_KEY)).thenReturn(null);
            when(departmentRepositoryPort.findAll()).thenReturn(dbList);

            // ==========================================
            // PASO 2 (WHEN - CUANDO): Ejecutamos la consulta
            // ==========================================
            List<Department> result = departmentService.getAllDepartments();

            // ==========================================
            // PASO 3 (THEN - ENTONCES):
            // Verificamos que se consultó la BD y posteriormente se actualizó la caché con el TTL esperado
            // ==========================================
            assertEquals(1, result.size());
            assertEquals("Finanzas", result.get(0).getName());
            verify(departmentRepositoryPort, times(1)).findAll();
            verify(valueOperations, times(1)).set(eq(CACHE_KEY), eq(dbList), eq(Duration.ofMinutes(10)));
        }

        @Test
        @DisplayName("Paso 3: Fallback por Error en Redis - Si Redis falla, debe consultar la BD sin propagar error")
        void shouldFallbackToDatabaseWhenRedisThrowsException() {
            // ==========================================
            // PASO 1 (GIVEN - DADO): Redis arroja una excepción de conexión
            // ==========================================
            List<Department> dbList = List.of(DepartmentMother.aDepartmentNamed("Operaciones"));

            when(redisTemplate.opsForValue()).thenReturn(valueOperations);
            when(valueOperations.get(CACHE_KEY)).thenThrow(new RuntimeException("Redis connection error"));
            when(departmentRepositoryPort.findAll()).thenReturn(dbList);

            // ==========================================
            // PASO 2 (WHEN - CUANDO): Se llama al método
            // ==========================================
            List<Department> result = departmentService.getAllDepartments();

            // ==========================================
            // PASO 3 (THEN - ENTONCES):
            // El fallo es transparente para el cliente y los datos se obtienen de la base de datos
            // ==========================================
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals("Operaciones", result.get(0).getName());
            verify(departmentRepositoryPort, times(1)).findAll();
        }
    }

    @Nested
    @DisplayName("Caso de Uso: Obtener Departamento por ID (getDepartmentById)")
    class GetDepartmentByIdTests {

        @Test
        @DisplayName("Paso 1: Debe retornar el departamento cuando el ID existe")
        void shouldReturnDepartmentWhenFound() {
            // ==========================================
            // PASO 1 (GIVEN - DADO): Configuramos un ID y departamento existente en el repositorio
            // ==========================================
            UUID id = UUID.randomUUID();
            Department expected = DepartmentMother.aDepartmentWith(id, "Legal");
            when(departmentRepositoryPort.findById(id)).thenReturn(Optional.of(expected));

            // ==========================================
            // PASO 2 (WHEN - CUANDO): Consultamos por ID
            // ==========================================
            Department actual = departmentService.getDepartmentById(id);

            // ==========================================
            // PASO 3 (THEN - ENTONCES): El departamento devuelto coincide con el esperado
            // ==========================================
            assertNotNull(actual);
            assertEquals(id, actual.getPublicId());
            assertEquals("Legal", actual.getName());
            verify(departmentRepositoryPort, times(1)).findById(id);
        }

        @Test
        @DisplayName("Paso 2: Debe lanzar DepartmentNotFoundException cuando el ID no existe")
        void shouldThrowDepartmentNotFoundExceptionWhenNotFound() {
            // ==========================================
            // PASO 1 (GIVEN - DADO): El repositorio retorna Optional.empty()
            // ==========================================
            UUID nonExistentId = UUID.randomUUID();
            when(departmentRepositoryPort.findById(nonExistentId)).thenReturn(Optional.empty());

            // ==========================================
            // PASO 2 & 3 (WHEN & THEN): Se verifica que se lance la excepción correspondiente
            // ==========================================
            DepartmentNotFoundException exception = assertThrows(
                    DepartmentNotFoundException.class,
                    () -> departmentService.getDepartmentById(nonExistentId)
            );
            assertTrue(exception.getMessage().contains(nonExistentId.toString()));
            verify(departmentRepositoryPort, times(1)).findById(nonExistentId);
        }
    }

    @Nested
    @DisplayName("Caso de Uso: Actualizar Departamento (update)")
    class UpdateDepartmentTests {

        @Test
        @DisplayName("Paso 1: Debe actualizar los datos del departamento exitosamente y purgar la caché")
        void shouldUpdateDepartmentSuccessfully() {
            // ==========================================
            // PASO 1 (GIVEN - DADO):
            // Departamento existente en BD y nueva información para actualizar.
            // ==========================================
            UUID id = UUID.randomUUID();
            Department existing = DepartmentMother.aDepartmentWith(id, "Ventas");
            Department updateData = Department.builder()
                    .name("   Ventas y Marketing   ")
                    .active(false)
                    .build();
            Department saved = DepartmentMother.aDepartmentWith(id, "Ventas y Marketing");
            saved.setActive(false);

            when(departmentRepositoryPort.findById(id)).thenReturn(Optional.of(existing));
            when(departmentRepositoryPort.existsByNameAndIdNot("Ventas y Marketing", id)).thenReturn(false);
            when(departmentRepositoryPort.save(any(Department.class))).thenReturn(saved);
            when(redisTemplate.delete(CACHE_KEY)).thenReturn(Boolean.TRUE);

            // ==========================================
            // PASO 2 (WHEN - CUANDO): Actualizamos el departamento
            // ==========================================
            Department result = departmentService.update(id, updateData);

            // ==========================================
            // PASO 3 (THEN - ENTONCES):
            // Comprobamos la actualización de valores, la llamada a guardar y la invalidación de la caché.
            // ==========================================
            assertNotNull(result);
            assertEquals("Ventas y Marketing", result.getName());
            assertFalse(result.getActive());
            verify(departmentRepositoryPort, times(1)).findById(id);
            verify(departmentRepositoryPort, times(1)).existsByNameAndIdNot("Ventas y Marketing", id);
            verify(departmentRepositoryPort, times(1)).save(existing);
            verify(redisTemplate, times(1)).delete(CACHE_KEY);
        }

        @Test
        @DisplayName("Paso 2: Debe lanzar DepartmentNotFoundException si el departamento a actualizar no existe")
        void shouldThrowDepartmentNotFoundExceptionWhenUpdatingNonExistent() {
            // ==========================================
            // PASO 1 (GIVEN - DADO): ID no existe en la BD
            // ==========================================
            UUID id = UUID.randomUUID();
            Department updateData = DepartmentMother.aDepartmentNamed("Nuevo Nombre");
            when(departmentRepositoryPort.findById(id)).thenReturn(Optional.empty());

            // ==========================================
            // PASO 2 & 3 (WHEN & THEN): Comprobamos la excepción
            // ==========================================
            assertThrows(DepartmentNotFoundException.class, () -> departmentService.update(id, updateData));
            verify(departmentRepositoryPort, never()).save(any());
            verifyNoInteractions(redisTemplate);
        }

        @Test
        @DisplayName("Paso 3: Debe lanzar BusinessException si el nuevo nombre es nulo o vacío")
        void shouldThrowBusinessExceptionWhenUpdatingWithBlankName() {
            // ==========================================
            // PASO 1 (GIVEN - DADO): Departamento encontrado pero datos de actualización con nombre vacío
            // ==========================================
            UUID id = UUID.randomUUID();
            Department existing = DepartmentMother.aDepartmentWith(id, "Original");
            Department invalidUpdate = Department.builder().name("   ").build();

            when(departmentRepositoryPort.findById(id)).thenReturn(Optional.of(existing));

            // ==========================================
            // PASO 2 & 3 (WHEN & THEN): Verificamos validación de negocio
            // ==========================================
            assertThrows(BusinessException.class, () -> departmentService.update(id, invalidUpdate));
            verify(departmentRepositoryPort, never()).save(any());
        }

        @Test
        @DisplayName("Paso 4: Debe lanzar DepartmentAlreadyExistsException si el nombre ya está en uso por otro departamento")
        void shouldThrowDepartmentAlreadyExistsExceptionWhenNameTakenByAnother() {
            // ==========================================
            // PASO 1 (GIVEN - DADO): El nombre a actualizar ya pertenece a otro departamento
            // ==========================================
            UUID id = UUID.randomUUID();
            Department existing = DepartmentMother.aDepartmentWith(id, "Original");
            Department updateData = Department.builder().name("NombreOcupado").active(true).build();

            when(departmentRepositoryPort.findById(id)).thenReturn(Optional.of(existing));
            when(departmentRepositoryPort.existsByNameAndIdNot("NombreOcupado", id)).thenReturn(true);

            // ==========================================
            // PASO 2 & 3 (WHEN & THEN): Se lanza la excepción de duplicado
            // ==========================================
            assertThrows(DepartmentAlreadyExistsException.class, () -> departmentService.update(id, updateData));
            verify(departmentRepositoryPort, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Caso de Uso: Eliminar Departamento (delete)")
    class DeleteDepartmentTests {

        @Test
        @DisplayName("Paso 1: Debe eliminar departamento exitosamente y purgar la caché")
        void shouldDeleteDepartmentSuccessfully() {
            // ==========================================
            // PASO 1 (GIVEN - DADO): El departamento existe en BD
            // ==========================================
            UUID id = UUID.randomUUID();
            when(departmentRepositoryPort.existsById(id)).thenReturn(true);
            when(redisTemplate.delete(CACHE_KEY)).thenReturn(Boolean.TRUE);

            // ==========================================
            // PASO 2 (WHEN - CUANDO): Invocamos la eliminación
            // ==========================================
            departmentService.delete(id);

            // ==========================================
            // PASO 3 (THEN - ENTONCES): Se ejecuta delete en el puerto e invalidación en Redis
            // ==========================================
            verify(departmentRepositoryPort, times(1)).delete(id);
            verify(redisTemplate, times(1)).delete(CACHE_KEY);
        }

        @Test
        @DisplayName("Paso 2: Debe lanzar DepartmentNotFoundException si el departamento a eliminar no existe")
        void shouldThrowDepartmentNotFoundExceptionWhenDeletingNonExistent() {
            // ==========================================
            // PASO 1 (GIVEN - DADO): ID inexistente
            // ==========================================
            UUID id = UUID.randomUUID();
            when(departmentRepositoryPort.existsById(id)).thenReturn(false);

            // ==========================================
            // PASO 2 & 3 (WHEN & THEN): Comprobamos que falle y no ejecute delete en BD
            // ==========================================
            assertThrows(DepartmentNotFoundException.class, () -> departmentService.delete(id));
            verify(departmentRepositoryPort, never()).delete(any());
            verifyNoInteractions(redisTemplate);
        }
    }

    @Nested
    @DisplayName("Caso de Uso: Paginación de Departamentos (execute / getDepartmentsFromCache)")
    class PageDepartmentsTests {

        @Test
        @DisplayName("Paso 1: Cache HIT en Paginación - Debe retornar resultados desde Redis sin ir al repositorio")
        void shouldReturnPagedDepartmentsFromCacheWhenHit() {
            // ==========================================
            // PASO 1 (GIVEN - DADO): Clave de caché para página 0 tamaño 10
            // ==========================================
            PageQuery query = new PageQuery(0, 10);
            String pageCacheKey = CACHE_KEY + ":page:0:size:10";
            PageResult<Department> cachedPage = new PageResult<>(
                    List.of(DepartmentMother.aDepartmentNamed("IT")),
                    0, 10, 1L, 1
            );

            when(redisTemplate.opsForValue()).thenReturn(valueOperations);
            when(valueOperations.get(pageCacheKey)).thenReturn(cachedPage);

            // ==========================================
            // PASO 2 (WHEN - CUANDO): Ejecutamos la paginación
            // ==========================================
            PageResult<Department> result = departmentService.execute(query);

            // ==========================================
            // PASO 3 (THEN - ENTONCES): Se devuelven los datos cacheados
            // ==========================================
            assertNotNull(result);
            assertEquals(1, result.content().size());
            verify(departmentRepositoryPort, never()).findAllPages(any());
        }

        @Test
        @DisplayName("Paso 2: Cache MISS en Paginación - Debe consultar el repositorio y poblar la caché con TTL")
        void shouldFetchFromRepositoryAndCacheWhenPageCacheMiss() {
            // ==========================================
            // PASO 1 (GIVEN - DADO): Caché vacía y repositorio con resultados
            // ==========================================
            PageQuery query = new PageQuery(1, 5);
            String pageCacheKey = CACHE_KEY + ":page:1:size:5";
            PageResult<Department> dbPage = new PageResult<>(
                    List.of(DepartmentMother.aDepartmentNamed("Logística")),
                    1, 5, 6L, 2
            );

            when(redisTemplate.opsForValue()).thenReturn(valueOperations);
            when(valueOperations.get(pageCacheKey)).thenReturn(null);
            when(departmentRepositoryPort.findAllPages(query)).thenReturn(dbPage);

            // ==========================================
            // PASO 2 (WHEN - CUANDO): Ejecutamos la consulta paginada
            // ==========================================
            PageResult<Department> result = departmentService.execute(query);

            // ==========================================
            // PASO 3 (THEN - ENTONCES): Verificamos llamada a BD y guardado en Redis
            // ==========================================
            assertNotNull(result);
            assertEquals(1, result.content().size());
            verify(departmentRepositoryPort, times(1)).findAllPages(query);
            verify(valueOperations, times(1)).set(eq(pageCacheKey), eq(dbPage), eq(Duration.ofMinutes(10)));
        }

        @Test
        @DisplayName("Paso 3: Fallback por Error en Redis durante paginación")
        void shouldFallbackToDatabaseWhenRedisErrorDuringPaging() {
            // ==========================================
            // PASO 1 (GIVEN - DADO): Redis falla al consultar la clave de página
            // ==========================================
            PageQuery query = new PageQuery(0, 10);
            String pageCacheKey = CACHE_KEY + ":page:0:size:10";
            PageResult<Department> dbPage = new PageResult<>(
                    List.of(DepartmentMother.aDepartmentNamed("Finanzas")),
                    0, 10, 1L, 1
            );

            when(redisTemplate.opsForValue()).thenReturn(valueOperations);
            when(valueOperations.get(pageCacheKey)).thenThrow(new RuntimeException("Redis timeout"));
            when(departmentRepositoryPort.findAllPages(query)).thenReturn(dbPage);

            // ==========================================
            // PASO 2 (WHEN - CUANDO): Ejecutamos
            // ==========================================
            PageResult<Department> result = departmentService.execute(query);

            // ==========================================
            // PASO 3 (THEN - ENTONCES): Fallback transparente
            // ==========================================
            assertNotNull(result);
            assertEquals(1, result.content().size());
            verify(departmentRepositoryPort, times(1)).findAllPages(query);
        }
    }

    @Nested
    @DisplayName("Caso de Uso: Restaurar Departamento (restore)")
    class RestoreDepartmentTests {

        @Test
        @DisplayName("Paso 1: Debe llamar al repositorio para restaurar el departamento y retornar el resultado")
        void shouldRestoreDepartmentSuccessfully() {
            // ==========================================
            // PASO 1 (GIVEN - DADO): ID a restaurar y departamento retornado por el puerto
            // ==========================================
            UUID id = UUID.randomUUID();
            Department restored = DepartmentMother.aDepartmentWith(id, "IT");
            restored.setActive(true);
            restored.setDeletedAt(null);

            when(departmentRepositoryPort.restoreDepartment(id)).thenReturn(restored);

            // ==========================================
            // PASO 2 (WHEN - CUANDO): Invocamos restaurar
            // ==========================================
            Department result = departmentService.restore(id);

            // ==========================================
            // PASO 3 (THEN - ENTONCES): Verificamos el resultado devuelto
            // ==========================================
            assertNotNull(result);
            assertTrue(result.getActive());
            assertNull(result.getDeletedAt());
            verify(departmentRepositoryPort, times(1)).restoreDepartment(id);
        }
    }
}
