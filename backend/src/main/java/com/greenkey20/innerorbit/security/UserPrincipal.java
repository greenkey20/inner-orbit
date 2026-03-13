package com.greenkey20.innerorbit.security;

/**
 * SecurityContext에 저장되는 인증 주체
 */
public record UserPrincipal(Long userId, String username) {
}
