import { useEffect, useState } from 'react';
import useGameStore from '@stores/useGameStore';
import { useSocketStore } from '@stores/socketStore';
import CodeEditor from '@components/Ide/SingleCodeEditor';
import Terminal from '@components/Ide/Terminal';
import IdeFooter from '@components/Ide/IdeFooter';
import ProblemInfo from '@components/Ide/ProblemInfo';
import SideProblemModal from '@components/Ide/SideProblemModal';
import GameUserList from '@components/Ide/GameUserList';
import useAuthStore from '@stores/authStore';
import { User } from 'types/types';
import useModalStore from '@stores/modalStore';
import { GAME_TYPES, MODAL_TYPES, RESULT_TYPES } from 'types/modalTypes';
import { toast } from 'react-toastify';

const MAX_REQUESTS = 1;

export interface SubmittedTeam {
  code: string;
  createdAt: string;
  duration: string | null;
  executeMemory: string | null;
  executeTime: string | null;
  gameResult: number;
  lang: string;
  members: Member[];
  passRate: number;
  point: number;
  teamId: number;
  totalHeadCount: number;
}

export interface Member {
  nickname: string;
  profileImage: string;
  tierId: number;
  userId: number;
}

const SingleIdePage = () => {
  const { gameId, roomId, users, setUserRoomId, setIsFinish } = useGameStore();
  const { subscribe, sendMessage, connected } = useSocketStore();
  const { openModal } = useModalStore();

  const [sideProblem, setSideProblem] = useState(null);
  const [completeUsers, setCompleteUsers] = useState<number[]>([]);
  const [userProgress, setUserProgress] = useState<Record<number, number>>({});
  const [leftUsers, setLeftUsers] = useState<User[]>([]);

  const [code, setCode] = useState('');
  const [language, setLanguage] = useState<'python' | 'java'>('python');
  const [showModal, setShowModal] = useState(false);
  const [requestCount, setRequestCount] = useState(0);
  const [output, setOutput] = useState<string>('');
  const { userId } = useAuthStore();
  const userRoomId = users.find(user => user.userId === Number(userId))?.roomId;
  console.log('roomId:', userRoomId);

  useEffect(() => {
    if (userRoomId && userRoomId !== roomId) {
      setUserRoomId(userRoomId);
    }
  }, [userId, users, roomId, setUserRoomId]);

  useEffect(() => {
    if (users.length > 0) {
      const initialProgress = users.reduce(
        (acc, user) => {
          acc[user.userId] = 0;
          return acc;
        },
        {} as Record<number, number>
      );

      setUserProgress(initialProgress);
    }
  }, [users]);

  useEffect(() => {
    if (!connected) return;

    // 사이드 문제 구독
    subscribe(`/sub/${gameId}/${userRoomId}/side-problem/receive`, data => {
      // console.log('📩 사이드 문제 수신:', data);
      setSideProblem(data);
      setShowModal(true);
    });

    // 코드 채점 결과 구독
    subscribe(`/sub/${gameId}/${userRoomId}/team-submission/result`, data => {
      console.log('📩 코드 채점 결과 수신:', data);

      if (data.type === '팀 제출 결과' && data.data.status === 'F') {
        console.log('틀린거 왜 안뜨지?');

        toast.error(
          `틀렸습니다! \nTC ${data.data.totalCount}개 중 ${data.data.passCount}개 맞았습니다.`,
          {
            position: 'bottom-center',
            autoClose: 5000,
          }
        );
      }
    });

    // 실시간 게임 현황 구독
    subscribe(`/sub/game/${gameId}/submission/result`, data => {
      console.log('📩 실시간 게임 현황 수신:', data);

      // console.log('data:', data);

      if (data?.type === '게임 현황' && data.data.gameType === 'S') {
        const submittedTeam: SubmittedTeam = data.data.submittedTeam;

        // submittedTeam의 gameResult가 0이 아니다 -> 문제를 해결함
        // => submittedTeam.members[0].userId를 completeUsers, userProgress에 추가
        // sumittedTeam의 gameResult가 0이다 -> 문제를 해결하지 못함
        // => userProgress[member.userId] = submittedTeam.passRate 추가
        // userProgress[member.userId] = 100이 되면 userProgress에서 해당 키와 값을 삭제

        setUserProgress(prev => ({
          ...prev,
          [submittedTeam.members[0].userId]: submittedTeam.passRate,
        }));

        setUserProgress(prev => {
          const updatedProgress = { ...prev };
          Object.keys(updatedProgress).forEach(userId => {
            if (updatedProgress[Number(userId)] === 100) {
              delete updatedProgress[Number(userId)];
            }
          });

          return updatedProgress;
        });

        if (submittedTeam.gameResult > 0) {
          setCompleteUsers(prev => [...new Set([...prev, submittedTeam.members[0].userId])]);
        }

        // 만약 정답이고, 정답을 맞춘 userId가 현재 로그인한 유저의 userId라면
        if (submittedTeam.gameResult > 0 && submittedTeam.members[0].userId === userId) {
          setRequestCount(2);
          setIsFinish('WIN');
          openModal(MODAL_TYPES.RESULT, {
            type: GAME_TYPES.SINGLE,
            result: RESULT_TYPES.SUCCESS,
          });
        }
      }

      // // ✅ submittedTeam이 존재하는지 확인
      // if (submittedTeam?.gameResult > 0 && Array.isArray(submittedTeam.members)) {
      //   submittedTeam.members.forEach((member: MemberInfo) => {
      //     if (!completeUsers.includes(member.userId)) {
      //       // ✅ 중복 체크 후 추가
      //       completeUsers.push(member.userId);
      //       updatedProgress[member.userId] = 100; // 통과율 100%
      //       console.log('문제 맞힌 사람:', member.userId);
      //     }

      //     if (member.userId === userId) {
      //       // 사이드 문제 모달 막기
      //       setRequestCount(5);
      //       setIsFinish('WIN');
      //       console.log('single isFinish:', isFinish);

      //       openModal(MODAL_TYPES.RESULT, {
      //         type: GAME_TYPES.SINGLE,
      //         result: RESULT_TYPES.SUCCESS,
      //       });
      //     }
      //   });
      // } else if (submittedTeam?.gameResult === 0 && Array.isArray(submittedTeam.members)) {
      //   submittedTeam.members.forEach((member: MemberInfo) => {
      //     updatedProgress[member.userId] = submittedTeam.passRate;
      //   });
      // }

      // setCompleteUsers(completeUsers);
      // setUserProgress(prev => ({ ...prev, ...updatedProgress }));
    });

    // 퇴장하기 구독
    subscribe(`/sub/single/room/${gameId}`, data => {
      console.log('퇴장하기 구독 데이터:', data);

      if (data.type === 'GAME_IN_PROGRESS_LEAVE') {
        const { leftUser } = data.data;

        // leftUser.userId가 userProgress에 있다면 userProgress에서 제거
        setUserProgress(prev => {
          const updatedProgress = { ...prev };
          if (updatedProgress[leftUser.userId] !== undefined) {
            delete updatedProgress[leftUser.userId];
          }
          return updatedProgress;
        });

        // leftUser.userId가 completeUsers에 있다면 leftUsers에 저장하지 않음
        setLeftUsers(prev => {
          if (!completeUsers.includes(leftUser.userId)) {
            return [...prev, leftUser]; // completeUsers에 없으면 leftUser에 추가
          }
          return prev; // completeUsers에 있으면 반환
        });
      }
    });

    return () => {
      // 모든 구독 해제
    };
  }, []);

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

  useEffect(() => {
    if (sideProblem) {
      setShowModal(true);
    }
  }, [sideProblem]);

  return (
    <div className="flex max-h-screen bg-primary-black border-t border-gray-04">
      <div className="min-w-[23em] max-w-[30rem] border-r border-gray-04">
        <ProblemInfo />
      </div>

      <div className="max-w-[65rem] flex-[46rem] border-r border-gray-04">
        <CodeEditor
          code={code}
          setCode={setCode}
          language={language}
          setLanguage={setLanguage}
          roomId={String(userRoomId)}
        />
        <Terminal output={output} isTeam={false} />
        <div className="text-center">
          <IdeFooter
            code={code}
            language={language}
            setOutput={setOutput}
            userRoomId={userRoomId}
          />
        </div>
      </div>

      <div className="grow min-w-[15rem]">
        <GameUserList
          users={users}
          completeUsers={completeUsers}
          userProgress={userProgress}
          leftUsers={leftUsers}
        />
      </div>

      {/* ✅ 사이드 문제 모달 */}
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

export default SingleIdePage;
