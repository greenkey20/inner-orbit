import React, { useMemo } from 'react';
import { ScatterChart, Scatter, BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, Cell } from 'recharts';
import { TrendingUp, Clock, MessageSquare, Activity } from 'lucide-react';
import {
    calculateCorrelation,
    analyzeTemporalPatterns,
    extractKeywords,
    calculateResilienceIndex
} from '../utils/analytics';

/**
 * Analytics - Advanced Analytics Dashboard Component
 * "Mission Control Report" - 심층 분석 대시보드
 * 
 * @param {Object} props
 * @param {Array} props.entries - 로그 엔트리 배열
 */
export default function Analytics({ entries }) {
    // 최소 데이터 요구사항 체크
    const hasEnoughData = entries && entries.length >= 7;

    // 분석 데이터 계산 (메모이제이션)
    const correlationData = useMemo(() => calculateCorrelation(entries), [entries]);
    const temporalData = useMemo(() => analyzeTemporalPatterns(entries), [entries]);
    const keywordData = useMemo(() => extractKeywords(entries), [entries]);
    const resilienceData = useMemo(() => calculateResilienceIndex(entries), [entries]);

    // 데이터 부족 시 안내 메시지
    if (!hasEnoughData) {
        return (
            <div className="p-6 bg-white rounded-xl shadow-sm border border-slate-200">
                <div className="text-center py-12">
                    <Activity className="w-16 h-16 text-slate-300 mx-auto mb-4" />
                    <h3 className="text-lg font-bold text-slate-800 mb-2">분석 준비 중...</h3>
                    <p className="text-sm text-slate-600 mb-1">
                        의미 있는 패턴 분석을 위해 최소 7개 이상의 로그가 필요합니다.
                    </p>
                    <p className="text-xs text-slate-500">
                        현재 로그: {entries?.length || 0}개 / 필요: 7개
                    </p>
                </div>
            </div>
        );
    }

    // Scatter chart data for correlation
    const scatterData = entries.map(e => ({
        gravity: e.gravity,
        stability: e.stability,
        id: e.id
    }));

    // Bar chart data for temporal patterns
    const temporalChartData = [
        { period: '새벽\n(0-6시)', count: temporalData.dawn, key: 'dawn' },
        { period: '아침\n(6-12시)', count: temporalData.morning, key: 'morning' },
        { period: '오후\n(12-18시)', count: temporalData.afternoon, key: 'afternoon' },
        { period: '밤\n(18-24시)', count: temporalData.night, key: 'night' }
    ];

    return (
        <div className="space-y-6 animate-fade-in">
            {/* 헤더 */}
            <div className="bg-gradient-to-r from-indigo-600 to-purple-600 text-white p-6 rounded-xl shadow-lg">
                <h2 className="text-2xl font-bold mb-2">📊 Mission Control Report</h2>
                <p className="text-sm opacity-90">심층 분석 대시보드 - 당신의 감정 패턴을 수치화합니다</p>
            </div>

            {/* 1. Correlation Analysis */}
            <section className="bg-white p-6 rounded-xl shadow-sm border border-slate-200">
                <div className="flex items-center gap-2 mb-4">
                    <TrendingUp className="w-5 h-5 text-indigo-600" />
                    <h3 className="text-lg font-bold text-slate-800">상관관계 분석</h3>
                </div>

                <ResponsiveContainer width="100%" height={250}>
                    <ScatterChart margin={{ top: 10, right: 10, bottom: 10, left: 10 }}>
                        <CartesianGrid strokeDasharray="3 3" stroke="#e2e8f0" />
                        <XAxis
                            dataKey="gravity"
                            name="Gravity"
                            label={{ value: 'Gravity (%)', position: 'insideBottom', offset: -5, style: { fontSize: 12 } }}
                            domain={[0, 100]}
                        />
                        <YAxis
                            dataKey="stability"
                            name="Stability"
                            label={{ value: 'Stability (%)', angle: -90, position: 'insideLeft', style: { fontSize: 12 } }}
                            domain={[0, 100]}
                        />
                        <Tooltip
                            cursor={{ strokeDasharray: '3 3' }}
                            content={({ active, payload }) => {
                                if (active && payload && payload.length) {
                                    return (
                                        <div className="bg-white p-2 border border-slate-200 rounded shadow-lg text-xs">
                                            <p className="font-semibold">Gravity: {payload[0].value}%</p>
                                            <p className="font-semibold">Stability: {payload[1].value}%</p>
                                        </div>
                                    );
                                }
                                return null;
                            }}
                        />
                        <Scatter data={scatterData} fill="#6366f1" />
                    </ScatterChart>
                </ResponsiveContainer>

                {/* Insights */}
                <div className="mt-4 p-3 bg-indigo-50 rounded-lg border border-indigo-100">
                    {correlationData.insights.length > 0 ? (
                        <ul className="space-y-1 text-sm text-slate-700">
                            {correlationData.insights.map((insight, idx) => (
                                <li key={idx}>💡 {insight}</li>
                            ))}
                        </ul>
                    ) : (
                        <p className="text-sm text-slate-600">아직 유의미한 패턴이 발견되지 않았습니다.</p>
                    )}
                </div>
            </section>

            {/* 2. Temporal Patterns */}
            <section className="bg-white p-6 rounded-xl shadow-sm border border-slate-200">
                <div className="flex items-center gap-2 mb-4">
                    <Clock className="w-5 h-5 text-purple-600" />
                    <h3 className="text-lg font-bold text-slate-800">시간대 분석</h3>
                </div>

                <ResponsiveContainer width="100%" height={200}>
                    <BarChart data={temporalChartData} margin={{ top: 10, right: 10, bottom: 10, left: 10 }}>
                        <CartesianGrid strokeDasharray="3 3" stroke="#e2e8f0" />
                        <XAxis dataKey="period" tick={{ fontSize: 11 }} />
                        <YAxis />
                        <Tooltip
                            content={({ active, payload }) => {
                                if (active && payload && payload.length) {
                                    return (
                                        <div className="bg-white p-2 border border-slate-200 rounded shadow-lg text-xs">
                                            <p className="font-semibold">로그 수: {payload[0].value}개</p>
                                        </div>
                                    );
                                }
                                return null;
                            }}
                        />
                        <Bar dataKey="count" radius={[8, 8, 0, 0]}>
                            {temporalChartData.map((entry, index) => (
                                <Cell
                                    key={`cell-${index}`}
                                    fill={entry.key === temporalData.maxPeriod ? '#9333ea' : '#c4b5fd'}
                                />
                            ))}
                        </Bar>
                    </BarChart>
                </ResponsiveContainer>

                {/* Insights */}
                {temporalData.maxPeriod && temporalData.maxCount > 0 && (
                    <div className="mt-4 p-3 bg-purple-50 rounded-lg border border-purple-100">
                        <p className="text-sm text-slate-700">
                            🌙 {
                                temporalData.maxPeriod === 'dawn' ? '새벽 (0-6시)' :
                                    temporalData.maxPeriod === 'morning' ? '아침 (6-12시)' :
                                        temporalData.maxPeriod === 'afternoon' ? '오후 (12-18시)' : '밤 (18-24시)'
                            } 시간대에 로그가 가장 많습니다. ({temporalData.maxCount}개)
                        </p>
                    </div>
                )}
            </section>

            {/* 3. Keyword Cloud */}
            <section className="bg-white p-6 rounded-xl shadow-sm border border-slate-200">
                <div className="flex items-center gap-2 mb-4">
                    <MessageSquare className="w-5 h-5 text-pink-600" />
                    <h3 className="text-lg font-bold text-slate-800">키워드 클라우드</h3>
                    <span className="text-xs text-slate-500">(Gravity 70% 이상일 때)</span>
                </div>

                {keywordData.length > 0 ? (
                    <div className="flex flex-wrap gap-2">
                        {keywordData.map((item, idx) => {
                            const size = Math.max(12, Math.min(24, 12 + item.count * 2));
                            const opacity = Math.max(0.5, Math.min(1, 0.5 + item.count * 0.1));
                            return (
                                <span
                                    key={idx}
                                    className="inline-block px-3 py-1 rounded-full bg-pink-100 text-pink-800 font-semibold"
                                    style={{
                                        fontSize: `${size}px`,
                                        opacity: opacity
                                    }}
                                >
                                    {item.word}
                                </span>
                            );
                        })}
                    </div>
                ) : (
                    <p className="text-sm text-slate-600">Gravity 70% 이상인 로그가 없습니다.</p>
                )}
            </section>

            {/* 4. Resilience Index */}
            <section className="bg-white p-6 rounded-xl shadow-sm border border-slate-200">
                <div className="flex items-center gap-2 mb-4">
                    <Activity className="w-5 h-5 text-emerald-600" />
                    <h3 className="text-lg font-bold text-slate-800">회복 탄력성 지수</h3>
                </div>

                {resilienceData.avgRecoveryDays !== null ? (
                    <div className="space-y-3">
                        <div className="p-4 bg-gradient-to-r from-emerald-50 to-teal-50 rounded-lg border border-emerald-200">
                            <p className="text-3xl font-bold text-emerald-700 mb-1">
                                {resilienceData.avgRecoveryDays.toFixed(1)}일
                            </p>
                            <p className="text-sm text-slate-700">
                                Stability 저점에서 회복까지 평균 소요 시간
                            </p>
                            <p className="text-xs text-slate-500 mt-2">
                                회복 사례: {resilienceData.recoveryCount}회
                            </p>
                        </div>

                        {/* Trend */}
                        {resilienceData.trend && (
                            <div className="p-3 bg-slate-50 rounded-lg border border-slate-200">
                                <p className="text-sm text-slate-700">
                                    {resilienceData.trend === 'improving' && '🚀 회복 속도가 빨라지고 있습니다!'}
                                    {resilienceData.trend === 'declining' && '⚠️ 회복 속도가 느려지고 있습니다.'}
                                    {resilienceData.trend === 'stable' && '📊 회복 속도가 안정적입니다.'}
                                </p>
                            </div>
                        )}
                    </div>
                ) : (
                    <p className="text-sm text-slate-600">
                        아직 회복 패턴이 감지되지 않았습니다. (Stability 30% 이하에서 50% 이상으로 회복한 기록이 필요합니다)
                    </p>
                )}
            </section>
        </div>
    );
}
