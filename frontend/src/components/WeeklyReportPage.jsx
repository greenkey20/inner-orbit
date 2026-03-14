import React, { useState, useEffect, useCallback } from 'react';
import { weeklyReportService } from '../services/apiService';

const STATUS_CONFIG = {
    NA: { label: '기록 없음', badgeClass: 'bg-slate-100 text-slate-500' },
    INSUFFICIENT: { label: '데이터 부족', badgeClass: 'bg-yellow-100 text-yellow-700' },
    GENERATED: { label: '리포트 생성됨', badgeClass: 'bg-emerald-100 text-emerald-700' },
};

function formatDateRange(weekStart, weekEnd) {
    const start = new Date(weekStart + 'T00:00:00');
    const end = new Date(weekEnd + 'T00:00:00');
    const fmt = (d) =>
        `${d.getFullYear()}.${String(d.getMonth() + 1).padStart(2, '0')}.${String(d.getDate()).padStart(2, '0')}`;
    return `${fmt(start)} ~ ${fmt(end)}`;
}

function ReportCard({ report, isExpanded, onToggle }) {
    const statusCfg = STATUS_CONFIG[report.status] || STATUS_CONFIG.NA;

    return (
        <div className="bg-white p-6 rounded-xl shadow-sm border border-slate-200">
            <button
                onClick={onToggle}
                className="w-full text-left"
            >
                <div className="flex items-start justify-between gap-3">
                    <div className="flex-1 min-w-0">
                        <p className="text-xs text-slate-400 font-medium mb-1">
                            {formatDateRange(report.weekStart, report.weekEnd)}
                        </p>
                        <div className="flex items-center gap-2 flex-wrap">
                            <span className={`text-xs font-semibold px-2 py-0.5 rounded-full ${statusCfg.badgeClass}`}>
                                {statusCfg.label}
                            </span>
                            <span className="text-xs text-slate-400">{report.logCount}개 로그</span>
                        </div>
                        {report.status === 'GENERATED' && report.report?.weeklyFlow && (
                            <p className="mt-2 text-sm text-slate-600 line-clamp-2">
                                {report.report.weeklyFlow}
                            </p>
                        )}
                    </div>
                    <span className="text-slate-400 text-xs mt-1 flex-shrink-0">
                        {isExpanded ? '▲' : '▼'}
                    </span>
                </div>
            </button>

            {isExpanded && report.status === 'GENERATED' && report.report && (
                <div className="mt-4 pt-4 border-t border-slate-100 space-y-4">
                    <ReportSection title="이번 주 흐름" content={report.report.weeklyFlow} />
                    <ReportSection title="발견된 패턴" content={report.report.patterns} />
                    <ReportSection title="강점 & 회복력" content={report.report.resilience} />
                    <ReportSection title="다음 주를 위한 제안" content={report.report.recommendations} />
                </div>
            )}

            {isExpanded && report.status !== 'GENERATED' && (
                <div className="mt-4 pt-4 border-t border-slate-100">
                    <p className="text-sm text-slate-400 text-center py-2">
                        {report.status === 'NA'
                            ? '이 주에는 기록된 로그가 없습니다.'
                            : '분석을 위한 로그가 충분하지 않습니다. (최소 2건 필요)'}
                    </p>
                </div>
            )}
        </div>
    );
}

function ReportSection({ title, content }) {
    return (
        <div>
            <h4 className="text-xs font-bold text-slate-500 uppercase tracking-wider mb-1">{title}</h4>
            <p className="text-sm text-slate-700 leading-relaxed">{content}</p>
        </div>
    );
}

export default function WeeklyReportPage() {
    const [reports, setReports] = useState([]);
    const [loading, setLoading] = useState(true);
    const [generating, setGenerating] = useState(false);
    const [expandedId, setExpandedId] = useState(null);
    const [error, setError] = useState(null);

    const loadReports = useCallback(async () => {
        try {
            setLoading(true);
            setError(null);
            const data = await weeklyReportService.getMyReports();
            setReports(data);
        } catch (e) {
            setError(e.message);
        } finally {
            setLoading(false);
        }
    }, []);

    useEffect(() => {
        loadReports();
    }, [loadReports]);

    const handleGenerate = async () => {
        try {
            setGenerating(true);
            setError(null);
            const newReport = await weeklyReportService.generateForCurrentWeek();
            setReports((prev) => {
                const filtered = prev.filter((r) => r.id !== newReport.id);
                return [newReport, ...filtered];
            });
        } catch (e) {
            setError(e.message);
        } finally {
            setGenerating(false);
        }
    };

    const toggleExpand = (id) => {
        setExpandedId((prev) => (prev === id ? null : id));
    };

    return (
        <div className="space-y-4">
            {/* Header */}
            <div className="bg-white p-6 rounded-xl shadow-sm border border-slate-200">
                <div className="flex items-center justify-between gap-3">
                    <div>
                        <h2 className="text-base font-bold text-slate-800">Weekly Insight</h2>
                        <p className="text-xs text-slate-400 mt-0.5">지난 주 로그를 AI가 분석한 리포트</p>
                    </div>
                    <button
                        onClick={handleGenerate}
                        disabled={generating}
                        className="text-xs font-semibold px-3 py-2 rounded-lg bg-slate-800 text-white hover:bg-slate-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors flex-shrink-0"
                    >
                        {generating ? '생성 중...' : '이번 주 리포트 생성'}
                    </button>
                </div>
            </div>

            {/* Error */}
            {error && (
                <div className="bg-red-50 border border-red-200 rounded-xl p-4">
                    <p className="text-sm text-red-600">{error}</p>
                </div>
            )}

            {/* Loading */}
            {loading && (
                <div className="bg-white p-8 rounded-xl shadow-sm border border-slate-200 text-center">
                    <p className="text-sm text-slate-400">불러오는 중...</p>
                </div>
            )}

            {/* Empty */}
            {!loading && reports.length === 0 && !error && (
                <div className="bg-white p-8 rounded-xl shadow-sm border border-slate-200 text-center">
                    <p className="text-sm text-slate-400">아직 주간 리포트가 없습니다.</p>
                    <p className="text-xs text-slate-400 mt-1">위 버튼으로 이번 주 리포트를 생성해보세요.</p>
                </div>
            )}

            {/* Report List */}
            {!loading && reports.map((report) => (
                <ReportCard
                    key={report.id}
                    report={report}
                    isExpanded={expandedId === report.id}
                    onToggle={() => toggleExpand(report.id)}
                />
            ))}
        </div>
    );
}
