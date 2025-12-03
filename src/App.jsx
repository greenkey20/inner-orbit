import React, { useState, useEffect, useRef } from 'react';
import { Send, Zap, Activity, Shield, Calendar, Trash2, RefreshCw, Anchor, Coffee, Code, Download, Upload, AlertCircle, PenTool, BookOpen, Hexagon, ChevronDown, ChevronUp } from 'lucide-react';

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

const PROMPTS = [
  "현재 감지되는 '외부 인력(그리움)'의 강도는 어떠하며, 이에 대응하기 위해 나는 어떤 '코어 활동'을 하고 있나요?",
  "시스템(나)의 안정성을 높이기 위해, 지금 당장 투입할 수 있는 긍정적 데이터(행동)는 무엇인가요?",
  "외부 신호(연락하고 싶은 충동)를 훌륭하게 제어한 나 자신에게, 관리자로서 격려의 로그를 남겨주세요.",
  "관계 변수를 제외하고, 오늘 나의 '성장 알고리즘'에서 가장 잘 작동한 부분은 무엇인가요?",
  "현재 나의 물리적 리소스(체력, 허기) 상태는 어떠한가요? 재충전이 필요한가요?",
  "미래에 대한 불확실한 예측 프로세스를 끄고, '오늘'이라는 런타임에만 집중한다면 무엇을 실행해야 할까요?"
];

// 생성된 이미지 URL (제미나이 생성 이미지)
const HERO_IMAGE_URL = "http://googleusercontent.com/image_generation_content/0";

export default function App() {
  const [entries, setEntries] = useState(() => {
    try {
      const saved = localStorage.getItem('journalEntries');
      return saved ? JSON.parse(saved) : [];
    } catch (e) {
      return [];
    }
  });
  
  const [message, setMessage] = useState("");
  const [stability, setStability] = useState(50);
  const [gravity, setGravity] = useState(50);
  const [view, setView] = useState('write');
  const [promptIndex, setPromptIndex] = useState(0);
  const [showPrompt, setShowPrompt] = useState(true);
  const [isHeaderExpanded, setIsHeaderExpanded] = useState(true);
  const fileInputRef = useRef(null);

  useEffect(() => {
    localStorage.setItem('journalEntries', JSON.stringify(entries));
  }, [entries]);

  const handleSubmit = () => {
    if (!message.trim()) return;
    
    const newEntry = {
      id: Date.now(),
      date: new Date().toLocaleString('ko-KR', { 
        year: 'numeric', month: 'long', day: 'numeric', 
        hour: '2-digit', minute: '2-digit', hour12: false 
      }),
      content: message,
      stability,
      gravity,
    };

    setEntries([newEntry, ...entries]);
    setMessage("");
    setView('history');
  };

  const deleteEntry = (id) => {
    if (window.confirm('이 로그를 영구 삭제하시겠습니까?')) {
      setEntries(entries.filter(e => e.id !== id));
    }
  };

  const insertPrompt = () => {
    const promptText = PROMPTS[promptIndex];
    setMessage((prev) => prev ? `${prev}\n\n[Query]: ${promptText}\n[Log]: ` : `[Query]: ${promptText}\n[Log]: `);
  };

  const downloadData = () => {
    const dataStr = JSON.stringify(entries, null, 2);
    const blob = new Blob([dataStr], { type: "application/json" });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `inner_orbit_backup_${new Date().toISOString().slice(0,10)}.json`;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  };

  const handleFileUpload = (event) => {
    const file = event.target.files[0];
    if (!file) return;
    const reader = new FileReader();
    reader.onload = (e) => {
      try {
        const imported = JSON.parse(e.target.result);
        if (Array.isArray(imported)) {
          if (window.confirm(`${imported.length}개의 데이터 로그를 병합하시겠습니까?`)) {
             const existingIds = new Set(entries.map(e => e.id));
             const newUnique = imported.filter(e => !existingIds.has(e.id));
             setEntries([...newUnique, ...entries].sort((a,b) => b.id - a.id));
          }
        }
      } catch (error) { alert('데이터 파싱 오류가 발생했습니다.'); }
    };
    reader.readAsText(file);
    event.target.value = '';
  };

  return (
    <div className="min-h-screen bg-slate-100 text-slate-800 font-sans selection:bg-indigo-100">
      <div className="max-w-md mx-auto min-h-screen bg-white shadow-2xl overflow-hidden flex flex-col relative border-x border-slate-200">
        
        {/* Immersive Header with Image */}
        <header className={`relative transition-all duration-500 ease-in-out ${isHeaderExpanded ? 'h-72' : 'h-24'} overflow-hidden bg-slate-900 group`}>
          {/* Background Image Layer */}
          <div className="absolute inset-0 z-0">
            <img 
              src={HERO_IMAGE_URL} 
              alt="Gravity Assist Visualization" 
              className={`w-full h-full object-cover transition-opacity duration-500 ${isHeaderExpanded ? 'opacity-90' : 'opacity-40'}`}
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
                  <Hexagon className="w-5 h-5 text-indigo-400 fill-indigo-400/20" />
                  <h1 className="text-2xl font-bold tracking-tight text-white shadow-sm">Inner Orbit</h1>
                </div>
                <div className="flex items-center gap-2">
                   <span className="px-2 py-0.5 rounded-full bg-indigo-500/30 border border-indigo-400/30 text-[10px] font-mono text-indigo-200 backdrop-blur-sm">
                    System Active
                  </span>
                  <span className="text-[10px] font-mono text-slate-400">
                    Log Sequence #{entries.length + 1}
                  </span>
                </div>
              </div>
              
              <button 
                onClick={() => setIsHeaderExpanded(!isHeaderExpanded)}
                className="p-2 rounded-full bg-white/10 hover:bg-white/20 backdrop-blur-md transition-colors text-white/80"
              >
                {isHeaderExpanded ? <ChevronUp className="w-4 h-4" /> : <ChevronDown className="w-4 h-4" />}
              </button>
            </div>

            {/* Expanded Description */}
            <div className={`overflow-hidden transition-all duration-500 ${isHeaderExpanded ? 'max-h-20 opacity-100 mt-3' : 'max-h-0 opacity-0 mt-0'}`}>
              <p className="text-xs text-slate-200 font-light leading-relaxed max-w-[90%] opacity-90">
                <span className="font-semibold text-indigo-300">Gravity Assist Protocol:</span><br/>
                Converting emotional gravity into growth momentum.
                (감정의 인력을 성장의 동력으로 전환합니다.)
              </p>
            </div>
          </div>
        </header>

        {/* Tabs */}
        <div className="flex p-2 gap-2 bg-slate-100 border-b border-slate-200">
          <button onClick={() => setView('write')} className={`flex-1 py-2.5 text-xs font-bold uppercase tracking-wider rounded-lg transition-colors ${view === 'write' ? 'bg-white shadow-sm text-slate-800 border border-slate-200' : 'text-slate-500 hover:text-slate-700'}`}>Status & Log</button>
          <button onClick={() => setView('history')} className={`flex-1 py-2.5 text-xs font-bold uppercase tracking-wider rounded-lg transition-colors ${view === 'history' ? 'bg-white shadow-sm text-slate-800 border border-slate-200' : 'text-slate-500 hover:text-slate-700'}`}>Flight History</button>
        </div>

        <main className="flex-1 overflow-y-auto p-5 scrollbar-hide pb-24 bg-slate-50">
          {view === 'write' ? (
            <div className="space-y-6 animate-fade-in">
              
              {/* 1. Status Dashboard */}
              <section className="bg-white rounded-xl border border-slate-200 p-5 space-y-5 shadow-sm">
                <h3 className="text-[11px] font-bold text-slate-400 uppercase tracking-widest mb-2 flex items-center gap-2">
                  <Activity className="w-3 h-3" /> Flight Telemetry
                </h3>
                
                {/* Gravity (External Influence) */}
                <div className="space-y-2">
                  <div className="flex justify-between text-sm">
                    <span className="flex items-center gap-2 text-slate-600 font-medium">
                      <Zap className="w-4 h-4 text-amber-500" /> 
                      External Gravity (외부 인력)
                    </span>
                    <span className="font-mono font-bold text-amber-600">{gravity}%</span>
                  </div>
                  <input type="range" min="0" max="100" value={gravity} onChange={(e) => setGravity(parseInt(e.target.value))} className="w-full h-1.5 bg-amber-100 rounded-lg appearance-none cursor-pointer accent-amber-500" />
                </div>

                {/* Stability (Internal Strength) */}
                <div className="space-y-2 pt-1">
                  <div className="flex justify-between text-sm">
                    <span className="flex items-center gap-2 text-slate-600 font-medium">
                      <Shield className="w-4 h-4 text-indigo-500" /> 
                      Core Stability (코어 안정성)
                    </span>
                    <span className="font-mono font-bold text-indigo-600">{stability}%</span>
                  </div>
                  <input type="range" min="0" max="100" value={stability} onChange={(e) => setStability(parseInt(e.target.value))} className="w-full h-1.5 bg-indigo-100 rounded-lg appearance-none cursor-pointer accent-indigo-600" />
                </div>
              </section>

              {/* 2. Prompt Interface */}
              {showPrompt && (
                <div className="bg-slate-800 p-4 rounded-xl border border-slate-700 relative text-slate-200 shadow-md">
                  <div className="flex justify-between items-start mb-3">
                    <span className="text-[10px] font-bold text-indigo-300 uppercase flex items-center gap-1 tracking-wider">
                      <BookOpen className="w-3 h-3" /> Navigation Query
                    </span>
                    <div className="flex gap-2">
                      <button onClick={() => setPromptIndex((p) => (p + 1) % PROMPTS.length)} className="text-slate-400 hover:text-white transition-colors"><RefreshCw className="w-3 h-3" /></button>
                      <button onClick={() => setShowPrompt(false)} className="text-slate-400 hover:text-white transition-colors"><Trash2 className="w-3 h-3" /></button>
                    </div>
                  </div>
                  <p className="text-sm font-medium leading-relaxed mb-4 text-slate-100">
                    "{PROMPTS[promptIndex]}"
                  </p>
                  <Button variant="secondary" onClick={insertPrompt} className="w-full text-xs py-2 h-auto bg-slate-700 border-slate-600 text-slate-200 hover:bg-slate-600 hover:text-white">
                    Apply to Log
                  </Button>
                </div>
              )}

              {/* 3. Log Area */}
              <section className="space-y-2">
                <div className="flex justify-between items-end px-1">
                  <label className="text-xs font-bold text-slate-500 uppercase flex items-center gap-2 tracking-wider">
                    <PenTool className="w-3 h-3" /> Captain's Log
                  </label>
                  {!showPrompt && (
                    <button onClick={() => setShowPrompt(true)} className="text-xs text-indigo-600 hover:text-indigo-800 font-medium">
                      + Show Query
                    </button>
                  )}
                </div>
                <div className="relative group">
                  <textarea
                    value={message}
                    onChange={(e) => setMessage(e.target.value)}
                    placeholder="감정의 인력을 성장의 동력으로. 오늘 당신의 항해 기록을 남겨주세요."
                    className="w-full h-60 p-5 bg-white border border-slate-200 rounded-xl resize-none focus:ring-2 focus:ring-slate-200 focus:border-slate-400 text-slate-700 leading-relaxed placeholder:text-slate-300 text-sm transition-all shadow-sm font-sans"
                  />
                </div>
              </section>

              <Button onClick={handleSubmit} disabled={!message.trim()} className="w-full py-4 shadow-lg text-sm tracking-wide">
                <Send className="w-4 h-4" />
                Commit Log Entry
              </Button>
            </div>
          ) : (
            <div className="space-y-6 animate-fade-in">
              {/* Backup Section */}
              <div className="bg-white p-4 rounded-xl border border-slate-200 flex flex-col gap-3 shadow-sm">
                 <div className="flex items-center gap-2 text-[10px] font-bold text-slate-400 uppercase tracking-widest">
                    <AlertCircle className="w-3 h-3" /> Data Persistence
                 </div>
                 <div className="flex gap-2">
                    <Button onClick={downloadData} variant="outline" className="flex-1 text-xs bg-slate-50">
                      <Download className="w-3 h-3" /> Backup (JSON)
                    </Button>
                    <Button onClick={() => fileInputRef.current?.click()} variant="outline" className="flex-1 text-xs bg-slate-50">
                      <Upload className="w-3 h-3" /> Restore
                    </Button>
                    <input type="file" ref={fileInputRef} onChange={handleFileUpload} accept="application/json" className="hidden" />
                 </div>
              </div>

              {entries.length === 0 ? (
                <div className="text-center py-16 opacity-60">
                  <Hexagon className="w-10 h-10 mx-auto mb-4 text-slate-300" />
                  <p className="text-sm text-slate-500">No logs found.<br/>Initialize your first entry.</p>
                </div>
              ) : (
                entries.map((entry) => (
                  <Card key={entry.id} className="p-5 hover:shadow-md transition-shadow group">
                    <div className="flex justify-between items-start mb-3 border-b border-slate-100 pb-3">
                      <div className="flex items-center gap-2">
                        <Calendar className="w-3 h-3 text-slate-400" />
                        <span className="text-xs font-mono font-medium text-slate-500">{entry.date}</span>
                      </div>
                      <button onClick={() => deleteEntry(entry.id)} className="text-slate-300 hover:text-rose-500 opacity-0 group-hover:opacity-100 transition-all"><Trash2 className="w-3 h-3" /></button>
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
          )}
        </main>
      </div>
      <style>{`
        @keyframes fade-in { from { opacity: 0; transform: translateY(10px); } to { opacity: 1; transform: translateY(0); } }
        .animate-fade-in { animation: fade-in 0.4s cubic-bezier(0.16, 1, 0.3, 1) forwards; }
        .scrollbar-hide::-webkit-scrollbar { display: none; }
        .scrollbar-hide { -ms-overflow-style: none; scrollbar-width: none; }
      `}</style>
    </div>
  );
}