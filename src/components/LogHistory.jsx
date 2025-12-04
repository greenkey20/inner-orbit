import React, { useState, useEffect, useRef } from 'react';
import { Calendar, Trash2, Download, Upload, AlertCircle, Hexagon, Pencil, Check, X, Zap, Shield } from 'lucide-react';

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
export default function LogHistory({ entries, onDeleteEntry, onUpdateEntry, onDownloadData, onFileUpload, fileInputRef }) {
    // 수정 모드 상태 관리
    const [editingId, setEditingId] = useState(null);
    const [editContent, setEditContent] = useState('');
    const [editGravity, setEditGravity] = useState(50);
    const [editStability, setEditStability] = useState(50);

    // 페이지네이션 상태
    const [visibleCount, setVisibleCount] = useState(10);
    const observerRef = useRef(null);
    const sentinelRef = useRef(null);

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

    // 수정 모드 진입
    const startEdit = (entry) => {
        setEditingId(entry.id);
        setEditContent(entry.content);
        setEditGravity(entry.gravity || 50);
        setEditStability(entry.stability || 50);
    };

    // 수정 저장
    const saveEdit = () => {
        onUpdateEntry(editingId, editContent, editGravity, editStability);
        setEditingId(null);
    };

    // 수정 취소
    const cancelEdit = () => {
        setEditingId(null);
        setEditContent('');
    };

    return (
        <div className="space-y-6 animate-fade-in">
            {entries.length === 0 ? (
                <div className="text-center py-16 opacity-60">
                    <Hexagon className="w-10 h-10 mx-auto mb-4 text-slate-300" />
                    <p className="text-sm text-slate-500">No logs found.<br />Initialize your first entry.</p>
                </div>
            ) : (
                entries.slice(0, visibleCount).map((entry) => {
                    const isEditing = editingId === entry.id;

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
                                            {entry.date}
                                        </span>
                                    </div>
                                    {/* Audit Timestamp - Show if updated */}
                                    {entry.updatedAt && (
                                        <div className="flex items-center gap-2 ml-5">
                                            <span className="text-[10px] font-mono text-slate-400">
                                                (수정: {entry.updatedAt})
                                            </span>
                                        </div>
                                    )}
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

                            {/* Content: Text or Textarea */}
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
                        </Card>
                    );
                })
            )}

            {/* Infinite Scroll Sentinel */}
            {entries.length > visibleCount && (
                <div ref={sentinelRef} className="h-10 flex justify-center items-center">
                    <div className="w-5 h-5 border-2 border-slate-200 border-t-slate-400 rounded-full animate-spin"></div>
                </div>
            )}

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
        </div>
    );
}
