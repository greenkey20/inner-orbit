import React, { useState } from 'react';
import { Hexagon, Lock } from 'lucide-react';
import { authService } from '../services/apiService';

/**
 * LoginPage - 단일 관리자 계정 로그인 페이지
 */
export default function LoginPage({ onLoginSuccess }) {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setLoading(true);

        try {
            const success = await authService.login(username, password);
            if (success) {
                onLoginSuccess();
            } else {
                setError('아이디 또는 비밀번호가 올바르지 않습니다.');
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

                {/* Login Card */}
                <div className="bg-slate-800 rounded-2xl p-8 shadow-2xl border border-slate-700">
                    <div className="flex items-center gap-2 mb-6">
                        <Lock className="w-4 h-4 text-slate-400" />
                        <span className="text-slate-300 text-sm font-medium">System Authentication</span>
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
                                autoComplete="username"
                                className="w-full px-4 py-3 bg-slate-700 border border-slate-600 rounded-lg text-white placeholder-slate-500 text-sm focus:outline-none focus:border-primary-500 focus:ring-1 focus:ring-primary-500 transition-colors"
                                placeholder="Enter username"
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
                                autoComplete="current-password"
                                className="w-full px-4 py-3 bg-slate-700 border border-slate-600 rounded-lg text-white placeholder-slate-500 text-sm focus:outline-none focus:border-primary-500 focus:ring-1 focus:ring-primary-500 transition-colors"
                                placeholder="Enter password"
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
                            {loading ? 'Authenticating...' : 'Login'}
                        </button>
                    </form>
                </div>

                <p className="text-center text-slate-600 text-xs mt-6 font-mono">
                    Private access only
                </p>
            </div>

            <style>{`
                @keyframes fade-in { from { opacity: 0; transform: translateY(10px); } to { opacity: 1; transform: translateY(0); } }
            `}</style>
        </div>
    );
}
