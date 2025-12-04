import React, { useState } from 'react';
import { Settings, Key, Save, X } from 'lucide-react';
import { getApiKey, setApiKey as saveApiKey, clearApiKey } from '../services/openaiService';

/**
 * ApiKeySettings - OpenAI API Key 설정 컴포넌트
 * 사용자가 API Key를 입력/저장/삭제할 수 있는 간단한 UI
 */
export default function ApiKeySettings() {
    const [isOpen, setIsOpen] = useState(false);
    const [apiKey, setApiKey] = useState('');
    const [isSaved, setIsSaved] = useState(false);

    const handleOpen = () => {
        const existingKey = getApiKey();
        if (existingKey) {
            // 기존 키가 있으면 마스킹해서 표시
            setApiKey(`${existingKey.substring(0, 7)}...${existingKey.substring(existingKey.length - 4)}`);
            setIsSaved(true);
        }
        setIsOpen(true);
    };

    const handleSave = () => {
        if (apiKey && apiKey.trim()) {
            saveApiKey(apiKey.trim());
            setIsSaved(true);
            alert('API Key가 저장되었습니다! 🎉');
        }
    };

    const handleClear = () => {
        if (confirm('API Key를 삭제하시겠습니까?')) {
            clearApiKey();
            setApiKey('');
            setIsSaved(false);
            alert('API Key가 삭제되었습니다.');
        }
    };

    const handleClose = () => {
        setIsOpen(false);
        setApiKey('');
        setIsSaved(false);
    };

    if (!isOpen) {
        return (
            <button
                onClick={handleOpen}
                className="fixed bottom-5 right-5 z-50 p-3 bg-slate-800 text-white rounded-full shadow-lg hover:bg-slate-900 transition-all"
                title="API Key 설정"
            >
                <Settings className="w-5 h-5" />
            </button>
        );
    }

    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50 p-4">
            <div className="bg-white rounded-xl shadow-2xl max-w-md w-full p-6">
                <div className="flex justify-between items-center mb-4">
                    <h3 className="text-lg font-bold flex items-center gap-2">
                        <Key className="w-5 h-5 text-purple-600" />
                        OpenAI API Key
                    </h3>
                    <button onClick={handleClose} className="text-slate-400 hover:text-slate-600">
                        <X className="w-5 h-5" />
                    </button>
                </div>

                <div className="mb-4">
                    <label className="block text-sm font-medium text-slate-700 mb-2">
                        API Key 입력
                    </label>
                    <input
                        type="text"
                        value={apiKey}
                        onChange={(e) => setApiKey(e.target.value)}
                        placeholder="sk-proj-..."
                        className="w-full px-3 py-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-purple-500 focus:border-purple-500 text-sm"
                        disabled={isSaved}
                    />
                    <p className="text-xs text-slate-500 mt-2">
                        OpenAI Platform에서 API Key를 발급받아 입력하세요.
                        <br />
                        저장된 Key는 localStorage에 안전하게 보관됩니다.
                    </p>
                </div>

                <div className="flex gap-2">
                    {!isSaved ? (
                        <button
                            onClick={handleSave}
                            className="flex-1 px-4 py-2 bg-purple-600 text-white rounded-lg hover:bg-purple-700 flex items-center justify-center gap-2"
                        >
                            <Save className="w-4 h-4" />
                            저장
                        </button>
                    ) : (
                        <button
                            onClick={handleClear}
                            className="flex-1 px-4 py-2 bg-rose-500 text-white rounded-lg hover:bg-rose-600 flex items-center justify-center gap-2"
                        >
                            <X className="w-4 h-4" />
                            삭제
                        </button>
                    )}
                    <button
                        onClick={handleClose}
                        className="px-4 py-2 border border-slate-300 text-slate-700 rounded-lg hover:bg-slate-50"
                    >
                        닫기
                    </button>
                </div>
            </div>
        </div>
    );
}
