package com.greenkey20.innerorbit.auth.application.service;

import com.greenkey20.innerorbit.auth.application.port.in.AuthUseCase;
import com.greenkey20.innerorbit.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService implements AuthUseCase {

    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.username}")
    private String adminUsername;

    @Value("${admin.password}")
    private String adminPassword;

    @Override
    public String login(String username, String password) {
        if (!adminUsername.equals(username)) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        // 운영 환경에서 ADMIN_PASSWORD는 BCrypt hash로 설정 가능.
        // 로컬 개발용 평문 비밀번호도 지원하기 위해 두 방식 모두 처리.
        boolean passwordMatches;
        if (adminPassword.startsWith("$2")) {
            // BCrypt hash
            passwordMatches = passwordEncoder.matches(password, adminPassword);
        } else {
            // 평문 (개발 환경 전용)
            passwordMatches = adminPassword.equals(password);
        }

        if (!passwordMatches) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        return jwtUtil.generateToken(adminUsername);
    }
}
