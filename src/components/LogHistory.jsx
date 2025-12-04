import React from 'react';
import { Calendar, Trash2, Download, Upload, AlertCircle, Hexagon } from 'lucide-react';

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
        secondary: "bg-indigo-50 text-indigo-700 hover:bg-indigo-100 border border-indigo-100",
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
 * @param {function} onDownloadData - 백업 핸들러
 * @param {function} onFileUpload - 복원 핸들러
 * @param {React.RefObject} fileInputRef - 파일 입력 ref
 */
export default function LogHistory({ entries, onDeleteEntry, onDownloadData, onFileUpload, fileInputRef }) {
    return (
        <div className="space-y-6 animate-fade-in">
            {/* Backup Section */}
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

            {entries.length === 0 ? (
                <div className="text-center py-16 opacity-60">
                    <Hexagon className="w-10 h-10 mx-auto mb-4 text-slate-300" />
                    <p className="text-sm text-slate-500">No logs found.<br />Initialize your first entry.</p>
                </div>
            ) : (
                entries.map((entry) => (
                    <Card key={entry.id} className="p-5 hover:shadow-md transition-shadow group">
                        <div className="flex justify-between items-start mb-3 border-b border-slate-100 pb-3">
                            <div className="flex items-center gap-2">
                                <Calendar className="w-3 h-3 text-slate-400" />
                                <span className="text-xs font-mono font-medium text-slate-500">{entry.date}</span>
                            </div>
                            <button onClick={() => onDeleteEntry(entry.id)} className="text-slate-300 hover:text-rose-500 opacity-0 group-hover:opacity-100 transition-all"><Trash2 className="w-3 h-3" /></button>
                        </div>

                        <div className="mb-5 text-sm text-slate-700 whitespace-pre-wrap leading-relaxed font-sans">
                            {entry.content}
                        </div>

                        <div className="flex gap-3 pt-1">
                            <div className="flex-1 flex items-center justify-between px-3 py-2 bg-amber-50 rounded border border-amber-100" title="외부 인력 (Gravity)">
                                <span className="text-[10px] font-bold text-amber-800/60 uppercase">Gravity</span>
                                <span className="text-xs font-mono font-bold text-amber-600">{entry.gravity || entry.longing}%</span>
                            </div>
                            <div className="flex-1 flex items-center justify-between px-3 py-2 bg-indigo-50 rounded border border-indigo-100" title="코어 안정성 (Stability)">
                                <span className="text-[10px] font-bold text-indigo-800/60 uppercase">Stability</span>
                                <span className="text-xs font-mono font-bold text-indigo-600">{entry.stability || entry.mood}%</span>
                            </div>
                        </div>
                    </Card>
                ))
            )}
        </div>
    );
}
