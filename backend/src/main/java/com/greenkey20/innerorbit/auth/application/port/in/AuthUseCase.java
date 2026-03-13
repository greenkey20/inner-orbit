package com.greenkey20.innerorbit.auth.application.port.in;

public interface AuthUseCase {

    /**
     * 로그인 - 성공 시 JWT 토큰 반환, 실패 시 예외
     *
     * @param username 사용자명
     * @param password 비밀번호
     * @return JWT 토큰
     */
    String login(String username, String password);
}
