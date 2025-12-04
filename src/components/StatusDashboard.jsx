import React from 'react';
import { Activity, Zap, Shield } from 'lucide-react';

/**
 * StatusDashboard - Gravity와 Stability를 조정하는 슬라이더 패널
 * 
 * @param {number} gravity - 현재 gravity 값
 * @param {number} stability - 현재 stability 값
 * @param {function} onGravityChange - gravity 변경 핸들러
 * @param {function} onStabilityChange - stability 변경 핸들러
 */
export default function StatusDashboard({ gravity, stability, onGravityChange, onStabilityChange }) {
    return (
        <section className="bg-white rounded-xl border border-slate-200 p-5 space-y-5 shadow-sm">
            <h3 className="text-[11px] font-bold text-slate-400 uppercase tracking-widest mb-2 flex items-center gap-2">
                <Activity className="w-3 h-3" /> Flight Telemetry
            </h3>

            {/* Gravity (External Influence) */}
            <div className="space-y-2">
                <div className="flex justify-between text-sm">
                    <span className="flex items-center gap-2 text-slate-600 font-medium">
                        <Zap className="w-4 h-4 text-amber-500" />
                        External Gravity (외부 인력)
                    </span>
                    <span className="font-mono font-bold text-amber-600">{gravity}%</span>
                </div>
                <input
                    type="range"
                    min="0"
                    max="100"
                    value={gravity}
                    onChange={(e) => onGravityChange(parseInt(e.target.value))}
                    className="w-full h-1.5 bg-amber-100 rounded-lg appearance-none cursor-pointer accent-amber-500"
                />
            </div>

            {/* Stability (Internal Strength) */}
            <div className="space-y-2 pt-1">
                <div className="flex justify-between text-sm">
                    <span className="flex items-center gap-2 text-slate-600 font-medium">
                        <Shield className="w-4 h-4 text-indigo-500" />
                        Core Stability (코어 안정성)
                    </span>
                    <span className="font-mono font-bold text-indigo-600">{stability}%</span>
                </div>
                <input
                    type="range"
                    min="0"
                    max="100"
                    value={stability}
                    onChange={(e) => onStabilityChange(parseInt(e.target.value))}
                    className="w-full h-1.5 bg-indigo-100 rounded-lg appearance-none cursor-pointer accent-indigo-600"
                />
            </div>
        </section>
    );
}
