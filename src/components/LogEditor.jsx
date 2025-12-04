import React from 'react';
import { PenTool, Send } from 'lucide-react';

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
 * LogEditor - 로그 입력 및 전송 영역 컴포넌트
 * 
 * @param {string} message - 현재 메시지 내용
 * @param {function} onMessageChange - 메시지 변경 핸들러
 * @param {function} onSubmit - 전송 핸들러
 * @param {boolean} showPrompt - 프롬프트 표시 여부
 * @param {function} onShowPrompt - 프롬프트 표시 요청 핸들러
 */
export default function LogEditor({ message, onMessageChange, onSubmit, showPrompt, onShowPrompt }) {
    return (
        <>
            <section className="space-y-2">
                <div className="flex justify-between items-end px-1">
                    <label className="text-xs font-bold text-slate-500 uppercase flex items-center gap-2 tracking-wider">
                        <PenTool className="w-3 h-3" /> Captain's Log
                    </label>
                    {!showPrompt && (
                        <button
                            onClick={() => onShowPrompt(true)}
                            className="text-xs text-indigo-600 hover:text-indigo-800 font-medium"
                        >
                            + Show Query
                        </button>
                    )}
                </div>
                <div className="relative group">
                    <textarea
                        value={message}
                        onChange={(e) => onMessageChange(e.target.value)}
                        placeholder="감정의 인력을 성장의 동력으로. 오늘 당신의 항해 기록을 남겨주세요."
                        className="w-full h-60 p-5 bg-white border border-slate-200 rounded-xl resize-none focus:ring-2 focus:ring-slate-200 focus:border-slate-400 text-slate-700 leading-relaxed placeholder:text-slate-300 text-sm transition-all shadow-sm font-sans"
                    />
                </div>
            </section>

            <Button onClick={onSubmit} disabled={!message.trim()} className="w-full py-4 shadow-lg text-sm tracking-wide">
                <Send className="w-4 h-4" />
                Commit Log Entry
            </Button>
        </>
    );
}
