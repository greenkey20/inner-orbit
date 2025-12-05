import React, { useState } from 'react';
import { BookOpen, RefreshCw, Trash2, Sparkles } from 'lucide-react';
import { generateDynamicPrompt, getApiKey } from '../services/openaiService';

// UI 컴포넌트: 버튼
const Button = ({ onClick, children, variant = "primary", className = "", ...props }) => {
    const baseStyle = "px-4 py-2 rounded-lg font-medium transition-all duration-200 flex items-center justify-center gap-2";
    const variants = {
        primary: "bg-slate-800 text-white hover:bg-slate-900 shadow-md hover:shadow-lg disabled:opacity-50 disabled:cursor-not-allowed",
        secondary: "bg-primary-50 text-primary-700 hover:bg-primary-100 border border-primary-100",
        outline: "border border-slate-300 text-slate-600 hover:border-slate-400 hover:bg-slate-50",
        ghost: "text-slate-400 hover:text-slate-700 hover:bg-slate-100",
        danger: "bg-rose-50 text-rose-500 hover:bg-rose-100",
        ai: "bg-gradient-to-r from-slate-700 to-emerald-500 text-white hover:from-slate-800 hover:to-emerald-600 shadow-md hover:shadow-lg disabled:opacity-50 disabled:cursor-not-allowed"
    };
    return (
        <button onClick={onClick} className={`${baseStyle} ${variants[variant]} ${className}`} {...props}>
            {children}
        </button>
    );
};

/**
 * PromptAssistant - 통합 프롬프트 생성기 (Static + AI)
 * 
 * @param {string} currentPrompt - 현재 Static 프롬프트 텍스트
 * @param {function} onNextPrompt - 다음 Static 프롬프트로 이동
 * @param {function} onInsertPrompt - 프롬프트 적용 (Static용)
 * @param {boolean} showPrompt - 표시 여부
 * @param {function} onTogglePrompt - 표시/숨김 토글
 * @param {number} gravity - 현재 Gravity 값
 * @param {number} stability - 현재 Stability 값
 */
export default function PromptAssistant({
    currentPrompt,
    onNextPrompt,
    onInsertPrompt,
    showPrompt,
    onTogglePrompt,
    gravity,
    stability
}) {
    const [mode, setMode] = useState('static'); // 'static' or 'ai'
    const [aiPrompt, setAiPrompt] = useState('');
    const [isGenerating, setIsGenerating] = useState(false);
    const [error, setError] = useState('');

    const handleGenerateAiPrompt = async () => {
        const hasApiKey = Boolean(getApiKey());

        if (!hasApiKey) {
            setError('API Key가 필요합니다. Settings에서 OpenAI API Key를 입력해주세요.');
            return;
        }

        setIsGenerating(true);
        setError('');

        try {
            const prompt = await generateDynamicPrompt(gravity, stability);
            setAiPrompt(prompt);
        } catch (err) {
            setError(err.message);
        } finally {
            setIsGenerating(false);
        }
    };

    const handleInsertCurrentPrompt = () => {
        if (mode === 'static') {
            onInsertPrompt(); // Static mode uses the hook's internal promptIndex
        } else {
            // AI mode: insert the AI-generated prompt directly into message
            onInsertPrompt(aiPrompt);
        }
    };

    if (!showPrompt) return null;

    return (
        <div className="bg-slate-800 p-4 rounded-xl border border-slate-700 relative text-slate-200 shadow-md">
            {/* Header with Mode Toggle */}
            <div className="flex justify-between items-start mb-3">
                <div className="flex items-center gap-2">
                    <BookOpen className="w-3 h-3 text-primary-300" />
                    <span className="text-[10px] font-bold text-primary-300 uppercase tracking-wider">
                        Navigation Query
                    </span>
                </div>
                <button
                    onClick={() => onTogglePrompt(false)}
                    className="text-slate-400 hover:text-white transition-colors"
                >
                    <Trash2 className="w-3 h-3" />
                </button>
            </div>

            {/* Mode Tabs */}
            <div className="flex gap-2 mb-4">
                <button
                    onClick={() => setMode('static')}
                    className={`flex-1 px-3 py-2 rounded-lg text-xs font-medium transition-all ${mode === 'static'
                        ? 'bg-slate-700 text-white shadow-sm'
                        : 'bg-slate-800 text-slate-400 hover:text-slate-200'
                        }`}
                >
                    <RefreshCw className="w-3 h-3 inline mr-1" />
                    Static
                </button>
                <button
                    onClick={() => setMode('ai')}
                    className={`flex-1 px-3 py-2 rounded-lg text-xs font-medium transition-all ${mode === 'ai'
                        ? 'bg-gradient-to-r from-slate-700 to-emerald-500 text-white shadow-sm'
                        : 'bg-slate-800 text-slate-400 hover:text-slate-200'
                        }`}
                >
                    <Sparkles className="w-3 h-3 inline mr-1" />
                    AI Request
                </button>
            </div>

            {/* Static Mode */}
            {mode === 'static' && (
                <>
                    <p className="text-sm font-medium leading-relaxed mb-4 text-slate-100">
                        "{currentPrompt}"
                    </p>
                    <div className="flex gap-2">
                        <Button
                            variant="secondary"
                            onClick={onNextPrompt}
                            className="flex-1 text-xs py-2 h-auto bg-slate-700 border-slate-600 text-slate-200 hover:bg-slate-600 hover:text-white"
                        >
                            <RefreshCw className="w-3 h-3" />
                            New Question
                        </Button>
                        <Button
                            variant="secondary"
                            onClick={handleInsertCurrentPrompt}
                            className="flex-1 text-xs py-2 h-auto bg-primary-700 border-primary-600 text-white hover:bg-primary-600"
                        >
                            Apply to Log
                        </Button>
                    </div>
                </>
            )}

            {/* AI Mode */}
            {mode === 'ai' && (
                <>
                    {error && (
                        <div className="mb-3 p-2 bg-rose-50 border border-rose-200 rounded text-xs text-rose-600">
                            {error}
                        </div>
                    )}

                    {aiPrompt && !isGenerating && (
                        <p className="text-sm font-medium leading-relaxed mb-4 text-slate-100">
                            "{aiPrompt}"
                        </p>
                    )}

                    {isGenerating && (
                        <div className="mb-4 flex items-center justify-center gap-2 text-slate-300">
                            <RefreshCw className="w-4 h-4 animate-spin" />
                            <span className="text-sm">Receiving Transmission...</span>
                        </div>
                    )}

                    <div className="flex gap-2">
                        <Button
                            variant="ai"
                            onClick={handleGenerateAiPrompt}
                            disabled={isGenerating}
                            className="flex-1 text-xs py-2 h-auto"
                        >
                            <Sparkles className="w-3 h-3" />
                            {isGenerating ? 'Requesting...' : 'Request Signal'}
                        </Button>
                        {aiPrompt && !isGenerating && (
                            <Button
                                variant="secondary"
                                onClick={handleInsertCurrentPrompt}
                                className="flex-1 text-xs py-2 h-auto bg-primary-700 border-primary-600 text-white hover:bg-primary-600"
                            >
                                Apply to Log
                            </Button>
                        )}
                    </div>
                </>
            )}
        </div>
    );
}
