package com.alexhiz.hexagonal.helpdesk.department.infrastructure.adapter.in.rest;

import com.alexhiz.hexagonal.helpdesk.department.application.port.in.CreateDepartmentUseCase;
import com.alexhiz.hexagonal.helpdesk.department.domain.model.Department;
import com.alexhiz.hexagonal.helpdesk.department.infrastructure.adapter.in.rest.dto.DepartmentRequest;
import com.alexhiz.hexagonal.helpdesk.department.infrastructure.adapter.in.rest.dto.DepartmentResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final CreateDepartmentUseCase createDepartmentUseCase;

    @PostMapping("/")
    public ResponseEntity<DepartmentResponse> create(@Valid @RequestBody DepartmentRequest request) {
        Department department = Department.builder()
                .name(request.name())
                .active(request.active())
                .build();
        Department saved = createDepartmentUseCase.create(department);
        return ResponseEntity.status(HttpStatus.CREATED).body(DepartmentResponse.from(saved));
    }
}
