package com.alexhiz.hexagonal.helpdesk.auth.infrastructure.adapter.in.rest;

import com.alexhiz.hexagonal.helpdesk.auth.application.port.in.LoginAuthUseCase;
import com.alexhiz.hexagonal.helpdesk.auth.domain.model.Auth;
import com.alexhiz.hexagonal.helpdesk.auth.infrastructure.adapter.in.rest.dto.AuthLoginRequest;
import com.alexhiz.hexagonal.helpdesk.auth.infrastructure.adapter.in.rest.dto.AuthResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final LoginAuthUseCase loginAuthUseCase;
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody  AuthLoginRequest request){
        Auth auth = loginAuthUseCase.login(request.toDomain());
        return ResponseEntity.status(HttpStatus.OK).body(AuthResponse.from(auth));
    }
}
