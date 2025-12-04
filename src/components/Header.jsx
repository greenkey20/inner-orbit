import React from 'react';
import { Hexagon, ChevronDown, ChevronUp } from 'lucide-react';

// Vite의 public 폴더에서 이미지를 참조 (절대 경로)
const HERO_IMAGE_URL = "/gravity.jpg";

/**
 * Header - 히어로 이미지, 타이틀, 시스템 상태를 표시하는 컴포넌트
 * 
 * @param {boolean} isExpanded - 헤더 확장 상태
 * @param {function} onToggleExpand - 확장/축소 토글 핸들러
 * @param {number} entryCount - 로그 엔트리 개수
 */
export default function Header({ isExpanded, onToggleExpand, entryCount }) {
    return (
        <header className={`relative transition-all duration-500 ease-in-out ${isExpanded ? 'h-72' : 'h-24'} overflow-hidden bg-slate-900 group`}>
            {/* Background Image Layer */}
            <div className="absolute inset-0 z-0">
                <img
                    src={HERO_IMAGE_URL}
                    alt="Gravity Assist Visualization"
                    className={`w-full h-full object-cover transition-opacity duration-500 ${isExpanded ? 'opacity-90' : 'opacity-40'}`}
                />
                {/* Gradient Overlay for Text Readability */}
                <div className="absolute inset-0 bg-gradient-to-t from-slate-900 via-slate-900/50 to-transparent" />
                <div className="absolute inset-0 bg-slate-900/20 mix-blend-multiply" />
            </div>

            {/* Content Layer */}
            <div className="relative z-10 flex flex-col justify-end h-full p-6 text-white">
                <div className="flex justify-between items-end mb-1">
                    <div>
                        <div className="flex items-center gap-2 mb-1">
                            <Hexagon className="w-5 h-5 text-primary-400 fill-primary-400/20" />
                            <h1 className="text-2xl font-bold tracking-tight text-white shadow-sm">Inner Orbit</h1>
                        </div>
                        <div className="flex items-center gap-2">
                            <span className="px-2 py-0.5 rounded-full bg-primary-500/30 border border-primary-400/30 text-[10px] font-mono text-primary-200 backdrop-blur-sm">
                                System Active
                            </span>
                            <span className="text-[10px] font-mono text-slate-400">
                                Log Sequence #{entryCount + 1}
                            </span>
                        </div>
                    </div>

                    <button
                        onClick={onToggleExpand}
                        className="p-2 rounded-full bg-white/10 hover:bg-white/20 backdrop-blur-md transition-colors text-white/80"
                    >
                        {isExpanded ? <ChevronUp className="w-4 h-4" /> : <ChevronDown className="w-4 h-4" />}
                    </button>
                </div>

                {/* Expanded Description */}
                <div className={`overflow-hidden transition-all duration-500 ${isExpanded ? 'max-h-20 opacity-100 mt-3' : 'max-h-0 opacity-0 mt-0'}`}>
                    <p className="text-xs text-slate-200 font-light leading-relaxed max-w-[90%] opacity-90">
                        <span className="font-semibold text-primary-300">Gravity Assist Protocol:</span><br />
                        Converting emotional gravity into growth momentum.
                        (감정의 인력을 성장의 동력으로 전환합니다.)
                    </p>
                </div>
            </div>
        </header>
    );
}
