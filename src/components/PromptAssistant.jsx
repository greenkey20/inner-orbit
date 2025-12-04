import React from 'react';
import { BookOpen, RefreshCw, Trash2 } from 'lucide-react';

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
 * PromptAssistant - 프롬프트 질문 생성기 및 적용 컴포넌트
 * 
 * @param {number} currentPromptIndex - 현재 프롬프트 인덱스
 * @param {string} currentPrompt - 현재 프롬프트 텍스트
 * @param {function} onNextPrompt - 다음 프롬프트로 이동
 * @param {function} onInsertPrompt - 프롬프트 적용
 * @param {boolean} showPrompt - 표시 여부
 * @param {function} onTogglePrompt - 표시/숨김 토글
 */
export default function PromptAssistant({
    currentPrompt,
    onNextPrompt,
    onInsertPrompt,
    showPrompt,
    onTogglePrompt
}) {
    if (!showPrompt) return null;

    return (
        <div className="bg-slate-800 p-4 rounded-xl border border-slate-700 relative text-slate-200 shadow-md">
            <div className="flex justify-between items-start mb-3">
                <span className="text-[10px] font-bold text-primary-300 uppercase flex items-center gap-1 tracking-wider">
                    <BookOpen className="w-3 h-3" /> Navigation Query
                </span>
                <div className="flex gap-2">
                    <button
                        onClick={onNextPrompt}
                        className="text-slate-400 hover:text-white transition-colors"
                    >
                        <RefreshCw className="w-3 h-3" />
                    </button>
                    <button
                        onClick={() => onTogglePrompt(false)}
                        className="text-slate-400 hover:text-white transition-colors"
                    >
                        <Trash2 className="w-3 h-3" />
                    </button>
                </div>
            </div>
            <p className="text-sm font-medium leading-relaxed mb-4 text-slate-100">
                "{currentPrompt}"
            </p>
            <Button
                variant="secondary"
                onClick={onInsertPrompt}
                className="w-full text-xs py-2 h-auto bg-slate-700 border-slate-600 text-slate-200 hover:bg-slate-600 hover:text-white"
            >
                Apply to Log
            </Button>
        </div>
    );
}
