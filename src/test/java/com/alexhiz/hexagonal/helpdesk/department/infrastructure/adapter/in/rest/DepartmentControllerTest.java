package com.alexhiz.hexagonal.helpdesk.department.infrastructure.adapter.in.rest;

import com.alexhiz.hexagonal.helpdesk.department.application.port.in.*;
import com.alexhiz.hexagonal.helpdesk.department.domain.exception.DepartmentAlreadyExistsException;
import com.alexhiz.hexagonal.helpdesk.department.domain.exception.DepartmentNotFoundException;
import com.alexhiz.hexagonal.helpdesk.department.domain.model.Department;
import com.alexhiz.hexagonal.helpdesk.department.infrastructure.adapter.in.rest.dto.DepartmentRequest;
import com.alexhiz.hexagonal.helpdesk.department.testdata.DepartmentMother;
import com.alexhiz.hexagonal.helpdesk.shared.domain.model.PageQuery;
import com.alexhiz.hexagonal.helpdesk.shared.domain.model.PageResult;
import com.alexhiz.hexagonal.helpdesk.shared.infrastructure.adapter.in.rest.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas unitarias para DepartmentController (Adaptador REST Inbound).
 * Se utiliza MockMvc en modo Standalone junto con GlobalExceptionHandler
 * para comprobar el contrato HTTP, serialización JSON, validaciones @Valid y códigos de estado.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("DepartmentController - Pruebas del Adaptador REST")
class DepartmentControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private CreateDepartmentUseCase createDepartmentUseCase;
    @Mock
    private ListDepartmentsUseCase listDepartmentsUseCase;
    @Mock
    private DeleteDepartmentUseCase deleteDepartmentUseCase;
    @Mock
    private GetDepartmentByIdUseCase getDepartmentByIdUseCase;
    @Mock
    private UpdateDepartmentUseCase updateDepartmentUseCase;
    @Mock
    private PageDepartmentsUseCase pageDepartmentsUseCase;
    @Mock
    private RestoreDepartmentUseCase restoreDepartmentUseCase;

    private static final String BASE_URL = "/api/departments";

    @BeforeEach
    void setUp() {
        DepartmentController controller = new DepartmentController(
                createDepartmentUseCase,
                listDepartmentsUseCase,
                deleteDepartmentUseCase,
                getDepartmentByIdUseCase,
                updateDepartmentUseCase,
                pageDepartmentsUseCase,
                restoreDepartmentUseCase
        );

        // Configuramos MockMvc con el controlador y el manejador global de excepciones
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        // Configuramos ObjectMapper con soporte para fechas de Java 8+
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    }

    @Nested
    @DisplayName("POST /api/departments - Crear Departamento")
    class CreateEndpointTests {

        @Test
        @DisplayName("Paso 1: Debe responder 201 Created con el departamento creado cuando la petición es válida")
        void shouldReturn201WhenCreateRequestIsValid() throws Exception {
            // ==========================================
            // PASO 1 (GIVEN - DADO): Petición válida y entidad guardada simulada
            // ==========================================
            DepartmentRequest request = new DepartmentRequest("Tecnología", true);
            UUID generatedId = UUID.randomUUID();
            Department savedDomain = DepartmentMother.aDepartmentWith(generatedId, "Tecnología");

            when(createDepartmentUseCase.create(any(Department.class))).thenReturn(savedDomain);

            // ==========================================
            // PASO 2 & 3 (WHEN & THEN): Enviamos POST y verificamos el status 201 y cuerpo JSON
            // ==========================================
            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(generatedId.toString()))
                    .andExpect(jsonPath("$.name").value("Tecnología"))
                    .andExpect(jsonPath("$.active").value(true));

            verify(createDepartmentUseCase, times(1)).create(any(Department.class));
        }

        @Test
        @DisplayName("Paso 2: Debe responder 400 Bad Request cuando el nombre es inválido (validación @Valid)")
        void shouldReturn400WhenNameIsBlank() throws Exception {
            // ==========================================
            // PASO 1 (GIVEN - DADO): Request con nombre en blanco
            // ==========================================
            DepartmentRequest invalidRequest = new DepartmentRequest("   ", true);

            // ==========================================
            // PASO 2 & 3 (WHEN & THEN): Se ejecuta la petición y se valida error 400
            // ==========================================
            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.validationErrors.name").exists());

            verifyNoInteractions(createDepartmentUseCase);
        }

        @Test
        @DisplayName("Paso 3: Debe responder 409 Conflict cuando el departamento ya existe")
        void shouldReturn409WhenDepartmentAlreadyExists() throws Exception {
            // ==========================================
            // PASO 1 (GIVEN - DADO): El caso de uso lanza DepartmentAlreadyExistsException
            // ==========================================
            DepartmentRequest request = new DepartmentRequest("IT", true);
            when(createDepartmentUseCase.create(any(Department.class)))
                    .thenThrow(new DepartmentAlreadyExistsException("IT"));

            // ==========================================
            // PASO 2 & 3 (WHEN & THEN): Comprobamos código 409 Conflict
            // ==========================================
            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.status").value(409))
                    .andExpect(jsonPath("$.message").value(containsString("Department already exists with name: IT")));
        }
    }

    @Nested
    @DisplayName("GET /api/departments - Listar Departamentos")
    class ListEndpointTests {

        @Test
        @DisplayName("Paso 1: Debe responder 200 OK con la lista de departamentos")
        void shouldReturn200WithDepartmentList() throws Exception {
            // ==========================================
            // PASO 1 (GIVEN - DADO): Lista de departamentos retornada por el caso de uso
            // ==========================================
            UUID id1 = UUID.randomUUID();
            UUID id2 = UUID.randomUUID();
            List<Department> list = List.of(
                    DepartmentMother.aDepartmentWith(id1, "IT"),
                    DepartmentMother.aDepartmentWith(id2, "RRHH")
            );

            when(listDepartmentsUseCase.getAllDepartments()).thenReturn(list);

            // ==========================================
            // PASO 2 & 3 (WHEN & THEN): Solicitamos GET y comprobamos elementos
            // ==========================================
            mockMvc.perform(get(BASE_URL))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].id").value(id1.toString()))
                    .andExpect(jsonPath("$[0].name").value("IT"))
                    .andExpect(jsonPath("$[1].id").value(id2.toString()))
                    .andExpect(jsonPath("$[1].name").value("RRHH"));

            verify(listDepartmentsUseCase, times(1)).getAllDepartments();
        }
    }

    @Nested
    @DisplayName("GET /api/departments/{id} - Obtener por ID")
    class GetByIdEndpointTests {

        @Test
        @DisplayName("Paso 1: Debe responder 200 OK con el departamento cuando existe")
        void shouldReturn200WhenDepartmentExists() throws Exception {
            // ==========================================
            // PASO 1 (GIVEN - DADO): Departamento encontrado
            // ==========================================
            UUID id = UUID.randomUUID();
            Department department = DepartmentMother.aDepartmentWith(id, "Finanzas");

            when(getDepartmentByIdUseCase.getDepartmentById(id)).thenReturn(department);

            // ==========================================
            // PASO 2 & 3 (WHEN & THEN): Solicitud GET con ID
            // ==========================================
            mockMvc.perform(get(BASE_URL + "/{id}", id))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(id.toString()))
                    .andExpect(jsonPath("$.name").value("Finanzas"));

            verify(getDepartmentByIdUseCase, times(1)).getDepartmentById(id);
        }

        @Test
        @DisplayName("Paso 2: Debe responder 404 Not Found cuando el departamento no existe")
        void shouldReturn404WhenDepartmentNotFound() throws Exception {
            // ==========================================
            // PASO 1 (GIVEN - DADO): ID inexistente
            // ==========================================
            UUID nonExistentId = UUID.randomUUID();
            when(getDepartmentByIdUseCase.getDepartmentById(nonExistentId))
                    .thenThrow(new DepartmentNotFoundException(nonExistentId));

            // ==========================================
            // PASO 2 & 3 (WHEN & THEN): Verificamos respuesta 404
            // ==========================================
            mockMvc.perform(get(BASE_URL + "/{id}", nonExistentId))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.message").value(containsString("Department not found with id: " + nonExistentId)));
        }
    }

    @Nested
    @DisplayName("PUT /api/departments/{id} - Actualizar Departamento")
    class UpdateEndpointTests {

        @Test
        @DisplayName("Paso 1: Debe responder 200 OK con el departamento actualizado")
        void shouldReturn200WhenUpdateIsSuccessful() throws Exception {
            // ==========================================
            // PASO 1 (GIVEN - DADO): Datos para actualización
            // ==========================================
            UUID id = UUID.randomUUID();
            DepartmentRequest request = new DepartmentRequest("Infraestructura", false);
            Department updatedDomain = DepartmentMother.aDepartmentWith(id, "Infraestructura");
            updatedDomain.setActive(false);

            when(updateDepartmentUseCase.update(eq(id), any(Department.class))).thenReturn(updatedDomain);

            // ==========================================
            // PASO 2 & 3 (WHEN & THEN): Petición PUT
            // ==========================================
            mockMvc.perform(put(BASE_URL + "/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(id.toString()))
                    .andExpect(jsonPath("$.name").value("Infraestructura"))
                    .andExpect(jsonPath("$.active").value(false));

            verify(updateDepartmentUseCase, times(1)).update(eq(id), any(Department.class));
        }

        @Test
        @DisplayName("Paso 2: Debe responder 404 Not Found al actualizar un departamento inexistente")
        void shouldReturn404WhenUpdatingNonExistent() throws Exception {
            // ==========================================
            // PASO 1 (GIVEN - DADO): Excepción de departamento no encontrado
            // ==========================================
            UUID id = UUID.randomUUID();
            DepartmentRequest request = new DepartmentRequest("Cualquiera", true);

            when(updateDepartmentUseCase.update(eq(id), any(Department.class)))
                    .thenThrow(new DepartmentNotFoundException(id));

            // ==========================================
            // PASO 2 & 3 (WHEN & THEN): Petición PUT esperando 404
            // ==========================================
            mockMvc.perform(put(BASE_URL + "/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404));
        }

        @Test
        @DisplayName("Paso 3: Debe responder 400 Bad Request cuando el body no cumple con las validaciones")
        void shouldReturn400WhenValidationFailsOnUpdate() throws Exception {
            // ==========================================
            // PASO 1 (GIVEN - DADO): Request con campo 'active' nulo (inválido por @NotNull)
            // ==========================================
            UUID id = UUID.randomUUID();
            DepartmentRequest request = new DepartmentRequest("Válido", null);

            // ==========================================
            // PASO 2 & 3 (WHEN & THEN): Esperamos 400 Bad Request
            // ==========================================
            mockMvc.perform(put(BASE_URL + "/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.validationErrors.active").exists());

            verifyNoInteractions(updateDepartmentUseCase);
        }
    }

    @Nested
    @DisplayName("DELETE /api/departments/{id} - Eliminar Departamento")
    class DeleteEndpointTests {

        @Test
        @DisplayName("Paso 1: Debe responder 204 No Content cuando se elimina correctamente")
        void shouldReturn204WhenDeletedSuccessfully() throws Exception {
            // ==========================================
            // PASO 1 (GIVEN - DADO): ID a eliminar
            // ==========================================
            UUID id = UUID.randomUUID();
            doNothing().when(deleteDepartmentUseCase).delete(id);

            // ==========================================
            // PASO 2 & 3 (WHEN & THEN): Enviamos DELETE
            // ==========================================
            mockMvc.perform(delete(BASE_URL + "/{id}", id))
                    .andExpect(status().isNoContent());

            verify(deleteDepartmentUseCase, times(1)).delete(id);
        }

        @Test
        @DisplayName("Paso 2: Debe responder 404 Not Found si no existe el departamento a eliminar")
        void shouldReturn404WhenDepartmentToDeleteNotFound() throws Exception {
            // ==========================================
            // PASO 1 (GIVEN - DADO): Excepción lanzada por el caso de uso
            // ==========================================
            UUID id = UUID.randomUUID();
            doThrow(new DepartmentNotFoundException(id)).when(deleteDepartmentUseCase).delete(id);

            // ==========================================
            // PASO 2 & 3 (WHEN & THEN): Verificamos respuesta 404
            // ==========================================
            mockMvc.perform(delete(BASE_URL + "/{id}", id))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404));
        }
    }

    @Nested
    @DisplayName("GET /api/departments/pages - Listar Departamentos Paginados")
    class PagingEndpointTests {

        @Test
        @DisplayName("Paso 1: Debe responder 200 OK con la página de resultados")
        void shouldReturn200WithPagedResults() throws Exception {
            // ==========================================
            // PASO 1 (GIVEN - DADO): Configuración de página de prueba
            // ==========================================
            UUID id = UUID.randomUUID();
            Department department = DepartmentMother.aDepartmentWith(id, "Sistemas");
            PageResult<Department> pageResult = new PageResult<>(
                    List.of(department),
                    0, 10, 1L, 1
            );

            when(pageDepartmentsUseCase.execute(new PageQuery(0, 10))).thenReturn(pageResult);

            // ==========================================
            // PASO 2 & 3 (WHEN & THEN): Petición GET con parámetros de paginación
            // ==========================================
            mockMvc.perform(get(BASE_URL + "/pages")
                            .param("page", "0")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content.length()").value(1))
                    .andExpect(jsonPath("$.content[0].id").value(id.toString()))
                    .andExpect(jsonPath("$.content[0].name").value("Sistemas"))
                    .andExpect(jsonPath("$.page").value(0))
                    .andExpect(jsonPath("$.size").value(10))
                    .andExpect(jsonPath("$.totalElements").value(1))
                    .andExpect(jsonPath("$.totalPages").value(1));

            verify(pageDepartmentsUseCase, times(1)).execute(new PageQuery(0, 10));
        }
    }

    @Nested
    @DisplayName("PATCH /api/departments/{id}/restore - Restaurar Departamento")
    class RestoreEndpointTests {

        @Test
        @DisplayName("Paso 1: Debe responder 200 OK con el departamento restaurado")
        void shouldReturn200WhenDepartmentRestored() throws Exception {
            // ==========================================
            // PASO 1 (GIVEN - DADO): Departamento restaurado
            // ==========================================
            UUID id = UUID.randomUUID();
            Department restored = DepartmentMother.aDepartmentWith(id, "IT");
            restored.setActive(true);
            restored.setDeletedAt(null);

            when(restoreDepartmentUseCase.restore(id)).thenReturn(restored);

            // ==========================================
            // PASO 2 & 3 (WHEN & THEN): Petición PATCH para restaurar
            // ==========================================
            mockMvc.perform(patch(BASE_URL + "/{id}/restore", id))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(id.toString()))
                    .andExpect(jsonPath("$.active").value(true))
                    .andExpect(jsonPath("$.deletedAt").doesNotExist());

            verify(restoreDepartmentUseCase, times(1)).restore(id);
        }

        @Test
        @DisplayName("Paso 2: Debe responder 404 Not Found si el departamento a restaurar no existe")
        void shouldReturn404WhenRestoringNonExistentDepartment() throws Exception {
            // ==========================================
            // PASO 1 (GIVEN - DADO): Excepción de departamento no encontrado
            // ==========================================
            UUID id = UUID.randomUUID();
            when(restoreDepartmentUseCase.restore(id)).thenThrow(new DepartmentNotFoundException(id));

            // ==========================================
            // PASO 2 & 3 (WHEN & THEN): Petición PATCH esperando 404
            // ==========================================
            mockMvc.perform(patch(BASE_URL + "/{id}/restore", id))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404));
        }
    }
}
