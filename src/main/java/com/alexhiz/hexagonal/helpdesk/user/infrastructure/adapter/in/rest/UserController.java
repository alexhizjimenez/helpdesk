package com.alexhiz.hexagonal.helpdesk.user.infrastructure.adapter.in.rest;

import com.alexhiz.hexagonal.helpdesk.department.application.port.in.GetDepartmentByIdUseCase;
import com.alexhiz.hexagonal.helpdesk.department.domain.model.Department;
import com.alexhiz.hexagonal.helpdesk.shared.domain.model.PageQuery;
import com.alexhiz.hexagonal.helpdesk.shared.domain.model.PageResult;
import com.alexhiz.hexagonal.helpdesk.user.application.port.in.*;
import com.alexhiz.hexagonal.helpdesk.user.domain.model.User;
import com.alexhiz.hexagonal.helpdesk.user.infrastructure.adapter.in.rest.dto.UserRequest;
import com.alexhiz.hexagonal.helpdesk.user.infrastructure.adapter.in.rest.dto.UserResponse;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Operaciones para gestionar los usuarios")
public class UserController {

    private final CreateUserUseCase createUserUseCase;
    private final PageUsersUseCase pageUsersUseCase;
    private final GetUserByIdUseCase getUserByIdUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    private final DeleteUserUseCase deleteUserUseCase;

    private final GetDepartmentByIdUseCase getDepartmentByIdUseCase;

    @PostMapping
    @Operation(summary = "Crea un usuario", description = "Crea un nuevo usuario")
    @ApiResponse(responseCode = "201", description = "Usuario creado correctamente")
    @ApiResponse(responseCode = "400", description = "Datos del usuario inválidos")
    public ResponseEntity<UserResponse> create(@Valid @RequestBody UserRequest request) {
        User saved = createUserUseCase.create(request.toDomain());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(saved));
    }

    @GetMapping("/pages")
    @Operation(summary = "Obtiene usuarios paginados", description = "Obtiene una página de usuarios usando los parámetros de paginación")
    @ApiResponse(responseCode = "200", description = "Página de usuarios obtenida correctamente")
    public ResponseEntity<PageResult<UserResponse>> getAllPages(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "2") int size) {
        PageResult<UserResponse> response = pageUsersUseCase.execute(new PageQuery(page, size)).map(this::toResponse);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtiene un usuario", description = "Busca un usuario por su identificador")
    @ApiResponse(responseCode = "200", description = "Usuario obtenido correctamente")
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    public ResponseEntity<UserResponse> getById(@PathVariable UUID id) {
        User user = getUserByIdUseCase.getUserById(id);
        return ResponseEntity.status(HttpStatus.OK).body(toResponse(user));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualiza un usuario", description = "Actualiza los datos de un usuario existente")
    @ApiResponse(responseCode = "200", description = "Usuario actualizado correctamente")
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    public ResponseEntity<UserResponse> updateUser(@PathVariable UUID id, @Valid @RequestBody UserRequest request) {
        User user = updateUserUseCase.update(id, request.toDomain());
        return ResponseEntity.status(HttpStatus.OK).body(toResponse(user));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Elimina un usuario", description = "Elimina un usuario por su identificador")
    @ApiResponse(responseCode = "204", description = "Usuario eliminado correctamente")
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
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
