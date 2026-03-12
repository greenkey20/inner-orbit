import React, { useState, useEffect, useRef } from 'react';
import { Calendar, Trash2, Download, Upload, AlertCircle, Hexagon, Pencil, Check, X, Zap, Shield, Sparkles, MapPin, Eye, Ear, Hand, Lightbulb } from 'lucide-react';
import { apiFetch } from '../services/apiService';

// UI 컴포넌트: 카드
const Card = ({ children, className = "" }) => (
    <div className={`bg-white rounded-xl shadow-sm border border-slate-200 ${className}`}>
        {children}
    </div>
);

// UI 컴포넌트: 버튼
const Button = ({ onClick, children, variant = "primary", className = "", ...props }) => {
    const baseStyle = "px-4 py-2 rounded-lg font-medium transition-all duration-200 flex items-center justify-center gap-2";
    const variants = {
        primary: "bg-slate-800 text-white hover:bg-slate-900 shadow-md hover:shadow-lg disabled:opacity-50 disabled:cursor-not-allowed",
        secondary: "bg-primary-50 text-primary-700 hover:bg-primary-100 border border-primary-100",
        outline: "border border-slate-300 text-slate-600 hover:border-slate-400 hover:bg-slate-50",
        ghost: "text-slate-400 hover:text-slate-700 hover:bg-slate-100",
        danger: "bg-rose-50 text-rose-500 hover:bg-rose-100"
    };
    return (
        <button onClick={onClick} className={`${baseStyle} ${variants[variant]} ${className}`} {...props}>
            {children}
        </button>
    );
};

/**
 * LogHistory - 저장된 로그 목록 및 백업/복원 기능
 * 
 * @param {Array} entries - 로그 엔트리 배열
 * @param {function} onDeleteEntry - 삭제 핸들러
 * @param {function} onUpdateEntry - 수정 핸들러
 * @param {function} onDownloadData - 백업 핸들러
 * @param {function} onFileUpload - 복원 핸들러
 * @param {React.RefObject} fileInputRef - 파일 입력 ref
 */
export default function LogHistory({ entries, onDeleteEntry, onUpdateEntry, onUpdateEntryAnalysis, onDownloadData, onFileUpload, fileInputRef }) {
    // 수정 모드 상태 관리
    const [editingId, setEditingId] = useState(null);
    const [editContent, setEditContent] = useState('');
    const [editGravity, setEditGravity] = useState(50);
    const [editStability, setEditStability] = useState(50);

    // Deep Log 필드 상태
    const [editLocation, setEditLocation] = useState('');
    const [editSensoryVisual, setEditSensoryVisual] = useState('');
    const [editSensoryAuditory, setEditSensoryAuditory] = useState('');
    const [editSensoryTactile, setEditSensoryTactile] = useState('');

    // Insight Log 필드 상태
    const [editInsightAbstraction, setEditInsightAbstraction] = useState('');
    const [editInsightApplication, setEditInsightApplication] = useState('');

    // AI 피드백 요청 상태
    const [requestingFeedback, setRequestingFeedback] = useState(null);
    const [feedbackResults, setFeedbackResults] = useState({});
    const [feedbackError, setFeedbackError] = useState(null);

    // 페이지네이션 상태
    const [visibleCount, setVisibleCount] = useState(10);
    const observerRef = useRef(null);
    const sentinelRef = useRef(null);

    // AI Co-Pilot 상태
    const [analyzingId, setAnalyzingId] = useState(null);
    const [analysisResults, setAnalysisResults] = useState({});

    // ISO 8601 → 한글 날짜 표시 변환 함수
    const formatDateKorean = (isoDateStr) => {
        if (!isoDateStr) return '';

        const date = new Date(isoDateStr);
        const year = date.getFullYear();
        const month = date.getMonth() + 1;
        const day = date.getDate();
        const hour = String(date.getHours()).padStart(2, '0');
        const minute = String(date.getMinutes()).padStart(2, '0');

        return `${year}년 ${month}월 ${day}일 ${hour}:${minute}`;
    };
    const [analysisError, setAnalysisError] = useState(null);

    // 무한 스크롤 Observer 설정
    useEffect(() => {
        const observer = new IntersectionObserver((entries) => {
            if (entries[0].isIntersecting) {
                setVisibleCount((prev) => prev + 10);
            }
        }, { threshold: 1.0 });

        if (sentinelRef.current) {
            observer.observe(sentinelRef.current);
        }

        return () => {
            if (sentinelRef.current) {
                observer.unobserve(sentinelRef.current);
            }
        };
    }, []);

    // 저장된 분석 결과 로드 (localStorage에서)
    useEffect(() => {
        const savedResults = {};
        entries.forEach(entry => {
            if (entry.analysis) {
                savedResults[entry.id] = entry.analysis;
            }
        });
        setAnalysisResults(savedResults);
    }, [entries]);

    // 수정 모드 진입
    const startEdit = (entry) => {
        setEditingId(entry.id);
        setEditContent(entry.content);
        setEditGravity(entry.gravity || 50);
        setEditStability(entry.stability || 50);

        // Deep Log 필드 로드
        if (entry.isDeepLog) {
            setEditLocation(entry.location || '');
            setEditSensoryVisual(entry.sensoryVisual || '');
            setEditSensoryAuditory(entry.sensoryAuditory || '');
            setEditSensoryTactile(entry.sensoryTactile || '');
        }

        // Insight Log 필드 로드
        if (entry.logType === 'INSIGHT') {
            setEditInsightAbstraction(entry.insightAbstraction || '');
            setEditInsightApplication(entry.insightApplication || '');
        }
    };

    // 수정 저장
    const saveEdit = () => {
        const entry = entries.find(e => e.id === editingId);
        let logFields = null;

        // Deep Log 엔트리인 경우 감각 필드도 함께 전달
        if (entry && entry.isDeepLog) {
            logFields = {
                location: editLocation,
                sensoryVisual: editSensoryVisual,
                sensoryAuditory: editSensoryAuditory,
                sensoryTactile: editSensoryTactile,
                logType: 'SENSORY'
            };
        }
        // Insight Log 엔트리인 경우 통찰 필드도 함께 전달
        else if (entry && entry.logType === 'INSIGHT') {
            logFields = {
                insightAbstraction: editInsightAbstraction,
                insightApplication: editInsightApplication,
                logType: 'INSIGHT'
            };
        }

        onUpdateEntry(editingId, editContent, editGravity, editStability, logFields);
        setEditingId(null);
    };

    // 수정 취소
    const cancelEdit = () => {
        setEditingId(null);
        setEditContent('');

        // Deep Log state 초기화
        setEditLocation('');
        setEditSensoryVisual('');
        setEditSensoryAuditory('');
        setEditSensoryTactile('');

        // Insight Log state 초기화
        setEditInsightTrigger('');
        setEditInsightAbstraction('');
        setEditInsightApplication('');
    };

    // AI 분석 실행 - 백엔드 API 호출
    const handleDecryptLog = async (entry) => {
        console.log('🔵 Decrypt Log button clicked! Entry ID:', entry.id);
        console.log('Entry content:', entry.content);
        setAnalyzingId(entry.id);
        setAnalysisError(null);

        try {
            // 백엔드 API 호출
            const response = await apiFetch(`/api/logs/${entry.id}/analyze`, {
                method: 'POST'
            });

            if (!response.ok) {
                const errorData = await response.json().catch(() => ({}));
                throw new Error(errorData.message || `서버 오류: ${response.status}`);
            }

            const data = await response.json();
            console.log('✅ Backend Response:', data); // 디버깅용

            // 백엔드 응답에서 analysisResult 추출
            const result = data.analysisResult || {};
            console.log('  - Distortions:', result.distortions);
            console.log('  - Reframed:', result.reframed);
            console.log('  - Alternative:', result.alternative);

            // 분석 결과를 localStorage에 영구 저장
            onUpdateEntryAnalysis(entry.id, result);

            setAnalysisResults(prev => ({
                ...prev,
                [entry.id]: result
            }));
        } catch (error) {
            console.error('❌ Analysis Error:', error); // 디버깅용
            setAnalysisError(error.message);
        } finally {
            setAnalyzingId(null);
        }
    };

    // Insight Log AI 피드백 요청
    const handleRequestFeedback = async (entry) => {
        console.log('🟣 Request Feedback button clicked! Entry ID:', entry.id);
        setRequestingFeedback(entry.id);
        setFeedbackError(null);

        try {
            const response = await apiFetch(`/api/logs/${entry.id}/request-feedback`, {
                method: 'POST'
            });

            if (!response.ok) {
                const errorData = await response.json().catch(() => ({}));
                throw new Error(errorData.message || `피드백 생성 실패: ${response.status}`);
            }

            const updatedLog = await response.json();
            console.log('✅ AI Feedback Response:', updatedLog);

            // 피드백 결과를 localStorage에 영구 저장 (Decrypt Log와 동일한 방식)
            onUpdateEntryAnalysis(entry.id, updatedLog.aiFeedback);

            setFeedbackResults(prev => ({
                ...prev,
                [entry.id]: updatedLog.aiFeedback
            }));

            console.log('AI 피드백이 성공적으로 생성되었습니다.');

        } catch (error) {
            console.error('❌ Feedback Request Error:', error);
            setFeedbackError(error.message);
        } finally {
            setRequestingFeedback(null);
        }
    };

    return (
        <div className="space-y-6 animate-fade-in">
            {entries.length === 0 ? (
                <div className="text-center py-16 opacity-60">
                    <Hexagon className="w-10 h-10 mx-auto mb-4 text-slate-300" />
                    <p className="text-sm text-slate-500">No logs found.<br />Initialize your first entry.</p>
                </div>
            ) : (
                entries.slice(0, visibleCount).map((entry, index) => {
                    const isEditing = editingId === entry.id;
                    // Calculate log sequence number (oldest = #1)
                    const sortedEntries = [...entries].sort((a, b) => a.id - b.id);
                    const logNumber = sortedEntries.findIndex(e => e.id === entry.id) + 1;

                    return (
                        <Card
                            key={entry.id}
                            className={`p-5 transition-all group ${isEditing
                                ? 'ring-2 ring-primary-500 shadow-lg'
                                : 'hover:shadow-md'
                                }`}
                        >
                            {/* Header: Date and Action Buttons */}
                            <div className="flex justify-between items-start mb-3 border-b border-slate-100 pb-3">
                                <div className="flex flex-col gap-1">
                                    <div className="flex items-center gap-2">
                                        <Calendar className="w-3 h-3 text-slate-400" />
                                        <span className="text-xs font-mono font-medium text-slate-500">
                                            {formatDateKorean(entry.date)}
                                        </span>
                                    </div>
                                    {/* Audit Timestamp - Show only if actually updated */}
                                    {entry.updatedAt && entry.updatedAt !== entry.date && (
                                        <div className="flex items-center gap-2 ml-5">
                                            <span className="text-[10px] font-mono text-slate-400">
                                                (수정: {formatDateKorean(entry.updatedAt)})
                                            </span>
                                        </div>
                                    )}
                                    {/* Log Sequence Number */}
                                    <div className="flex items-center gap-2">
                                        <span className="text-[10px] font-mono text-slate-400">
                                            Log Sequence #{logNumber}
                                        </span>
                                    </div>
                                </div>

                                <div className="flex gap-2">
                                    {isEditing ? (
                                        <>
                                            <button
                                                onClick={saveEdit}
                                                className="text-green-500 hover:text-green-700 transition-colors"
                                                title="저장"
                                            >
                                                <Check className="w-4 h-4" />
                                            </button>
                                            <button
                                                onClick={cancelEdit}
                                                className="text-slate-400 hover:text-slate-600 transition-colors"
                                                title="취소"
                                            >
                                                <X className="w-4 h-4" />
                                            </button>
                                        </>
                                    ) : (
                                        <>
                                            <button
                                                onClick={() => startEdit(entry)}
                                                className="text-slate-300 hover:text-primary-500 opacity-0 group-hover:opacity-100 transition-all"
                                                title="수정"
                                            >
                                                <Pencil className="w-3 h-3" />
                                            </button>
                                            <button
                                                onClick={() => onDeleteEntry(entry.id)}
                                                className="text-slate-300 hover:text-rose-500 opacity-0 group-hover:opacity-100 transition-all"
                                                title="삭제"
                                            >
                                                <Trash2 className="w-3 h-3" />
                                            </button>
                                        </>
                                    )}
                                </div>
                            </div>


                            {/* Content: Text or Textarea (Insight Log는 content 필드 없음) */}
                            {entry.logType !== 'INSIGHT' && (
                                <div className="mb-5">
                                    {isEditing ? (
                                        <textarea
                                            value={editContent}
                                            onChange={(e) => setEditContent(e.target.value)}
                                            className="w-full min-h-[120px] p-3 bg-slate-50 border border-primary-200 rounded-lg resize-none focus:ring-2 focus:ring-primary-300 focus:border-primary-400 text-sm text-slate-700 leading-relaxed font-sans transition-all"
                                            placeholder="로그 내용을 입력하세요..."
                                        />
                                    ) : (
                                        <div className="text-sm text-slate-700 whitespace-pre-wrap leading-relaxed font-sans">
                                            {entry.content}
                                        </div>
                                    )}
                                </div>
                            )}

                            {/* Deep Log Data - 감각 정보 표시/수정 */}
                            {entry.isDeepLog && (
                                <div className="mb-5 p-4 bg-gradient-to-r from-lime-50 to-emerald-50 rounded-xl border border-lime-200">
                                    <div className="flex items-center gap-2 mb-3">
                                        <span className="text-xs font-bold text-lime-800 uppercase tracking-wider">✈️ Deep Travel Log</span>
                                    </div>

                                    {/* Warning for Deep Log Editing */}
                                    {isEditing && (
                                        <div className="mb-3 p-2.5 bg-amber-50 border border-amber-200 rounded-lg">
                                            <p className="text-xs text-gray-600 flex items-center gap-1.5">
                                                <span>⚠️</span>
                                                <span>주의: Deep Log는 순간의 감각을 기록합니다. 수정 시 원본 경험이 변경될 수 있습니다.</span>
                                            </p>
                                        </div>
                                    )}

                                    {isEditing ? (
                                        /* Edit Mode - Input Fields */
                                        <>
                                            {/* Location Input */}
                                            <div className="mb-3">
                                                <label className="flex items-center gap-2 text-xs font-bold text-slate-700 mb-2">
                                                    <MapPin className="w-4 h-4 text-lime-600" />
                                                    Location
                                                </label>
                                                <input
                                                    type="text"
                                                    value={editLocation}
                                                    onChange={(e) => setEditLocation(e.target.value)}
                                                    placeholder="Where are you now?"
                                                    className="w-full px-3 py-2 bg-white border border-lime-300 rounded-lg focus:ring-2 focus:ring-lime-400 focus:border-lime-400 text-sm text-slate-700 placeholder:text-lime-400"
                                                />
                                            </div>

                                            {/* Sensory Inputs Grid */}
                                            <div className="grid grid-cols-1 md:grid-cols-3 gap-3">
                                                {/* Visual */}
                                                <div>
                                                    <label className="flex items-center gap-2 text-xs font-bold text-slate-700 mb-2">
                                                        <Eye className="w-4 h-4 text-emerald-600" />
                                                        보이는 것
                                                    </label>
                                                    <textarea
                                                        value={editSensoryVisual}
                                                        onChange={(e) => setEditSensoryVisual(e.target.value)}
                                                        placeholder="무엇이 보이나요?"
                                                        className="w-full h-20 px-3 py-2 bg-white border border-emerald-300 rounded-lg resize-none focus:ring-2 focus:ring-emerald-400 focus:border-emerald-400 text-sm text-slate-700 placeholder:text-emerald-400"
                                                    />
                                                </div>

                                                {/* Auditory */}
                                                <div>
                                                    <label className="flex items-center gap-2 text-xs font-bold text-slate-700 mb-2">
                                                        <Ear className="w-4 h-4 text-lime-600" />
                                                        들리는 것
                                                    </label>
                                                    <textarea
                                                        value={editSensoryAuditory}
                                                        onChange={(e) => setEditSensoryAuditory(e.target.value)}
                                                        placeholder="무엇이 들리나요?"
                                                        className="w-full h-20 px-3 py-2 bg-white border border-lime-300 rounded-lg resize-none focus:ring-2 focus:ring-lime-400 focus:border-lime-400 text-sm text-slate-700 placeholder:text-lime-400"
                                                    />
                                                </div>

                                                {/* Tactile */}
                                                <div>
                                                    <label className="flex items-center gap-2 text-xs font-bold text-slate-700 mb-2">
                                                        <Hand className="w-4 h-4 text-green-600" />
                                                        느껴지는 것
                                                    </label>
                                                    <textarea
                                                        value={editSensoryTactile}
                                                        onChange={(e) => setEditSensoryTactile(e.target.value)}
                                                        placeholder="무엇이 느껴지나요?"
                                                        className="w-full h-20 px-3 py-2 bg-white border border-green-300 rounded-lg resize-none focus:ring-2 focus:ring-green-400 focus:border-green-400 text-sm text-slate-700 placeholder:text-green-400"
                                                    />
                                                </div>
                                            </div>
                                        </>
                                    ) : (
                                        /* View Mode - Display Data */
                                        <>
                                            {/* Location */}
                                            {entry.location && (
                                                <div className="mb-3 flex items-start gap-2">
                                                    <MapPin className="w-4 h-4 text-lime-600 mt-0.5 flex-shrink-0" />
                                                    <div>
                                                        <span className="text-xs font-bold text-slate-700 block mb-1">Location</span>
                                                        <span className="text-sm text-slate-600">{entry.location}</span>
                                                    </div>
                                                </div>
                                            )}

                                            {/* Sensory Grid */}
                                            <div className="grid grid-cols-1 md:grid-cols-3 gap-3">
                                                {/* Visual */}
                                                {entry.sensoryVisual && (
                                                    <div className="flex items-start gap-2">
                                                        <Eye className="w-4 h-4 text-emerald-600 mt-0.5 flex-shrink-0" />
                                                        <div>
                                                            <span className="text-xs font-bold text-slate-700 block mb-1">보이는 것</span>
                                                            <span className="text-sm text-slate-600">{entry.sensoryVisual}</span>
                                                        </div>
                                                    </div>
                                                )}

                                                {/* Auditory */}
                                                {entry.sensoryAuditory && (
                                                    <div className="flex items-start gap-2">
                                                        <Ear className="w-4 h-4 text-lime-600 mt-0.5 flex-shrink-0" />
                                                        <div>
                                                            <span className="text-xs font-bold text-slate-700 block mb-1">들리는 것</span>
                                                            <span className="text-sm text-slate-600">{entry.sensoryAuditory}</span>
                                                        </div>
                                                    </div>
                                                )}

                                                {/* Tactile */}
                                                {entry.sensoryTactile && (
                                                    <div className="flex items-start gap-2">
                                                        <Hand className="w-4 h-4 text-green-600 mt-0.5 flex-shrink-0" />
                                                        <div>
                                                            <span className="text-xs font-bold text-slate-700 block mb-1">느껴지는 것</span>
                                                            <span className="text-sm text-slate-600">{entry.sensoryTactile}</span>
                                                        </div>
                                                    </div>
                                                )}
                                            </div>
                                        </>
                                    )}
                                </div>
                            )}

                            {/* Insight Log Data - 통찰 정보 표시/수정 */}
                            {entry.logType === 'INSIGHT' && (
                                <div className="mb-5 p-4 bg-gradient-to-r from-teal-50 to-cyan-50 rounded-xl border border-teal-200">
                                    <div className="flex items-center gap-2 mb-3">
                                        <span className="text-xs font-bold text-teal-800 uppercase tracking-wider">💡 Architecture of Insight</span>
                                    </div>

                                    {isEditing ? (
                                        /* Edit Mode - Input Fields */
                                        <>
                                            {/* Observation (Content) Input */}
                                            <div className="mb-3">
                                                <label className="flex items-center gap-2 text-xs font-bold text-slate-700 mb-2">
                                                    📝 Observation (관찰)
                                                </label>
                                                <textarea
                                                    value={editContent}
                                                    onChange={(e) => setEditContent(e.target.value)}
                                                    placeholder="일상에서 관찰한 것을 적어보세요..."
                                                    className="w-full h-24 px-3 py-2 bg-white border border-teal-300 rounded-lg resize-none focus:ring-2 focus:ring-teal-400 focus:border-teal-400 text-sm text-slate-700 placeholder:text-teal-300"
                                                />
                                            </div>

                                            {/* Abstraction Input */}
                                            <div className="mb-3">
                                                <label className="flex items-center gap-2 text-xs font-bold text-slate-700 mb-2">
                                                    1️⃣ Abstraction (CS 개념 연결)
                                                </label>
                                                <textarea
                                                    value={editInsightAbstraction}
                                                    onChange={(e) => setEditInsightAbstraction(e.target.value)}
                                                    placeholder="관찰과 연결되는 CS 개념을 적어보세요..."
                                                    className="w-full h-20 px-3 py-2 bg-white border border-teal-300 rounded-lg resize-none focus:ring-2 focus:ring-teal-400 focus:border-teal-400 text-sm text-slate-700 placeholder:text-teal-300"
                                                />
                                            </div>

                                            {/* Application Input */}
                                            <div className="mb-3">
                                                <label className="flex items-center gap-2 text-xs font-bold text-slate-700 mb-2">
                                                    2️⃣ Application (실무 적용)
                                                </label>
                                                <textarea
                                                    value={editInsightApplication}
                                                    onChange={(e) => setEditInsightApplication(e.target.value)}
                                                    placeholder="이 개념을 내 코드나 프로젝트에 어떻게 적용할 수 있을까요..."
                                                    className="w-full h-24 px-3 py-2 bg-white border border-teal-300 rounded-lg resize-none focus:ring-2 focus:ring-teal-400 focus:border-teal-400 text-sm text-slate-700 placeholder:text-teal-300"
                                                />
                                            </div>
                                        </>
                                    ) : (
                                        /* View Mode - Display Data */
                                        <>
                                            {/* Observation */}
                                            {entry.content && (
                                                <div className="mb-3">
                                                    <div className="flex items-center gap-2 mb-1.5">
                                                        <Lightbulb className="w-4 h-4 text-teal-600" />
                                                        <span className="text-xs font-bold text-teal-700">1️⃣ Observation (관찰)</span>
                                                    </div>
                                                    <p className="text-sm text-slate-700 pl-6 leading-relaxed">{entry.content}</p>
                                                </div>
                                            )}

                                            {/* Abstraction */}
                                            {entry.insightAbstraction && (
                                                <div className="mb-3">
                                                    <div className="flex items-center gap-2 mb-1.5">
                                                        <Sparkles className="w-4 h-4 text-cyan-600" />
                                                        <span className="text-xs font-bold text-cyan-700">1️⃣ Abstraction (CS 개념)</span>
                                                    </div>
                                                    <p className="text-sm text-slate-700 pl-6 leading-relaxed">{entry.insightAbstraction}</p>
                                                </div>
                                            )}

                                            {/* Application */}
                                            {entry.insightApplication && (
                                                <div className="mb-3">
                                                    <div className="flex items-center gap-2 mb-1.5">
                                                        <Hexagon className="w-4 h-4 text-teal-600" />
                                                        <span className="text-xs font-bold text-teal-700">2️⃣ Application (실무 적용)</span>
                                                    </div>
                                                    <p className="text-sm text-slate-700 pl-6 leading-relaxed">{entry.insightApplication}</p>
                                                </div>
                                            )}
                                        </>
                                    )}
                                </div>
                            )}

                            {/* Metrics: Gravity and Stability */}
                            {isEditing ? (
                                <div className="space-y-3 mb-4">
                                    {/* Gravity Slider */}
                                    <div className="space-y-1">
                                        <div className="flex justify-between text-xs">
                                            <span className="flex items-center gap-1 text-slate-600 font-medium">
                                                <Zap className="w-3 h-3 text-secondary-500" />
                                                Gravity
                                            </span>
                                            <span className="font-mono font-bold text-secondary-600">{editGravity}%</span>
                                        </div>
                                        <input
                                            type="range"
                                            min="0"
                                            max="100"
                                            value={editGravity}
                                            onChange={(e) => setEditGravity(parseInt(e.target.value))}
                                            className="w-full h-1.5 bg-secondary-100 rounded-lg appearance-none cursor-pointer accent-secondary-500"
                                        />
                                    </div>

                                    {/* Stability Slider */}
                                    <div className="space-y-1">
                                        <div className="flex justify-between text-xs">
                                            <span className="flex items-center gap-1 text-slate-600 font-medium">
                                                <Shield className="w-3 h-3 text-primary-500" />
                                                Stability
                                            </span>
                                            <span className="font-mono font-bold text-primary-600">{editStability}%</span>
                                        </div>
                                        <input
                                            type="range"
                                            min="0"
                                            max="100"
                                            value={editStability}
                                            onChange={(e) => setEditStability(parseInt(e.target.value))}
                                            className="w-full h-1.5 bg-primary-100 rounded-lg appearance-none cursor-pointer accent-primary-600"
                                        />
                                    </div>
                                </div>
                            ) : (
                                <div className="flex gap-3 pt-1">
                                    <div className="flex-1 flex items-center justify-between px-3 py-2 bg-secondary-50 rounded border border-secondary-100" title="외부 인력 (Gravity)">
                                        <span className="text-[10px] font-bold text-secondary-800/60 uppercase">Gravity</span>
                                        <span className="text-xs font-mono font-bold text-secondary-600">{entry.gravity ?? entry.longing ?? 0}%</span>
                                    </div>
                                    <div className="flex-1 flex items-center justify-between px-3 py-2 bg-primary-50 rounded border border-primary-100" title="코어 안정성 (Stability)">
                                        <span className="text-[10px] font-bold text-primary-800/60 uppercase">Stability</span>
                                        <span className="text-xs font-mono font-bold text-primary-600">{entry.stability ?? entry.mood ?? 0}%</span>
                                    </div>
                                </div>
                            )}

                            {/* AI Co-Pilot: Decrypt Log / AI Feedback Button */}
                            {!isEditing && (
                                <div className="mt-4 pt-4 border-t border-slate-100">
                                    {entry.logType === 'INSIGHT' ? (
                                        // Insight Log용 AI Feedback 섹션
                                        feedbackResults[entry.id] ? (
                                            <div className="space-y-3">
                                                {/* AI Feedback Results */}
                                                <div className="p-4 bg-gradient-to-br from-slate-50 to-teal-50 rounded-lg border border-teal-200">
                                                    <div className="flex items-center gap-2 mb-3">
                                                        <Sparkles className="w-4 h-4 text-teal-600" />
                                                        <span className="text-xs font-bold text-teal-800 uppercase tracking-wider">AI Feedback</span>
                                                    </div>
                                                    <p className="text-sm text-slate-700 leading-relaxed">
                                                        {feedbackResults[entry.id]}
                                                    </p>
                                                </div>

                                                {/* Action Buttons */}
                                                <div className="flex gap-2">
                                                    <button
                                                        onClick={() => setFeedbackResults(prev => { const newResults = { ...prev }; delete newResults[entry.id]; return newResults; })}
                                                        className="flex-1 text-xs text-slate-400 hover:text-slate-600 transition-colors py-2 px-3 rounded bg-slate-50 hover:bg-slate-100"
                                                    >
                                                        Close Analysis
                                                    </button>
                                                    <button
                                                        onClick={() => handleRequestFeedback(entry)}
                                                        disabled={requestingFeedback === entry.id}
                                                        className="flex-1 text-xs text-teal-600 hover:text-teal-700 transition-colors py-2 px-3 rounded bg-teal-50 hover:bg-teal-100 disabled:opacity-50"
                                                    >
                                                        {requestingFeedback === entry.id ? 'Generating...' : 'Re-analyze'}
                                                    </button>
                                                </div>
                                            </div>
                                        ) : entry.aiFeedback ? (
                                            <button
                                                onClick={() => setFeedbackResults(prev => ({ ...prev, [entry.id]: entry.aiFeedback }))}
                                                className="w-full px-4 py-2 bg-gradient-to-r from-slate-600 to-teal-400 text-white rounded-lg hover:from-slate-700 hover:to-teal-500 transition-all flex items-center justify-center gap-2 text-sm font-medium"
                                            >
                                                <Sparkles className="w-4 h-4" />
                                                View Analysis
                                            </button>
                                        ) : (
                                            <button
                                                onClick={() => handleRequestFeedback(entry)}
                                                disabled={requestingFeedback === entry.id}
                                                className="w-full px-4 py-2 bg-gradient-to-r from-slate-700 to-teal-500 text-white rounded-lg hover:from-slate-800 hover:to-teal-600 transition-all flex items-center justify-center gap-2 text-sm font-medium disabled:opacity-50"
                                            >
                                                {requestingFeedback === entry.id ? (
                                                    <>
                                                        <div className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin"></div>
                                                        Generating AI Feedback...
                                                    </>
                                                ) : (
                                                    <>
                                                        <Sparkles className="w-4 h-4" />
                                                        AI Feedback
                                                    </>
                                                )}
                                            </button>
                                        )
                                    ) : (
                                        // 일반 Log용 Decrypt Log 섹션
                                        analysisResults[entry.id] ? (
                                            <div className="space-y-3">
                                                {/* Analysis Results */}
                                                <div className="p-4 bg-gradient-to-br from-slate-50 to-emerald-50 rounded-lg border border-emerald-200">
                                                    <div className="flex items-center gap-2 mb-3">
                                                        <Sparkles className="w-4 h-4 text-emerald-600" />
                                                        <span className="text-xs font-bold text-emerald-800 uppercase tracking-wider">로그 해독 리포트</span>
                                                    </div>

                                                    {/* Distortions */}
                                                    {analysisResults[entry.id].distortions && analysisResults[entry.id].distortions.length > 0 && (
                                                        <div className="mb-3">
                                                            <span className="text-xs font-bold text-slate-700">🔍 발견된 왜곡:</span>
                                                            <ul className="mt-2 space-y-1">
                                                                {analysisResults[entry.id].distortions.map((d, idx) => (
                                                                    <li key={idx} className="text-xs text-slate-600 ml-2">
                                                                        <span className="font-semibold text-rose-600">{d.type}</span>: "{d.quote}"
                                                                    </li>
                                                                ))}
                                                            </ul>
                                                        </div>
                                                    )}

                                                    {/* Reframed Perspective */}
                                                    {analysisResults[entry.id].reframed && (
                                                        <div className="mb-3">
                                                            <span className="text-xs font-bold text-slate-700">💡 재해석:</span>
                                                            <p className="text-sm text-slate-700 leading-relaxed mt-1">
                                                                {analysisResults[entry.id].reframed}
                                                            </p>
                                                        </div>
                                                    )}

                                                    {/* Alternative Perspective */}
                                                    {analysisResults[entry.id].alternative && (
                                                        <div>
                                                            <span className="text-xs font-bold text-slate-700">✨ 대안적 관점:</span>
                                                            <p className="text-sm text-emerald-700 leading-relaxed mt-1">
                                                                {analysisResults[entry.id].alternative}
                                                            </p>
                                                        </div>
                                                    )}

                                                    {/* Fallback for empty result */}
                                                    {(!analysisResults[entry.id].distortions?.length && !analysisResults[entry.id].reframed && !analysisResults[entry.id].alternative) && (
                                                        <div className="text-xs text-slate-500 italic">
                                                            특이 사항이 발견되지 않았습니다.
                                                        </div>
                                                    )}
                                                </div>

                                                {/* Action Buttons */}
                                                <div className="flex gap-2">
                                                    <button
                                                        onClick={() => setAnalysisResults(prev => { const newResults = { ...prev }; delete newResults[entry.id]; return newResults; })}
                                                        className="flex-1 text-xs text-slate-400 hover:text-slate-600 transition-colors py-2 px-3 rounded bg-slate-50 hover:bg-slate-100"
                                                    >
                                                        Close Analysis
                                                    </button>
                                                    <button
                                                        onClick={() => handleDecryptLog(entry)}
                                                        disabled={analyzingId === entry.id}
                                                        className="flex-1 text-xs text-emerald-600 hover:text-emerald-700 transition-colors py-2 px-3 rounded bg-emerald-50 hover:bg-emerald-100 disabled:opacity-50"
                                                    >
                                                        Re-analyze
                                                    </button>
                                                </div>
                                            </div>
                                        ) : entry.analysis ? (
                                            <button
                                                onClick={() => setAnalysisResults(prev => ({ ...prev, [entry.id]: entry.analysis }))}
                                                className="w-full px-4 py-2 bg-gradient-to-r from-slate-600 to-emerald-400 text-white rounded-lg hover:from-slate-700 hover:to-emerald-500 transition-all flex items-center justify-center gap-2 text-sm font-medium"
                                            >
                                                <Sparkles className="w-4 h-4" />
                                                View Analysis
                                            </button>
                                        ) : (
                                            <button
                                                onClick={() => handleDecryptLog(entry)}
                                                disabled={analyzingId === entry.id}
                                                className="w-full px-4 py-2 bg-gradient-to-r from-slate-700 to-emerald-500 text-white rounded-lg hover:from-slate-800 hover:to-emerald-600 transition-all flex items-center justify-center gap-2 text-sm font-medium disabled:opacity-50"
                                            >
                                                {analyzingId === entry.id ? (
                                                    <>
                                                        <div className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin"></div>
                                                        Decrypting Log...
                                                    </>
                                                ) : (
                                                    <>
                                                        <Sparkles className="w-4 h-4" />
                                                        Decrypt Log
                                                    </>
                                                )}
                                            </button>
                                        )
                                    )}

                                    {/* Error Message */}
                                    {(analysisError && analyzingId === entry.id) || (feedbackError && requestingFeedback === entry.id) ? (
                                        <div className="mt-2 p-2 bg-rose-50 border border-rose-200 rounded text-xs text-rose-600">
                                            {entry.logType === 'INSIGHT' ? feedbackError : analysisError}
                                        </div>
                                    ) : null}
                                </div>
                            )
                            }
                        </Card>
                    );
                })
            )}

            {/* Infinite Scroll Sentinel */}
            {
                entries.length > visibleCount && (
                    <div ref={sentinelRef} className="h-10 flex justify-center items-center">
                        <div className="w-5 h-5 border-2 border-slate-200 border-t-slate-400 rounded-full animate-spin"></div>
                    </div>
                )
            }

            {/* Data Persistence Section - Moved to Bottom */}
            <div className="bg-white p-4 rounded-xl border border-slate-200 flex flex-col gap-3 shadow-sm">
                <div className="flex items-center gap-2 text-[10px] font-bold text-slate-400 uppercase tracking-widest">
                    <AlertCircle className="w-3 h-3" /> Data Persistence
                </div>
                <div className="flex gap-2">
                    <Button onClick={onDownloadData} variant="outline" className="flex-1 text-xs bg-slate-50">
                        <Download className="w-3 h-3" /> Backup (JSON)
                    </Button>
                    <Button onClick={() => fileInputRef.current?.click()} variant="outline" className="flex-1 text-xs bg-slate-50">
                        <Upload className="w-3 h-3" /> Restore
                    </Button>
                    <input type="file" ref={fileInputRef} onChange={onFileUpload} accept="application/json" className="hidden" />
                </div>
            </div>
        </div >
    );
}
