package com.greenkey20.innerorbit.auth.controller;

import com.greenkey20.innerorbit.auth.dto.LoginRequest;
import com.greenkey20.innerorbit.auth.dto.LoginResponse;
import com.greenkey20.innerorbit.security.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.username}")
    private String adminUsername;

    @Value("${admin.password}")
    private String adminPassword;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        if (!adminUsername.equals(request.username())) {
            return ResponseEntity.status(401).build();
        }

        // 운영 환경에서 ADMIN_PASSWORD는 BCrypt hash로 설정 가능.
        // 로컬 개발용 평문 비밀번호도 지원하기 위해 두 방식 모두 처리.
        boolean passwordMatches;
        if (adminPassword.startsWith("$2")) {
            // BCrypt hash
            passwordMatches = passwordEncoder.matches(request.password(), adminPassword);
        } else {
            // 평문 (개발 환경 전용)
            passwordMatches = adminPassword.equals(request.password());
        }

        if (!passwordMatches) {
            return ResponseEntity.status(401).build();
        }

        String token = jwtUtil.generateToken(adminUsername);
        return ResponseEntity.ok(new LoginResponse(token));
    }
}
