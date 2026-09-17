package com.alexhiz.hexagonal.helpdesk.department.infrastructure.adapter.in.rest;

import com.alexhiz.hexagonal.helpdesk.category.infrastructure.adapter.in.rest.dto.CategoryResponse;
import com.alexhiz.hexagonal.helpdesk.department.application.port.in.*;
import com.alexhiz.hexagonal.helpdesk.department.domain.model.Department;
import com.alexhiz.hexagonal.helpdesk.department.infrastructure.adapter.in.rest.dto.DepartmentRequest;
import com.alexhiz.hexagonal.helpdesk.department.infrastructure.adapter.in.rest.dto.DepartmentResponse;
import com.alexhiz.hexagonal.helpdesk.shared.domain.model.PageQuery;
import com.alexhiz.hexagonal.helpdesk.shared.domain.model.PageResult;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
@Tag(name = "Departamentos", description = "Operaciones para gestionar los departamentos")
public class DepartmentController {

    private final CreateDepartmentUseCase createDepartmentUseCase;
    private final ListDepartmentsUseCase listDepartmentsUseCase;
    private final DeleteDepartmentUseCase deleteDepartmentUseCase;
    private final GetDepartmentByIdUseCase getDepartmentByIdUseCase;
    private final UpdateDepartmentUseCase updateDepartmentUseCase;
    private final PageDepartmentsUseCase pageDepartmentsUseCase;

    @PostMapping
    @Operation(summary = "Crea un departamento", description = "Crea un nuevo departamento")
    @ApiResponse(responseCode = "201", description = "Departamento creado correctamente")
    @ApiResponse(responseCode = "400", description = "Datos del departamento inválidos")
    public ResponseEntity<DepartmentResponse> create(@Valid @RequestBody DepartmentRequest request) {
        Department saved = createDepartmentUseCase.create(request.toDomain());
        return ResponseEntity.status(HttpStatus.CREATED).body(DepartmentResponse.from(saved));
    }

    @GetMapping
    @Operation(summary = "Lista los departamentos", description = "Obtiene todos los departamentos registrados")
    @ApiResponse(responseCode = "200", description = "Departamentos obtenidos correctamente")
    public ResponseEntity<List<DepartmentResponse>> list() {
        List<DepartmentResponse> responses = listDepartmentsUseCase.getAllDepartments().stream()
                .map(DepartmentResponse::from)
                .toList();
        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualiza un departamento", description = "Actualiza los datos de un departamento existente")
    @ApiResponse(responseCode = "200", description = "Departamento actualizado correctamente")
    @ApiResponse(responseCode = "404", description = "Departamento no encontrado")
    public ResponseEntity<DepartmentResponse> update(@PathVariable UUID id, @Valid @RequestBody DepartmentRequest request){
        Department updated = updateDepartmentUseCase.update(id, request.toDomain());
        return ResponseEntity.status(HttpStatus.OK).body(DepartmentResponse.from(updated));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtiene un departamento", description = "Busca un departamento por su identificador")
    @ApiResponse(responseCode = "200", description = "Departamento obtenido correctamente")
    @ApiResponse(responseCode = "404", description = "Departamento no encontrado")
    public ResponseEntity<DepartmentResponse> getDepartment(@PathVariable UUID id) {
        Department department = getDepartmentByIdUseCase.getDepartmentById(id);
        return ResponseEntity.status(HttpStatus.OK).body(DepartmentResponse.from(department));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Elimina un departamento", description = "Elimina un departamento por su identificador")
    @ApiResponse(responseCode = "204", description = "Departamento eliminado correctamente")
    @ApiResponse(responseCode = "404", description = "Departamento no encontrado")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        deleteDepartmentUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/pages")
    @Operation(summary = "Obtiene departamentos paginados", description = "Obtiene una página de departamentos usando los parámetros de paginación")
    @ApiResponse(responseCode = "200", description = "Página de departamentos obtenida correctamente")
    public ResponseEntity<PageResult<DepartmentResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "2") int size) {
        PageResult<DepartmentResponse> domainPage = pageDepartmentsUseCase.execute(new PageQuery(page, size)).map(DepartmentResponse::from);
        return ResponseEntity.status(HttpStatus.OK).body(domainPage);
    }
}
