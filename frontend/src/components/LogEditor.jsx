import React, { useState } from 'react';
import { PenTool, Send, MapPin, Eye, Ear, Hand, Lightbulb, Sparkles } from 'lucide-react';

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
 * LogEditor - 로그 입력 및 전송 영역 컴포넌트
 *
 * @param {string} message - 현재 메시지 내용
 * @param {function} onMessageChange - 메시지 변경 핸들러
 * @param {function} onSubmit - 전송 핸들러
 * @param {boolean} showPrompt - 프롬프트 표시 여부
 * @param {function} onShowPrompt - 프롬프트 표시 요청 핸들러
 * @param {Object} deepLogData - Log 데이터 (sensory, insight fields, logType)
 * @param {function} onDeepLogChange - Log 데이터 변경 핸들러
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
    const [logMode, setLogMode] = useState('DAILY'); // DAILY, SENSORY, INSIGHT

    const handleFieldChange = (field, value) => {
        onDeepLogChange({
            ...deepLogData,
            [field]: value,
            logType: logMode
        });
    };

    const handleMockKeywordSuggestion = () => {
        const mockKeywords = [
            'Load Balancer',
            'Deadlock',
            'Cache Invalidation',
            'Event-Driven Architecture',
            'Race Condition',
            'Dependency Injection',
            'State Machine',
            'Circuit Breaker',
            'Message Queue',
            'Asynchronous I/O',
            'Connection Pool',
            'Garbage Collection',
            'Hash Collision',
            'Binary Search Tree',
            'Thread Pool'
        ];

        // 3~5개 랜덤 선택
        const numKeywords = Math.floor(Math.random() * 3) + 3; // 3, 4, 또는 5
        const shuffled = [...mockKeywords].sort(() => 0.5 - Math.random());
        const selectedKeywords = shuffled.slice(0, numKeywords);

        // 현재 값에 해시태그 형태로 추가
        const currentValue = deepLogData.insightAbstraction || '';
        const keywordTags = selectedKeywords.map(k => `#${k.replace(/\s+/g, '')}`).join(' ');
        const newValue = currentValue ? `${currentValue} ${keywordTags}` : keywordTags;

        handleFieldChange('insightAbstraction', newValue);
    };

    const handleSubmitWithLogType = () => {
        if (logMode === 'SENSORY') {
            onSubmit({
                content: message,
                ...deepLogData,
                logType: 'SENSORY'
            });
        } else if (logMode === 'INSIGHT') {
            onSubmit({
                content: message,
                ...deepLogData,
                logType: 'INSIGHT'
            });
        } else {
            onSubmit();
        }
    };

    const isSubmitDisabled = logMode === 'SENSORY'
        ? !message.trim() && !deepLogData.location && !deepLogData.sensoryVisual && !deepLogData.sensoryAuditory && !deepLogData.sensoryTactile
        : logMode === 'INSIGHT'
        ? !deepLogData.insightTrigger?.trim() || !deepLogData.insightAbstraction?.trim() || !deepLogData.insightApplication?.trim()
        : !message.trim();
    return (
        <>
            <section className="space-y-4">
                {/* Mode Selector */}
                <div className="flex gap-2 p-1 bg-slate-100 rounded-lg">
                    <button
                        onClick={() => setLogMode('DAILY')}
                        className={`flex-1 py-2.5 px-3 rounded-md text-xs font-bold uppercase tracking-wider transition-all ${
                            logMode === 'DAILY'
                                ? 'bg-white shadow-sm text-slate-800'
                                : 'text-slate-500 hover:text-slate-700'
                        }`}
                    >
                        <div className="flex items-center justify-center gap-1.5">
                            <PenTool className="w-3.5 h-3.5" />
                            Daily
                        </div>
                    </button>
                    <button
                        onClick={() => setLogMode('SENSORY')}
                        className={`flex-1 py-2.5 px-3 rounded-md text-xs font-bold uppercase tracking-wider transition-all ${
                            logMode === 'SENSORY'
                                ? 'bg-gradient-to-r from-lime-50 to-emerald-50 shadow-sm text-lime-700 border border-lime-200'
                                : 'text-slate-500 hover:text-slate-700'
                        }`}
                    >
                        <div className="flex items-center justify-center gap-1.5">
                            <MapPin className="w-3.5 h-3.5" />
                            Sensory
                        </div>
                    </button>
                    <button
                        onClick={() => setLogMode('INSIGHT')}
                        className={`flex-1 py-2.5 px-3 rounded-md text-xs font-bold uppercase tracking-wider transition-all ${
                            logMode === 'INSIGHT'
                                ? 'bg-gradient-to-r from-violet-50 to-purple-50 shadow-sm text-violet-700 border border-violet-200'
                                : 'text-slate-500 hover:text-slate-700'
                        }`}
                    >
                        <div className="flex items-center justify-center gap-1.5">
                            <Lightbulb className="w-3.5 h-3.5" />
                            Insight
                        </div>
                    </button>
                </div>

                <div className="flex justify-between items-end px-1">
                    <label className="text-xs font-bold text-slate-500 uppercase flex items-center gap-2 tracking-wider">
                        <PenTool className="w-3 h-3" />
                        {logMode === 'SENSORY' ? 'Deep Travel Log' : logMode === 'INSIGHT' ? 'Architecture of Insight' : "Captain's Log"}
                    </label>
                    {!showPrompt && logMode === 'DAILY' && (
                        <button
                            onClick={() => onShowPrompt(true)}
                            className="text-xs text-primary-600 hover:text-primary-800 font-medium"
                        >
                            + Show Query
                        </button>
                    )}
                </div>

                {logMode === 'SENSORY' ? (
                    /* Sensory Mode - Travel Log Form */
                    <div className="space-y-4">
                        {/* Location Input */}
                        <div className="relative group">
                            <div className="absolute left-4 top-4 text-lime-500">
                                <MapPin className="w-4 h-4" />
                            </div>
                            <input
                                type="text"
                                value={deepLogData.location || ''}
                                onChange={(e) => handleFieldChange('location', e.target.value)}
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
                                    onChange={(e) => handleFieldChange('sensoryVisual', e.target.value)}
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
                                    onChange={(e) => handleFieldChange('sensoryAuditory', e.target.value)}
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
                                    onChange={(e) => handleFieldChange('sensoryTactile', e.target.value)}
                                    placeholder="무엇이 느껴지나요?"
                                    className="w-full h-24 pl-12 pr-4 pt-4 pb-4 bg-white border border-green-200 rounded-xl resize-none focus:ring-2 focus:ring-green-300 focus:border-green-400 text-slate-700 leading-relaxed placeholder:text-green-400 text-sm transition-all shadow-sm font-sans"
                                />
                            </div>
                        </div>
                    </div>
                ) : logMode === 'INSIGHT' ? (
                    /* Insight Mode - CS Concept Mapping Form */
                    <div className="space-y-4">
                        {/* Trigger Input */}
                        <div className="relative group">
                            <label className="text-xs font-semibold text-violet-600 mb-1.5 block">
                                1️⃣ Trigger (관찰)
                            </label>
                            <textarea
                                value={deepLogData.insightTrigger || ''}
                                onChange={(e) => handleFieldChange('insightTrigger', e.target.value)}
                                placeholder="일상에서 관찰한 것을 적어보세요... (예: 친구가 동시에 여러 채팅에 답장하다 놓친 메시지가 있었다)"
                                className="w-full h-24 p-4 bg-white border border-violet-200 rounded-xl resize-none focus:ring-2 focus:ring-violet-300 focus:border-violet-400 text-slate-700 leading-relaxed placeholder:text-violet-300 text-sm transition-all shadow-sm font-sans"
                            />
                        </div>

                        {/* Abstraction Input with AI Button */}
                        <div className="relative group">
                            <label className="text-xs font-semibold text-violet-600 mb-1.5 block flex items-center justify-between">
                                <span>2️⃣ Abstraction (CS 개념 연결)</span>
                                <button
                                    onClick={handleMockKeywordSuggestion}
                                    className="px-3 py-1 bg-gradient-to-r from-violet-100 to-purple-100 hover:from-violet-200 hover:to-purple-200 text-violet-700 rounded-lg text-xs font-bold transition-all flex items-center gap-1.5 shadow-sm"
                                >
                                    <Sparkles className="w-3.5 h-3.5" />
                                    AI Suggest
                                </button>
                            </label>
                            <textarea
                                value={deepLogData.insightAbstraction || ''}
                                onChange={(e) => handleFieldChange('insightAbstraction', e.target.value)}
                                placeholder="관찰과 연결되는 CS 개념을 적어보세요... (예: Race Condition, Deadlock, Load Balancer)"
                                className="w-full h-20 p-4 bg-white border border-violet-200 rounded-xl resize-none focus:ring-2 focus:ring-violet-300 focus:border-violet-400 text-slate-700 leading-relaxed placeholder:text-violet-300 text-sm transition-all shadow-sm font-sans"
                            />
                        </div>

                        {/* Application Input */}
                        <div className="relative group">
                            <label className="text-xs font-semibold text-violet-600 mb-1.5 block">
                                3️⃣ Application (실무 적용)
                            </label>
                            <textarea
                                value={deepLogData.insightApplication || ''}
                                onChange={(e) => handleFieldChange('insightApplication', e.target.value)}
                                placeholder="이 개념을 내 코드나 프로젝트에 어떻게 적용할 수 있을까요? (예: 채팅 앱에서 message queue를 사용해 순서 보장)"
                                className="w-full h-28 p-4 bg-white border border-violet-200 rounded-xl resize-none focus:ring-2 focus:ring-violet-300 focus:border-violet-400 text-slate-700 leading-relaxed placeholder:text-violet-300 text-sm transition-all shadow-sm font-sans"
                            />
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

            {/* Mode Description */}
            <div className="flex flex-col items-center justify-center my-4">
                <p className={`text-xs text-center transition-colors duration-200 ${
                    logMode === 'SENSORY' ? 'text-lime-600 font-medium' :
                    logMode === 'INSIGHT' ? 'text-violet-600 font-medium' :
                    'text-slate-500'
                }`}>
                    {logMode === 'SENSORY' ? '✈️ 여행 중 감각적 경험을 상세히 기록합니다' :
                     logMode === 'INSIGHT' ? '💡 일상 관찰을 CS 개념으로 연결하여 통찰을 쌓습니다' :
                     '📝 일상적인 감정 로그를 작성합니다'}
                </p>
            </div>

            <Button
                onClick={handleSubmitWithLogType}
                disabled={isSubmitDisabled}
                className={`w-full py-4 shadow-lg text-sm tracking-wide ${
                    logMode === 'SENSORY'
                        ? 'bg-gradient-to-r from-lime-600 to-emerald-600 hover:from-lime-700 hover:to-emerald-700'
                        : logMode === 'INSIGHT'
                        ? 'bg-gradient-to-r from-violet-600 to-purple-600 hover:from-violet-700 hover:to-purple-700'
                        : ''
                }`}
            >
                <Send className="w-4 h-4" />
                {logMode === 'SENSORY' ? 'Commit Travel Log' :
                 logMode === 'INSIGHT' ? 'Commit Insight' :
                 'Commit Log Entry'}
            </Button>
        </>
    );
}
