package com.alexhiz.hexagonal.helpdesk.user.infrastructure.adapter.in.rest;

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

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final CreateUserUseCase createUserUseCase;

    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody UserRequest request) {
        User saved = createUserUseCase.create(request.toDomain());
        return ResponseEntity.status(HttpStatus.CREATED).body(UserResponse.from(saved));
    }
}
