import { useEffect, useState } from 'react';
import useGameStore from '@stores/useGameStore';
import { useSocketStore } from '@stores/socketStore';
import CodeEditor from '@components/Ide/TeamCodeEditor';
import Terminal from '@components/Ide/Terminal';
import IdeFooter from '@components/Ide/IdeFooter';
import ProblemInfo from '@components/Ide/ProblemInfo';
import SideProblemModal from '@components/Ide/SideProblemModal';
import useAuthStore from '@stores/authStore';
import resize from '@assets/icon/resize.svg';
import VoiceChat from '@components/Ide/VoiceChat';
import { teamApi } from '@utils/Api/commonApi';
import { GAME_TYPES, MODAL_TYPES, RESULT_TYPES } from 'types/modalTypes';
import useModalStore from '@stores/modalStore';
import { TeamInfo, User } from 'types/types';
import { createToken } from '@utils/openVidu';
import { SubmittedTeam } from '@pages/Ide/SingleIdePage';

const MAX_REQUESTS = 1;

const TeamIdePage = () => {
  const { gameId, users, setUserRoomId, myTeam, setIsFinish, opponent, matchId } = useGameStore();
  const { subscribe, sendMessage, connected } = useSocketStore();
  const [sideProblem, setSideProblem] = useState(null);
  const [code, setCode] = useState('');
  const [opponentCode, setOpponentCode] = useState(''); // 상대 팀 코드
  const [language, setLanguage] = useState<'python' | 'java'>('python');
  const [showModal, setShowModal] = useState(false);
  const [requestCount, setRequestCount] = useState(0);
  const [output, setOutput] = useState<string>('');
  const [leftPanelWidth, setLeftPanelWidth] = useState(50);
  const [isResizing, setIsResizing] = useState(false);
  const [usedItem, setUsedItem] = useState(null);
  const { userId, token } = useAuthStore();
  const userRoomId = myTeam.roomId;
  const { openModal } = useModalStore();
  const [myTeamRemainingUsers, setMyTeamRemainingUsers] = useState<number[]>([]);
  const [opponentRemainingUsers, setOpponentRemainingUsers] = useState<number[]>([]);
  const [hittedItem, setHittedItem] = useState<{ type: string; duration: number } | null>(null);

  useEffect(() => {
    if (userRoomId) {
      setUserRoomId(userRoomId);
    }
  }, [userId, users, userRoomId, setUserRoomId]);

  useEffect(() => {
    if (!connected) return;

    // ✅ 사이드 문제 구독
    subscribe(`/sub/${gameId}/${userRoomId}/side-problem/receive`, data => {
      // console.log('📩 사이드 문제 수신:', data);
      setSideProblem(data);
      setShowModal(true);
    });

    // 코드 채점 결과 구독
    subscribe(`/sub/${gameId}/${userRoomId}/team-submission/result`, data => {
      // console.log('📩 코드 채점 결과 수신:', data);
    });

    // 실시간 게임 현황 구독
    subscribe(`/sub/game/${gameId}/submission/result`, data => {
      // console.log('📩 실시간 게임 현황 수신:', data);

      if (data?.type === '게임 현황' && data.data.gameType === 'T') {
        const submittedTeam: SubmittedTeam = data.data.submittedTeam;

        // submittedTeam.gameResult === 1이면 submittedTeam.teamId가 이긴거
        // submittedTeam.teamId === userRoomId이면 내가 승리
        // submittedTeam.gameResult === 1 && submittedTeam.teamId === userRoomId 라면 승리
        // => WIN 모달 띄우고, setIsFinish('WIN')

        // submittedTeam.gameResult === 1 && submittedTeam.teamId !== userRoomID 라면 패배
        // => LOSE 모달 띄우고, setIsFinish('LOSE')

        if (submittedTeam.gameResult === 1 && submittedTeam.teamId === userRoomId) {
          setIsFinish('WIN');
          openModal(MODAL_TYPES.RESULT, {
            type: GAME_TYPES.TEAM,
            result: RESULT_TYPES.SUCCESS,
          });
        } else if (submittedTeam.gameResult === 1 && submittedTeam.teamId !== userRoomId) {
          setIsFinish('LOSE');
          openModal(MODAL_TYPES.RESULT, {
            type: GAME_TYPES.TEAM,
            result: RESULT_TYPES.FAILURE,
          });
        }
      }
    });

    // ✅ 상대 팀 코드 구독
    subscribe(`/sub/${gameId}/${userRoomId}/opponent-submission/result`, data => {
      // console.log('📩 상대 팀 코드 수신:', data);
      setOpponentCode(data.code);
    });

    subscribe(`/sub/team/room/${matchId}`, data => {
      // console.log('퇴장하기 구독 데이터', data);

      if (data?.type === 'GAME_IN_PROGRESS_LEAVE') {
        const { remainingUsers } = data.data;

        // const myTeamRemain = remainingUsers[userRoomId]: 우리팀에서 남아있는 사람
        // 오디오 트랙에 참가하지 않은 사람은 현재 반투명 표시
        // myTeamRemain을 VoiceChat에 props로 전달
        // myTeam.users와 myTeamRemain을 비교해서
        // myTeamRemain에 없는 user.userId는 퇴장했음을 UI에 표시 _> 반투명+퇴장(가상요소로) 표시
        //
        // const opponentRemain = remaininUsers[opponent.roomId]: 상대 팀에서 남아있는 사람
        // VoiceChat에서 상대팀을 뺴고, 상대팀을 따로 모아두는 컴포넌트에다가 전달
        // opponent.users와 opponentRemain을 비교해서,
        // opponentRemain에 없는 user.userId는 퇴장했음을 UI에 표시 -> 반투명+퇴장(가상요소로) 표시
        // opponentRemain.length === 0이면 우리 팀이 이긴거
        // => WIN 모달 띄우고, setIsFinish('WIN')

        // 우리 팀 남은 인원
        const myTeamRemain = remainingUsers[userRoomId]?.map((user: User) => user.userId) || [];
        setMyTeamRemainingUsers(myTeamRemain);

        // 상대 팀 남은 인원
        const opponentRemain =
          remainingUsers[opponent.roomId]?.map((user: User) => user.userId) || [];
        setOpponentRemainingUsers(opponentRemain);

        if (opponentRemain.length === 0) {
          setIsFinish('WIN');
          openModal(MODAL_TYPES.RESULT, {
            type: GAME_TYPES.TEAM,
            result: RESULT_TYPES.SUCCESS,
          });
        }
      }
    });

    // 사용할 아이템 정보 구독
    subscribe(`/sub/${gameId}/${userRoomId}/item/hit`, data => {
      console.log('아이템 정보 전송', data);

      if (data.type === '아이템 사용') {
        // 이 데이터를 받으면 item을 사용하는 함수를 실행한다.
        // item의 종류: 3초간 내 코드 에디터 가리기
        // item을 사용하는 함수를 작성하고 실행하는 로직이 필요함
        setHittedItem({ type: 'blind', duration: 5 });
        setTimeout(() => {
          setHittedItem(null);
        }, 5 * 1000);
      }
    });

    return () => {
      // ✅ 구독 해제
    };
  }, [gameId]);

  // ✅ 사이드 문제 요청
  const requestSideProblem = () => {
    sendMessage(`/pub/side/receive`, { gameId, teamId: userRoomId });
    // console.log('📨 사이드 문제 요청 전송');
  };

  // ✅ 10분마다 자동으로 사이드 문제 요청
  useEffect(() => {
    if (!connected) return;
    if (requestCount >= MAX_REQUESTS) return;

    const interval = setInterval(() => {
      if (requestCount < MAX_REQUESTS) {
        requestSideProblem();
        setRequestCount(prev => prev + 1);
      } else {
        clearInterval(interval);
      }
    }, 10 * 1000);

    return () => clearInterval(interval);
  }, [requestCount]);

  const handleResizeEditor = (e: React.MouseEvent<HTMLDivElement>) => {
    e.preventDefault();
    setIsResizing(true);
  };

  useEffect(() => {
    if (!isResizing) return;

    const handleMouseMove = (moveEvent: MouseEvent) => {
      setLeftPanelWidth(prevWidth => {
        const deltaX = (moveEvent.movementX / window.innerWidth) * 100;
        const newWidth = prevWidth + deltaX;
        return Math.max(20, Math.min(80, newWidth));
      });
    };

    const handleMouseUp = () => {
      setIsResizing(false);
    };

    document.addEventListener('mousemove', handleMouseMove);
    document.addEventListener('mouseup', handleMouseUp);

    return () => {
      document.removeEventListener('mousemove', handleMouseMove);
      document.removeEventListener('mouseup', handleMouseUp);
    };
  }, [isResizing]);

  return (
    <div className="flex max-w-full h-screen mt-[3.5rem] bg-primary-black border-t border-gray-04">
      <div className="min-w-[23rem] max-w-[30rem] border-gray-04">
        <ProblemInfo />
        {/* <VoiceChat roomId={userRoomId} voiceToken={voiceToken} /> */}
      </div>

      {/* ✅ 우리 팀과 상대 팀의 코드 에디터 표시 */}
      <div className="flex grow mt-4 max-w-full box-border">
        <div
          className="border-r pr-4 border-gray-04"
          style={{ width: `${leftPanelWidth}%`, minWidth: '20%' }}
        >
          <h2 className="text-center">우리 팀 코드</h2>
          <CodeEditor
            roomId={String(userRoomId)}
            code={code}
            setCode={setCode}
            language={language}
            setLanguage={setLanguage}
            myRoomId={String(userRoomId)}
            item={hittedItem}
          />
          <Terminal output={output} isTeam={true} />
          <div className="text-center">
            <IdeFooter
              code={code}
              language={language}
              setOutput={setOutput}
              userRoomId={userRoomId}
            />
          </div>
        </div>
        <div
          className="w-2 cursor-ew-resize bg-gray-03 hover:bg-gray-04 transition shrink-0 rounded-lg flex items-center justify-center"
          onMouseDown={handleResizeEditor}
        >
          <img src={resize} alt="코드 너비 조정" />
        </div>
        <div style={{ width: `${100 - leftPanelWidth}%`, minWidth: '20%' }}>
          <h2 className="text-center">상대 팀 코드</h2>
          <div className="blur-sm pointer-events-none">
            <CodeEditor
              roomId={String(opponent.roomId)}
              code={opponentCode}
              setCode={() => {}}
              language={language}
              readOnly={true}
              myRoomId={String(userRoomId)}
            />
          </div>
          <VoiceChat opponentRemainingUsers={opponentRemainingUsers} />
        </div>
      </div>

      {/* ✅ 사이드 문제 모달 (팀원이 이미 풀었다면 결과 표시) */}
      {showModal && sideProblem && (
        <SideProblemModal
          gameId={gameId}
          roomId={userRoomId}
          problem={sideProblem?.data}
          onClose={() => setShowModal(false)}
        />
      )}
    </div>
  );
};

export default TeamIdePage;
