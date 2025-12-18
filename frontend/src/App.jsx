import React from 'react';
import useInnerOrbit from './hooks/useInnerOrbit';
import Header from './components/Header';
import StatusDashboard from './components/StatusDashboard';
import PromptAssistant from './components/PromptAssistant';
import LogEditor from './components/LogEditor';
import LogHistory from './components/LogHistory';
import TelemetryGuide from './components/TelemetryGuide';
import FlightTrajectory from './components/FlightTrajectory';
import Analytics from './components/Analytics';

/**
 * App - Inner Orbit 메인 애플리케이션 컴포넌트
 * 모든 비즈니스 로직은 useInnerOrbit 훅에 위임하고, 
 * UI 컴포넌트들을 조립(Composition)하는 역할만 수행합니다.
 */
export default function App() {
  const {
    // 상태
    entries,
    gravity,
    stability,
    message,
    view,
    promptIndex,
    showPrompt,
    isHeaderExpanded,
    prompts,
    deepLogData,

    // Setters
    setMessage,
    setStability,
    setGravity,
    setView,
    setPromptIndex,
    setShowPrompt,
    setIsHeaderExpanded,
    setDeepLogData,

    // Actions
    handleSubmit,
    deleteEntry,
    updateEntry,
    updateEntryAnalysis,
    insertPrompt,
    downloadData,
    handleFileUpload,

    // Refs
    fileInputRef
  } = useInnerOrbit();

  return (
    <div className="min-h-screen bg-slate-100 text-slate-800 font-sans selection:bg-primary-100">
      <div className="max-w-md mx-auto min-h-screen bg-white shadow-2xl overflow-hidden flex flex-col relative border-x border-slate-200">

        {/* Header */}
        <Header
          isExpanded={isHeaderExpanded}
          onToggleExpand={() => setIsHeaderExpanded(!isHeaderExpanded)}
          entryCount={entries.length}
        />

        {/* Tabs */}
        <div className="flex p-2 gap-2 bg-slate-100 border-b border-slate-200">
          <button
            onClick={() => setView('write')}
            className={`flex-1 py-2.5 text-xs font-bold uppercase tracking-wider rounded-lg transition-colors ${view === 'write'
              ? 'bg-white shadow-sm text-slate-800 border border-slate-200'
              : 'text-slate-500 hover:text-slate-700'
              }`}
          >
            Status & Log
          </button>
          <button
            onClick={() => setView('history')}
            className={`flex-1 py-2.5 text-xs font-bold uppercase tracking-wider rounded-lg transition-colors ${view === 'history'
              ? 'bg-white shadow-sm text-slate-800 border border-slate-200'
              : 'text-slate-500 hover:text-slate-700'
              }`}
          >
            Flight History
          </button>
          <button
            onClick={() => setView('analytics')}
            className={`flex-1 py-2.5 text-xs font-bold uppercase tracking-wider rounded-lg transition-colors ${view === 'analytics'
              ? 'bg-white shadow-sm text-slate-800 border border-slate-200'
              : 'text-slate-500 hover:text-slate-700'
              }`}
          >
            Analytics
          </button>
        </div>

        {/* Main Content */}
        <main className="flex-1 overflow-y-auto p-5 scrollbar-hide pb-24 bg-slate-50">
          {view === 'write' ? (
            <div className="space-y-6 animate-fade-in">
              <StatusDashboard
                gravity={gravity}
                stability={stability}
                onGravityChange={setGravity}
                onStabilityChange={setStability}
              />

              <PromptAssistant
                currentPrompt={prompts[promptIndex]}
                onNextPrompt={() => setPromptIndex((p) => (p + 1) % prompts.length)}
                onInsertPrompt={insertPrompt}
                showPrompt={showPrompt}
                onTogglePrompt={setShowPrompt}
                gravity={gravity}
                stability={stability}
              />

              <LogEditor
                message={message}
                onMessageChange={setMessage}
                onSubmit={handleSubmit}
                showPrompt={showPrompt}
                onShowPrompt={setShowPrompt}
                deepLogData={deepLogData}
                onDeepLogChange={setDeepLogData}
              />

              <TelemetryGuide />
            </div>
          ) : view === 'history' ? (
            <div className="space-y-6 animate-fade-in">
              <FlightTrajectory entries={entries} />

              <LogHistory
                entries={entries}
                onDeleteEntry={deleteEntry}
                onUpdateEntry={updateEntry}
                onUpdateEntryAnalysis={updateEntryAnalysis}
                onDownloadData={downloadData}
                onFileUpload={handleFileUpload}
                fileInputRef={fileInputRef}
              />
            </div>
          ) : (
            <div className="animate-fade-in">
              <Analytics entries={entries} />
            </div>
          )}
        </main>
      </div>

      {/* Global Styles */}
      <style>{`
        @keyframes fade-in { from { opacity: 0; transform: translateY(10px); } to { opacity: 1; transform: translateY(0); } }
        .animate-fade-in { animation: fade-in 0.4s cubic-bezier(0.16, 1, 0.3, 1) forwards; }
        .scrollbar-hide::-webkit-scrollbar { display: none; }
        .scrollbar-hide { -ms-overflow-style: none; scrollbar-width: none; }
      `}</style>
    </div>
  );
}