import React, { useState } from 'react';
import { Info, ChevronDown, ChevronUp, Activity, Anchor } from 'lucide-react';

export default function TelemetryGuide() {
    const [isOpen, setIsOpen] = useState(false);

    return (
        <div className="mt-6 border-t border-slate-200 pt-6">
            <button
                onClick={() => setIsOpen(!isOpen)}
                className="w-full flex items-center justify-between text-left group"
            >
                <div className="flex items-center gap-2">
                    <div className="p-1.5 rounded-full bg-slate-100 text-slate-500 group-hover:bg-slate-200 group-hover:text-slate-700 transition-colors">
                        <Info className="w-4 h-4" />
                    </div>
                    <span className="text-xs font-bold text-slate-500 uppercase tracking-wider group-hover:text-slate-700 transition-colors">
                        What is Flight Telemetry?
                    </span>
                </div>
                {isOpen ? (
                    <ChevronUp className="w-4 h-4 text-slate-400" />
                ) : (
                    <ChevronDown className="w-4 h-4 text-slate-400" />
                )}
            </button>

            <div className={`overflow-hidden transition-all duration-500 ease-in-out ${isOpen ? 'max-h-[800px] opacity-100 mt-4' : 'max-h-0 opacity-0'}`}>
                <div className="bg-slate-50 rounded-xl p-5 border border-slate-200 text-sm leading-relaxed text-slate-600 space-y-4">

                    <div>
                        <h4 className="font-bold text-slate-800 mb-1 flex items-center gap-2">
                            📡 Telemetry (원격 계측)
                        </h4>
                        <p className="text-slate-500 text-xs mb-2">
                            Remote (원격) + Measuring (측정)
                        </p>
                        <p>
                            "지금 기분이 어때?"라는 주관적 질문 대신, <strong>"지금 시스템이 비행 가능한 상태인가?"</strong>를 객관적인 수치로 확인하는 과정입니다.
                            슬픔이나 그리움은 제거해야 할 버그가 아니라, 단지 높게 측정된 중력 수치일 뿐입니다.
                        </p>
                    </div>

                    <div className="grid grid-cols-1 gap-3 pt-2">
                        <div className="bg-white p-3 rounded-lg border border-secondary-100 shadow-sm">
                            <h5 className="font-bold text-secondary-700 text-xs uppercase tracking-wide mb-1 flex items-center gap-1">
                                <Activity className="w-3 h-3" /> External Gravity (외부 인력)
                            </h5>
                            <p className="text-xs text-slate-600">
                                외부 행성(그 사람, 그리움)이 나를 당기는 힘입니다.
                                내가 못나서 힘든 게 아니라, <strong>상대 행성의 질량이 커서 발생하는 물리 현상</strong>입니다.
                            </p>
                        </div>

                        <div className="bg-white p-3 rounded-lg border border-primary-100 shadow-sm">
                            <h5 className="font-bold text-primary-700 text-xs uppercase tracking-wide mb-1 flex items-center gap-1">
                                <Anchor className="w-3 h-3" /> Core Stability (코어 안정성)
                            </h5>
                            <p className="text-xs text-slate-600">
                                외부 압력에도 선체(나)의 구조적 무결성을 유지하는 힘입니다.
                                밥을 먹고 잠을 자는 것은 단순한 휴식이 아니라, <strong>선체 방어막(Shield) 출력을 높이는 엔지니어링 행위</strong>입니다.
                            </p>
                        </div>
                    </div>

                    <div className="pt-2 border-t border-slate-200">
                        <p className="italic text-slate-500 text-xs text-center">
                            "경고등이 켜져도 당황하지 마세요. 당신은 이 비행의 통제권을 쥐고 있는 사령관입니다."
                        </p>
                    </div>
                </div>
            </div>
        </div>
    );
}
