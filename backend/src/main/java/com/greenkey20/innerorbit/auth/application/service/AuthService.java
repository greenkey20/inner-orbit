package com.greenkey20.innerorbit.auth.application.service;

import com.greenkey20.innerorbit.auth.application.port.in.AuthUseCase;
import com.greenkey20.innerorbit.auth.application.port.out.UserRepository;
import com.greenkey20.innerorbit.auth.domain.model.User;
import com.greenkey20.innerorbit.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService implements AuthUseCase {

    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Override
    public String login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        return jwtUtil.generateToken(user.getUsername(), user.getId());
    }

    @Override
    public String register(String username, String password) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalStateException("Username already exists");
        }

        User saved = userRepository.save(User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .build());

        return jwtUtil.generateToken(saved.getUsername(), saved.getId());
    }
}
