import React, { useState } from 'react';
import { Hexagon, UserPlus } from 'lucide-react';
import { authService } from '../services/apiService';

/**
 * RegisterPage - 회원가입 페이지
 */
export default function RegisterPage({ onRegisterSuccess, onGoToLogin }) {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [passwordConfirm, setPasswordConfirm] = useState('');
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');

        if (password !== passwordConfirm) {
            setError('비밀번호가 일치하지 않습니다.');
            return;
        }

        setLoading(true);
        try {
            const result = await authService.register(username, password);
            if (result.success) {
                onRegisterSuccess();
            } else if (result.status === 409) {
                setError('이미 사용 중인 아이디입니다.');
            } else {
                setError('회원가입에 실패했습니다. 다시 시도해주세요.');
            }
        } catch {
            setError('서버에 연결할 수 없습니다. 잠시 후 다시 시도해주세요.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="min-h-screen bg-slate-900 flex items-center justify-center p-4">
            <div className="w-full max-w-sm">
                {/* Logo */}
                <div className="flex flex-col items-center mb-8">
                    <div className="flex items-center gap-2 mb-2">
                        <Hexagon className="w-8 h-8 text-primary-400 fill-primary-400/20" />
                        <h1 className="text-3xl font-bold text-white tracking-tight">Inner Orbit</h1>
                    </div>
                    <p className="text-slate-400 text-sm">Gravity Assist Protocol</p>
                </div>

                {/* Register Card */}
                <div className="bg-slate-800 rounded-2xl p-8 shadow-2xl border border-slate-700">
                    <div className="flex items-center gap-2 mb-6">
                        <UserPlus className="w-4 h-4 text-slate-400" />
                        <span className="text-slate-300 text-sm font-medium">Create Account</span>
                    </div>

                    <form onSubmit={handleSubmit} className="space-y-4">
                        <div>
                            <label className="block text-xs font-mono text-slate-400 mb-1.5 uppercase tracking-wider">
                                Username
                            </label>
                            <input
                                type="text"
                                value={username}
                                onChange={(e) => setUsername(e.target.value)}
                                required
                                minLength={3}
                                maxLength={50}
                                autoComplete="username"
                                className="w-full px-4 py-3 bg-slate-700 border border-slate-600 rounded-lg text-white placeholder-slate-500 text-sm focus:outline-none focus:border-primary-500 focus:ring-1 focus:ring-primary-500 transition-colors"
                                placeholder="3자 이상 입력"
                            />
                        </div>

                        <div>
                            <label className="block text-xs font-mono text-slate-400 mb-1.5 uppercase tracking-wider">
                                Password
                            </label>
                            <input
                                type="password"
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                required
                                minLength={4}
                                autoComplete="new-password"
                                className="w-full px-4 py-3 bg-slate-700 border border-slate-600 rounded-lg text-white placeholder-slate-500 text-sm focus:outline-none focus:border-primary-500 focus:ring-1 focus:ring-primary-500 transition-colors"
                                placeholder="4자 이상 입력"
                            />
                        </div>

                        <div>
                            <label className="block text-xs font-mono text-slate-400 mb-1.5 uppercase tracking-wider">
                                Confirm Password
                            </label>
                            <input
                                type="password"
                                value={passwordConfirm}
                                onChange={(e) => setPasswordConfirm(e.target.value)}
                                required
                                autoComplete="new-password"
                                className="w-full px-4 py-3 bg-slate-700 border border-slate-600 rounded-lg text-white placeholder-slate-500 text-sm focus:outline-none focus:border-primary-500 focus:ring-1 focus:ring-primary-500 transition-colors"
                                placeholder="비밀번호 재입력"
                            />
                        </div>

                        {error && (
                            <p className="text-red-400 text-xs py-2 px-3 bg-red-400/10 rounded-lg border border-red-400/20">
                                {error}
                            </p>
                        )}

                        <button
                            type="submit"
                            disabled={loading}
                            className="w-full py-3 bg-primary-600 hover:bg-primary-500 disabled:bg-slate-600 disabled:cursor-not-allowed text-white font-semibold rounded-lg transition-colors text-sm mt-2"
                        >
                            {loading ? 'Creating account...' : 'Sign Up'}
                        </button>
                    </form>
                </div>

                <p className="text-center text-slate-500 text-xs mt-6">
                    이미 계정이 있으신가요?{' '}
                    <button
                        onClick={onGoToLogin}
                        className="text-primary-400 hover:text-primary-300 underline transition-colors"
                    >
                        로그인
                    </button>
                </p>
            </div>
        </div>
    );
}
