import React from 'react';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';

/**
 * FlightTrajectory - 감정 궤적 시각화 컴포넌트
 * Gravity(외부 인력)와 Stability(코어 안정성)의 시간별 변화를 라인 차트로 표시
 * 
 * @param {Array} entries - 로그 엔트리 배열 (각 엔트리는 id, date, gravity, stability 포함)
 */
export default function FlightTrajectory({ entries }) {
    // 데이터 변환: entries를 recharts 형식으로 변환
    const chartData = entries.map(entry => ({
        timestamp: new Date(entry.date).getTime(), // 실제 날짜를 밀리초 타임스탬프로 변환
        gravity: entry.gravity ?? 0,
        stability: entry.stability ?? 0,
        date: entry.date, // 툴팁용
    })).sort((a, b) => a.timestamp - b.timestamp); // 시간순 정렬

    // 커스텀 툴팁
    const CustomTooltip = ({ active, payload }) => {
        if (active && payload && payload.length) {
            const data = payload[0].payload;
            return (
                <div className="bg-white p-3 rounded-lg shadow-lg border border-slate-200">
                    <p className="text-xs font-mono text-slate-500 mb-2">{data.date}</p>
                    <div className="space-y-1">
                        <div className="flex items-center gap-2">
                            <div className="w-3 h-3 rounded-full bg-secondary-500"></div>
                            <span className="text-xs font-medium">Gravity: {data.gravity}%</span>
                        </div>
                        <div className="flex items-center gap-2">
                            <div className="w-3 h-3 rounded-full bg-primary-500"></div>
                            <span className="text-xs font-medium">Stability: {data.stability}%</span>
                        </div>
                    </div>
                </div>
            );
        }
        return null;
    };

    // X축 포맷터 (타임스탬프 -> 날짜)
    const formatXAxis = (timestamp) => {
        const date = new Date(timestamp);
        const now = new Date();

        // 데이터의 연도 범위 확인
        const dataYears = [...new Set(chartData.map(d => new Date(d.timestamp).getFullYear()))];

        // 데이터가 여러 해에 걸쳐 있거나 현재 연도가 아니면 연도 포함
        if (dataYears.length > 1 || !dataYears.includes(now.getFullYear())) {
            return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
        }
        // 같은 연도면 월/일만 표시
        return `${date.getMonth() + 1}/${date.getDate()}`;
    };

    if (entries.length === 0) {
        return (
            <div className="text-center py-16 opacity-60">
                <p className="text-sm text-slate-500">
                    No flight data yet.<br />
                    Start logging to see your trajectory.
                </p>
            </div>
        );
    }

    return (
        <div className="bg-white p-5 rounded-xl shadow-sm border border-slate-200">
            {/* Header */}
            <div className="mb-4">
                <h3 className="text-sm font-bold text-slate-700 uppercase tracking-wider">
                    🚀 Flight Trajectory
                </h3>
                <p className="text-xs text-slate-500 mt-1">
                    Your emotional journey over time
                </p>
            </div>

            {/* Chart */}
            <ResponsiveContainer width="100%" height={300}>
                <LineChart data={chartData} margin={{ top: 5, right: 10, left: -20, bottom: 5 }}>
                    <CartesianGrid strokeDasharray="3 3" stroke="#e2e8f0" />
                    <XAxis
                        dataKey="timestamp"
                        type="number"
                        domain={['dataMin', 'dataMax']}
                        tickFormatter={formatXAxis}
                        stroke="#94a3b8"
                        style={{ fontSize: '11px' }}
                        tickCount={Math.min(chartData.length, 8)}
                    />
                    <YAxis
                        domain={[0, 100]}
                        stroke="#94a3b8"
                        style={{ fontSize: '11px' }}
                        tickFormatter={(value) => `${value}%`}
                    />
                    <Tooltip content={<CustomTooltip />} />
                    <Legend
                        wrapperStyle={{ fontSize: '12px', paddingTop: '10px' }}
                        iconType="line"
                    />
                    <Line
                        type="monotone"
                        dataKey="gravity"
                        stroke="#6366f1"
                        strokeWidth={2}
                        dot={{ fill: '#6366f1', r: 3 }}
                        activeDot={{ r: 5 }}
                        name="Gravity (External)"
                    />
                    <Line
                        type="monotone"
                        dataKey="stability"
                        stroke="#10b981"
                        strokeWidth={2}
                        dot={{ fill: '#10b981', r: 3 }}
                        activeDot={{ r: 5 }}
                        name="Stability (Internal)"
                    />
                </LineChart>
            </ResponsiveContainer>
        </div>
    );
}
