package com.alexhiz.hexagonal.helpdesk.department.infrastructure.adapter.in.rest;

import com.alexhiz.hexagonal.helpdesk.category.infrastructure.adapter.in.rest.dto.CategoryResponse;
import com.alexhiz.hexagonal.helpdesk.department.application.port.in.*;
import com.alexhiz.hexagonal.helpdesk.department.domain.model.Department;
import com.alexhiz.hexagonal.helpdesk.department.infrastructure.adapter.in.rest.dto.DepartmentRequest;
import com.alexhiz.hexagonal.helpdesk.department.infrastructure.adapter.in.rest.dto.DepartmentResponse;
import com.alexhiz.hexagonal.helpdesk.shared.domain.model.PageQuery;
import com.alexhiz.hexagonal.helpdesk.shared.domain.model.PageResult;
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
    private final PageDepartmentsUseCase pageDepartmentsUseCase;

    @PostMapping
    public ResponseEntity<DepartmentResponse> create(@Valid @RequestBody DepartmentRequest request) {
        Department saved = createDepartmentUseCase.create(request.toDomain());
        return ResponseEntity.status(HttpStatus.CREATED).body(DepartmentResponse.from(saved));
    }

    @GetMapping
    public ResponseEntity<List<DepartmentResponse>> list() {
        List<DepartmentResponse> responses = listDepartmentsUseCase.getAllDepartments().stream()
                .map(DepartmentResponse::from)
                .toList();
        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DepartmentResponse> update(@PathVariable UUID id, @Valid @RequestBody DepartmentRequest request){
        Department updated = updateDepartmentUseCase.update(id, request.toDomain());
        return ResponseEntity.status(HttpStatus.OK).body(DepartmentResponse.from(updated));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DepartmentResponse> getDepartment(@PathVariable UUID id) {
        Department department = getDepartmentByIdUseCase.getDepartmentById(id);
        return ResponseEntity.status(HttpStatus.OK).body(DepartmentResponse.from(department));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        deleteDepartmentUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/pages")
    public ResponseEntity<PageResult<DepartmentResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "2") int size) {
        PageResult<DepartmentResponse> domainPage = pageDepartmentsUseCase.execute(new PageQuery(page, size)).map(DepartmentResponse::from);
        return ResponseEntity.status(HttpStatus.OK).body(domainPage);
    }
}
