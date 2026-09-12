package com.alexhiz.hexagonal.helpdesk.user.infrastructure.adapter.in.rest;

import com.alexhiz.hexagonal.helpdesk.category.application.port.in.UpdateCategoryUseCase;
import com.alexhiz.hexagonal.helpdesk.department.application.port.in.GetDepartmentByIdUseCase;
import com.alexhiz.hexagonal.helpdesk.department.domain.model.Department;
import com.alexhiz.hexagonal.helpdesk.shared.domain.model.PageQuery;
import com.alexhiz.hexagonal.helpdesk.shared.domain.model.PageResult;
import com.alexhiz.hexagonal.helpdesk.user.application.port.in.*;
import com.alexhiz.hexagonal.helpdesk.user.domain.model.User;
import com.alexhiz.hexagonal.helpdesk.user.infrastructure.adapter.in.rest.dto.UserRequest;
import com.alexhiz.hexagonal.helpdesk.user.infrastructure.adapter.in.rest.dto.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final CreateUserUseCase createUserUseCase;
    private final PageUsersUseCase pageUsersUseCase;
    private final GetUserByIdUseCase getUserByIdUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    private final DeleteUserUseCase deleteUserUseCase;

    private final GetDepartmentByIdUseCase getDepartmentByIdUseCase;

    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody UserRequest request) {
        User saved = createUserUseCase.create(request.toDomain());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(saved));
    }

    @GetMapping("/pages")
    public ResponseEntity<PageResult<UserResponse>> getAllPages(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "2") int size){
        PageResult<UserResponse> response = pageUsersUseCase.execute(new PageQuery(page,size)).map(this::toResponse);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getById(@PathVariable UUID id){
        User user = getUserByIdUseCase.getUserById(id);
        return ResponseEntity.status(HttpStatus.OK).body(toResponse(user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable UUID id, @Valid @RequestBody UserRequest request){
        User user = updateUserUseCase.update(id, request.toDomain());
        return ResponseEntity.status(HttpStatus.OK).body(toResponse(user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id){
        deleteUserUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }

    private UserResponse toResponse(User user) {
        Department department = user.getDepartmentId() == null
                ? null
                : getDepartmentByIdUseCase.getDepartmentById(user.getDepartmentId());

        return UserResponse.from(user, department);
    }
}
