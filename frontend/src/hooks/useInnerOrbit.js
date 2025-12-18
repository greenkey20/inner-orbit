import { useState, useEffect, useRef } from 'react';

const PROMPTS = [
    "현재 감지되는 '외부 인력(그리움)'의 강도는 어떠하며, 이에 대응하기 위해 나는 어떤 '코어 활동'을 하고 있나요?",
    "시스템(나)의 안정성을 높이기 위해, 지금 당장 투입할 수 있는 긍정적 데이터(행동)는 무엇인가요?",
    "외부 신호(연락하고 싶은 충동)를 훌륭하게 제어한 나 자신에게, 관리자로서 격려의 로그를 남겨주세요.",
    "관계 변수를 제외하고, 오늘 나의 '성장 알고리즘'에서 가장 잘 작동한 부분은 무엇인가요?",
    "현재 나의 물리적 리소스(체력, 허기) 상태는 어떠한가요? 재충전이 필요한가요?",
    "미래에 대한 불확실한 예측 프로세스를 끄고, '오늘'이라는 런타임에만 집중한다면 무엇을 실행해야 할까요?"
];

/**
 * useInnerOrbit - Inner Orbit 애플리케이션의 상태 및 비즈니스 로직을 관리하는 커스텀 훅
 * 
 * @returns {Object} 상태, 액션, ref를 포함하는 객체
 */
export default function useInnerOrbit() {
    // ===== 상태 관리 =====
    const [entries, setEntries] = useState([]);

    const [message, setMessage] = useState("");
    const [stability, setStability] = useState(50);
    const [gravity, setGravity] = useState(50);
    const [view, setView] = useState('write');
    const [promptIndex, setPromptIndex] = useState(0);
    const [showPrompt, setShowPrompt] = useState(true);
    const [isHeaderExpanded, setIsHeaderExpanded] = useState(true);
    const [deepLogData, setDeepLogData] = useState({
        location: '',
        sensoryVisual: '',
        sensoryAuditory: '',
        sensoryTactile: '',
        isDeepLog: false
    });
    const fileInputRef = useRef(null);

    // ===== 백엔드에서 로그 불러오기 =====
    useEffect(() => {
        const fetchLogs = async () => {
            try {
                const response = await fetch('/api/logs');
                if (response.ok) {
                    const data = await response.json();
                    // 백엔드 응답을 프론트엔드 형식으로 변환
                    const formattedEntries = data.map(log => ({
                        id: log.id,
                        date: log.createdAt,
                        content: log.content,
                        stability: log.stability,
                        gravity: log.gravity,
                        updatedAt: log.updatedAt,
                        analysis: log.analysisResult,
                        // Deep Log 필드 추가
                        location: log.location,
                        sensoryVisual: log.sensoryVisual,
                        sensoryAuditory: log.sensoryAuditory,
                        sensoryTactile: log.sensoryTactile,
                        isDeepLog: log.isDeepLog || false
                    }));
                    setEntries(formattedEntries);
                }
            } catch (error) {
                console.error('Failed to fetch logs:', error);
            }
        };

        fetchLogs();
    }, []);

    // ===== CRUD 작업 =====

    /**
     * 새 로그 엔트리를 생성하고 백엔드에 저장
     * @param {Object} customLogData - (선택) Deep Log 데이터가 포함된 커스텀 로그 데이터
     */
    const handleSubmit = async (customLogData = null) => {
        // Deep Log 모드인 경우 customLogData 사용, 아니면 기본 message 체크
        const isDeepLogSubmission = customLogData && customLogData.isDeepLog;
        const hasContent = isDeepLogSubmission 
            ? (customLogData.content?.trim() || customLogData.location?.trim() || 
               customLogData.sensoryVisual?.trim() || customLogData.sensoryAuditory?.trim() || 
               customLogData.sensoryTactile?.trim())
            : message.trim();

        if (!hasContent) return;

        try {
            // 요청 데이터 구성
            const requestData = isDeepLogSubmission 
                ? {
                    content: customLogData.content || '',
                    stability: stability,
                    gravity: gravity,
                    location: customLogData.location || null,
                    sensoryVisual: customLogData.sensoryVisual || null,
                    sensoryAuditory: customLogData.sensoryAuditory || null,
                    sensoryTactile: customLogData.sensoryTactile || null,
                    isDeepLog: true
                }
                : {
                    content: message,
                    stability: stability,
                    gravity: gravity,
                    location: null,
                    sensoryVisual: null,
                    sensoryAuditory: null,
                    sensoryTactile: null,
                    isDeepLog: false
                };

            const response = await fetch('/api/logs', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(requestData)
            });

            if (response.ok) {
                const newLog = await response.json();
                // 백엔드 응답을 프론트엔드 형식으로 변환
                const newEntry = {
                    id: newLog.id,
                    date: newLog.createdAt,
                    content: newLog.content,
                    stability: newLog.stability,
                    gravity: newLog.gravity,
                    analysis: newLog.analysisResult,
                    // Deep Log 필드 추가
                    location: newLog.location,
                    sensoryVisual: newLog.sensoryVisual,
                    sensoryAuditory: newLog.sensoryAuditory,
                    sensoryTactile: newLog.sensoryTactile,
                    isDeepLog: newLog.isDeepLog || false
                };

                setEntries([newEntry, ...entries]);
                setMessage("");
                // Deep Log 데이터 초기화
                if (isDeepLogSubmission) {
                    setDeepLogData({
                        location: '',
                        sensoryVisual: '',
                        sensoryAuditory: '',
                        sensoryTactile: '',
                        isDeepLog: false
                    });
                }
                setView('history');
            } else {
                console.error('Failed to save log');
                alert('로그 저장에 실패했습니다.');
            }
        } catch (error) {
            console.error('Error saving log:', error);
            alert('로그 저장 중 오류가 발생했습니다.');
        }
    };

    /**
     * 로그 엔트리 삭제
     * @param {number} id - 삭제할 엔트리의 ID
     */
    const deleteEntry = (id) => {
        if (window.confirm('이 로그를 영구 삭제하시겠습니까?')) {
            setEntries(entries.filter(e => e.id !== id));
        }
    };

    /**
     * 로그 엔트리 수정
     * @param {number} id - 수정할 엔트리의 ID
     * @param {string} newContent - 새로운 콘텐츠
     * @param {number} newGravity - 새로운 gravity 값
     * @param {number} newStability - 새로운 stability 값
     * @param {Object} deepLogFields - (선택) Deep Log 필드들 {location, sensoryVisual, sensoryAuditory, sensoryTactile}
     */
    const updateEntry = async (id, newContent, newGravity, newStability, deepLogFields = null) => {
        if (!newContent.trim()) {
            alert('내용을 입력해주세요.');
            return;
        }

        try {
            // 요청 데이터 구성
            const requestData = {
                content: newContent,
                stability: newStability,
                gravity: newGravity,
                ...(deepLogFields && {
                    location: deepLogFields.location || null,
                    sensoryVisual: deepLogFields.sensoryVisual || null,
                    sensoryAuditory: deepLogFields.sensoryAuditory || null,
                    sensoryTactile: deepLogFields.sensoryTactile || null
                })
            };

            // 백엔드에 PUT 요청
            const response = await fetch(`/api/logs/${id}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(requestData)
            });

            if (response.ok) {
                const updatedLog = await response.json();
                // 백엔드 응답으로 state 업데이트
                setEntries(entries.map(entry => {
                    if (entry.id === id) {
                        return {
                            ...entry,
                            content: updatedLog.content,
                            gravity: updatedLog.gravity,
                            stability: updatedLog.stability,
                            updatedAt: updatedLog.updatedAt,
                            // Deep Log 필드 업데이트
                            location: updatedLog.location,
                            sensoryVisual: updatedLog.sensoryVisual,
                            sensoryAuditory: updatedLog.sensoryAuditory,
                            sensoryTactile: updatedLog.sensoryTactile
                        };
                    }
                    return entry;
                }));
            } else {
                console.error('Failed to update log');
                alert('로그 수정에 실패했습니다.');
            }
        } catch (error) {
            console.error('Error updating log:', error);
            alert('로그 수정 중 오류가 발생했습니다.');
        }
    };

    /**
     * 로그 엔트리의 AI 분석 결과 저장
     * @param {number} id - 엔트리 ID
     * @param {Object} analysis - 분석 결과 객체
     */
    const updateEntryAnalysis = (id, analysis) => {
        setEntries(entries.map(entry => {
            if (entry.id === id) {
                return {
                    ...entry,
                    analysis: {
                        ...analysis,
                        analyzedAt: new Date().toISOString()
                    }
                };
            }
            return entry;
        }));
    };

    /**
     * 프롬프트를 메시지 영역에 삽입
     * @param {string} customPrompt - (선택) 사용자 정의 프롬프트 (AI 생성 질문 등)
     */
    const insertPrompt = (customPrompt) => {
        const promptText = customPrompt || PROMPTS[promptIndex];
        setMessage((prev) => prev ? `${prev}\n\n[Query]: ${promptText}\n[Log]: ` : `[Query]: ${promptText}\n[Log]: `);
    };

    // ===== 데이터 백업/복원 =====

    /**
     * 모든 엔트리를 JSON 파일로 다운로드
     */
    const downloadData = () => {
        const dataStr = JSON.stringify(entries, null, 2);
        const blob = new Blob([dataStr], { type: "application/json" });
        const url = URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = `inner_orbit_backup_${new Date().toISOString().slice(0, 10)}.json`;
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
    };

    /**
     * JSON 파일에서 데이터를 불러와 병합
     * @param {Event} event - 파일 입력 이벤트
     */
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
                        setEntries([...newUnique, ...entries].sort((a, b) => b.id - a.id));
                    }
                }
            } catch (error) { alert('데이터 파싱 오류가 발생했습니다.'); }
        };
        reader.readAsText(file);
        event.target.value = '';
    };

    // ===== 반환 인터페이스 =====
    return {
        // 상태
        entries,
        gravity,
        stability,
        message,
        view,
        promptIndex,
        showPrompt,
        isHeaderExpanded,
        prompts: PROMPTS,
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
    };
}
