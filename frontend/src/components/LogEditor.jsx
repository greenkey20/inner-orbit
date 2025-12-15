import React, { useState } from 'react';
import { PenTool, Send, MapPin, Eye, Ear, Hand } from 'lucide-react';

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

// UI 컴포넌트: Toggle Switch
const ToggleSwitch = ({ enabled, onToggle, label, className = "" }) => {
    return (
        <div className={`flex items-center gap-3 ${className}`}>
            <button
                onClick={onToggle}
                className={`relative w-11 h-6 rounded-full transition-colors duration-200 focus:outline-none focus:ring-2 focus:ring-lime-500 focus:ring-offset-2 ${
                    enabled ? 'bg-lime-600' : 'bg-slate-300'
                }`}
            >
                <span
                    className={`absolute top-0.5 left-0.5 w-5 h-5 bg-white rounded-full shadow transition-transform duration-200 ${
                        enabled ? 'transform translate-x-5' : ''
                    }`}
                />
            </button>
            <span className={`text-sm font-medium transition-colors duration-200 ${
                enabled ? 'text-lime-600' : 'text-slate-700'
            }`}>{label}</span>
        </div>
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
 * @param {Object} deepLogData - Deep Log 데이터 (location, sensory fields)
 * @param {function} onDeepLogChange - Deep Log 데이터 변경 핸들러
 */
export default function LogEditor({ 
    message, 
    onMessageChange, 
    onSubmit, 
    showPrompt, 
    onShowPrompt,
    deepLogData = {},
    onDeepLogChange = () => {}
}) {
    const [isTravelMode, setIsTravelMode] = useState(false);

    const handleDeepLogFieldChange = (field, value) => {
        onDeepLogChange({
            ...deepLogData,
            [field]: value,
            isDeepLog: isTravelMode
        });
    };

    const handleSubmitWithDeepLog = () => {
        if (isTravelMode) {
            onSubmit({
                content: message,
                ...deepLogData,
                isDeepLog: true
            });
        } else {
            onSubmit();
        }
    };

    const isSubmitDisabled = isTravelMode
        ? !message.trim() && !deepLogData.location && !deepLogData.sensoryVisual && !deepLogData.sensoryAuditory && !deepLogData.sensoryTactile
        : !message.trim();
    return (
        <>
            <section className="space-y-4">
                <div className="flex justify-between items-end px-1">
                    <label className="text-xs font-bold text-slate-500 uppercase flex items-center gap-2 tracking-wider">
                        <PenTool className="w-3 h-3" /> 
                        {isTravelMode ? 'Deep Travel Log' : "Captain's Log"}
                    </label>
                    {!showPrompt && (
                        <button
                            onClick={() => onShowPrompt(true)}
                            className="text-xs text-primary-600 hover:text-primary-800 font-medium"
                        >
                            + Show Query
                        </button>
                    )}
                </div>

                {isTravelMode ? (
                    /* Travel Mode - Deep Log Form */
                    <div className="space-y-4">
                        {/* Location Input */}
                        <div className="relative group">
                            <div className="absolute left-4 top-4 text-lime-500">
                                <MapPin className="w-4 h-4" />
                            </div>
                            <input
                                type="text"
                                value={deepLogData.location || ''}
                                onChange={(e) => handleDeepLogFieldChange('location', e.target.value)}
                                placeholder="Where are you now?"
                                className="w-full pl-12 pr-5 py-4 bg-white border border-lime-200 rounded-xl focus:ring-2 focus:ring-lime-300 focus:border-lime-400 text-slate-700 placeholder:text-lime-400 text-sm transition-all shadow-sm font-sans"
                            />
                        </div>

                        {/* Content Input */}
                        <div className="relative group">
                            <textarea
                                value={message}
                                onChange={(e) => onMessageChange(e.target.value)}
                                placeholder="여행에서 느끼는 전반적인 감정과 생각을 자유롭게 적어보세요..."
                                className="w-full h-32 p-5 bg-white border border-lime-200 rounded-xl resize-none focus:ring-2 focus:ring-lime-300 focus:border-lime-400 text-slate-700 leading-relaxed placeholder:text-lime-400 text-sm transition-all shadow-sm font-sans"
                            />
                        </div>

                        {/* Sensory Inputs */}
                        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                            {/* Visual */}
                            <div className="relative group">
                                <div className="absolute left-4 top-4 text-emerald-500">
                                    <Eye className="w-4 h-4" />
                                </div>
                                <textarea
                                    value={deepLogData.sensoryVisual || ''}
                                    onChange={(e) => handleDeepLogFieldChange('sensoryVisual', e.target.value)}
                                    placeholder="무엇이 보이나요?"
                                    className="w-full h-24 pl-12 pr-4 pt-4 pb-4 bg-white border border-emerald-200 rounded-xl resize-none focus:ring-2 focus:ring-emerald-300 focus:border-emerald-400 text-slate-700 leading-relaxed placeholder:text-emerald-400 text-sm transition-all shadow-sm font-sans"
                                />
                            </div>

                            {/* Auditory */}
                            <div className="relative group">
                                <div className="absolute left-4 top-4 text-lime-500">
                                    <Ear className="w-4 h-4" />
                                </div>
                                <textarea
                                    value={deepLogData.sensoryAuditory || ''}
                                    onChange={(e) => handleDeepLogFieldChange('sensoryAuditory', e.target.value)}
                                    placeholder="무엇이 들리나요?"
                                    className="w-full h-24 pl-12 pr-4 pt-4 pb-4 bg-white border border-lime-200 rounded-xl resize-none focus:ring-2 focus:ring-lime-300 focus:border-lime-400 text-slate-700 leading-relaxed placeholder:text-lime-400 text-sm transition-all shadow-sm font-sans"
                                />
                            </div>

                            {/* Tactile */}
                            <div className="relative group">
                                <div className="absolute left-4 top-4 text-green-500">
                                    <Hand className="w-4 h-4" />
                                </div>
                                <textarea
                                    value={deepLogData.sensoryTactile || ''}
                                    onChange={(e) => handleDeepLogFieldChange('sensoryTactile', e.target.value)}
                                    placeholder="무엇이 느껴지나요?"
                                    className="w-full h-24 pl-12 pr-4 pt-4 pb-4 bg-white border border-green-200 rounded-xl resize-none focus:ring-2 focus:ring-green-300 focus:border-green-400 text-slate-700 leading-relaxed placeholder:text-green-400 text-sm transition-all shadow-sm font-sans"
                                />
                            </div>
                        </div>
                    </div>
                ) : (
                    /* Normal Mode - Simple Text Area */
                    <div className="relative group">
                        <textarea
                            value={message}
                            onChange={(e) => onMessageChange(e.target.value)}
                            placeholder="감정의 인력을 성장의 동력으로. 오늘 당신의 항해 기록을 남겨주세요."
                            className="w-full h-60 p-5 bg-white border border-slate-200 rounded-xl resize-none focus:ring-2 focus:ring-slate-200 focus:border-slate-400 text-slate-700 leading-relaxed placeholder:text-slate-300 text-sm transition-all shadow-sm font-sans"
                        />
                    </div>
                )}
            </section>

            {/* Travel Mode Toggle */}
            <div className="flex flex-col items-center justify-center my-6">
                <ToggleSwitch
                    enabled={isTravelMode}
                    onToggle={() => setIsTravelMode(!isTravelMode)}
                    label="Travel Mode (Deep Log)"
                />
                <p className={`text-xs text-center mt-2 transition-colors duration-200 ${
                    isTravelMode ? 'text-lime-600 font-medium' : 'text-slate-500'
                }`}>
                    {isTravelMode ? '여행 중 감각적 경험을 상세히 기록합니다' : '일상적인 감정 로그를 작성합니다'}
                </p>
            </div>

            <Button
                onClick={handleSubmitWithDeepLog}
                disabled={isSubmitDisabled}
                className={`w-full py-4 shadow-lg text-sm tracking-wide ${
                    isTravelMode
                        ? 'bg-gradient-to-r from-lime-600 to-emerald-600 hover:from-lime-700 hover:to-emerald-700'
                        : ''
                }`}
            >
                <Send className="w-4 h-4" />
                {isTravelMode ? 'Commit Deep Travel Log' : 'Commit Log Entry'}
            </Button>
        </>
    );
}
