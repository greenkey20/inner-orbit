package com.greenkey20.innerorbit.auth.infrastructure.adapter.in.web;

import com.greenkey20.innerorbit.auth.application.port.in.AuthUseCase;
import com.greenkey20.innerorbit.auth.infrastructure.adapter.in.web.dto.LoginRequest;
import com.greenkey20.innerorbit.auth.infrastructure.adapter.in.web.dto.LoginResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthUseCase authUseCase;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            String token = authUseCase.login(request.username(), request.password());
            return ResponseEntity.ok(new LoginResponse(token));
        } catch (Exception e) {
            return ResponseEntity.status(401).build();
        }
    }
}
