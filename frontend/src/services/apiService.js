/**
 * apiService - JWT 토큰을 포함한 API 호출 래퍼
 * 401 응답 시 자동으로 로그인 페이지로 리다이렉트
 */

const TOKEN_KEY = 'inner_orbit_token';

const parseTokenPayload = (token) => {
    try {
        return JSON.parse(atob(token.split('.')[1]));
    } catch {
        return null;
    }
};

export const authService = {
    getToken: () => localStorage.getItem(TOKEN_KEY),

    setToken: (token) => localStorage.setItem(TOKEN_KEY, token),

    removeToken: () => localStorage.removeItem(TOKEN_KEY),

    isAuthenticated: () => {
        const token = localStorage.getItem(TOKEN_KEY);
        if (!token) return false;
        const payload = parseTokenPayload(token);
        if (!payload || payload.userId == null) {
            localStorage.removeItem(TOKEN_KEY);
            return false;
        }
        return true;
    },

    login: async (username, password) => {
        const response = await fetch('/api/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password }),
        });
        if (response.ok) {
            const { token } = await response.json();
            authService.setToken(token);
            return true;
        }
        return false;
    },

    logout: () => {
        authService.removeToken();
        window.dispatchEvent(new Event('auth:logout'));
    },
};

/**
 * 인증 헤더를 포함한 fetch 래퍼.
 * 401 응답 시 토큰을 제거하고 logout 이벤트를 발생시킵니다.
 */
export async function apiFetch(url, options = {}) {
    const token = authService.getToken();
    const headers = {
        'Content-Type': 'application/json',
        ...(token ? { Authorization: `Bearer ${token}` } : {}),
        ...options.headers,
    };

    const response = await fetch(url, { ...options, headers });

    if (response.status === 401) {
        authService.logout();
        return response;
    }

    return response;
}
