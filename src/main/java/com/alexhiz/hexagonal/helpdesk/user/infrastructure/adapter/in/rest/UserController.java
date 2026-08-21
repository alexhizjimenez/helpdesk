package com.alexhiz.hexagonal.helpdesk.user.infrastructure.adapter.in.rest;

import com.alexhiz.hexagonal.helpdesk.role.domain.model.Role;
import com.alexhiz.hexagonal.helpdesk.user.application.port.in.CreateUserUseCase;
import com.alexhiz.hexagonal.helpdesk.user.domain.model.User;
import com.alexhiz.hexagonal.helpdesk.user.infrastructure.adapter.in.rest.dto.UserRequest;
import com.alexhiz.hexagonal.helpdesk.user.infrastructure.adapter.in.rest.dto.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final CreateUserUseCase createUserUseCase;

    @PostMapping("/")
    public ResponseEntity<UserResponse> create(@Valid @RequestBody UserRequest request) {
        User user = User.builder()
                .fullName(request.fullName())
                .email(request.email())
                .password(request.password())
                .phone(request.phone())
                .departmentId(request.departmentId())
                .roles(request.roleIds() != null
                        ? request.roleIds().stream()
                                .map(roleId -> Role.builder().id(roleId).build())
                                .collect(Collectors.toSet())
                        : null)
                .build();
        User saved = createUserUseCase.create(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(UserResponse.from(saved));
    }
}
