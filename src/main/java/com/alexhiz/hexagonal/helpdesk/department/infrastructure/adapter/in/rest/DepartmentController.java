package com.alexhiz.hexagonal.helpdesk.department.infrastructure.adapter.in.rest;

import com.alexhiz.hexagonal.helpdesk.department.application.port.in.*;
import com.alexhiz.hexagonal.helpdesk.department.domain.exception.DepartmentNotFoundException;
import com.alexhiz.hexagonal.helpdesk.department.domain.model.Department;
import com.alexhiz.hexagonal.helpdesk.department.infrastructure.adapter.in.rest.dto.DepartmentRequest;
import com.alexhiz.hexagonal.helpdesk.department.infrastructure.adapter.in.rest.dto.DepartmentResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final CreateDepartmentUseCase createDepartmentUseCase;
    private final ListDepartmentsUseCase listDepartmentsUseCase;
    private final DeleteDepartmentUseCase deleteDepartmentUseCase;
    private final GetDepartmentByIdUseCase getDepartmentByIdUseCase;
    private final UpdateDepartmentUseCase updateDepartmentUseCase;

    @PostMapping
    public ResponseEntity<DepartmentResponse> create(@Valid @RequestBody DepartmentRequest request) {
        Department saved = createDepartmentUseCase.create(request.toDomain());
        return ResponseEntity.status(HttpStatus.CREATED).body(DepartmentResponse.from(saved));
    }

    @GetMapping
    public ResponseEntity<List<DepartmentResponse>> list() {
        List<DepartmentResponse> responses = listDepartmentsUseCase.listDepartments().stream()
                .map(DepartmentResponse::from)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DepartmentResponse> update(@PathVariable UUID id, @Valid @RequestBody DepartmentRequest request){
        Department updated = updateDepartmentUseCase.updateDepartment(id, request.toDomain());
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(DepartmentResponse.from(updated));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DepartmentResponse> getDepartment(@PathVariable UUID id) {
        Department department = getDepartmentByIdUseCase.getDepartmentById(id)
                .orElseThrow(() -> new DepartmentNotFoundException(id));
        return ResponseEntity.ok(DepartmentResponse.from(department));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        deleteDepartmentUseCase.deleteDepartmentById(id);
        return ResponseEntity.noContent().build();
    }
}
