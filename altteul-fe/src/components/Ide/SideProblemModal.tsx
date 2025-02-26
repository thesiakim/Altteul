import { useState, useEffect } from 'react';
import { useSocketStore } from '@stores/socketStore';
import SmallButton from '@components/Common/Button/SmallButton ';
import useAuthStore from '@stores/authStore';
import useGameStore from '@stores/useGameStore';

interface SideProblemModalProps {
  gameId: number;
  roomId: number;
  problem: {
    id: number;
    title: string;
    description: string;
  };
  onClose: () => void;
}

type SideProblemResult = {
  data: {
    status: string;
    // TODO: 사이드문제 결과에 userId 추가되면 ? 삭제
    userId?: number | null;
    itemId: number | null;
    itemName: string | null;
    bonusPoint: number | null;
  };
  type: string;
};

const SideProblemModal = ({ gameId, roomId, problem, onClose }: SideProblemModalProps) => {
  const { userId } = useAuthStore();
  const isTeam = location.pathname.includes('/game/team');
  const [answer, setAnswer] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [submissionResult, setSubmissionResult] = useState<string | null>(null);
  const [showForfeitMessage, setShowForfeitMessage] = useState(false);
  const [sideProblemResult, setSideProblemResult] = useState<SideProblemResult>(null);
  const { subscribe, sendMessage, connected } = useSocketStore();
  const [isMyAnswer, setIsMyAnswer] = useState(false);
  const { myTeam } = useGameStore();
  const userRoomId = myTeam?.roomId;

  // console.log('roomId:', roomId);
  

  useEffect(() => {
    if (!connected) return;

    // 사이드 문제 채점 결과 구독
    subscribe(`/sub/${gameId}/${roomId}/side-problem/result`, data => {
      console.log('📩 사이드 문제 채점 결과 수신:', data);
      setSideProblemResult(data);

      if (isTeam) {
        setIsMyAnswer(data.data?.roomId === userRoomId);
      }
    });
  }, [connected, gameId, roomId, subscribe]);

  useEffect(() => {
    const timer = setTimeout(() => {
      onClose();
    }, 60000);

    return () => clearTimeout(timer);
  }, []);

  // 사이드 문제 채점 요청
  const handleSubmit = () => {
    if (!answer.trim() || isSubmitting) return;

    setIsSubmitting(true);
    // const upperCaseAnswer = answer.toUpperCase();

    sendMessage(`/pub/side/submit`, {
      gameId,
      teamId: roomId,
      sideProblemId: problem.id,
      answer: answer,
    });
  };

  // 서버에서 결과를 받으면 정답 여부 확인
  useEffect(() => {
    if (sideProblemResult && isSubmitting) {
      setIsSubmitting(false);

      console.log('sideProblemResult:', sideProblemResult.data);

      if (sideProblemResult?.data.status === 'P') {
        setSubmissionResult(
          isTeam
            ? isMyAnswer
              ? `🎉 사이드 문제를 풀었습니다! ${sideProblemResult?.data.itemName} 아이템을 얻었어요!`
              : `🎉 팀원이 사이드 문제를 풀었습니다! ${sideProblemResult?.data.itemName} 아이템을 얻었어요!`
            : `🎉 사이드 문제를 풀었습니다! ${sideProblemResult?.data.bonusPoint} 포인트를 추가로 얻었어요!`
        );

        isTeam && requestUseItem(sideProblemResult?.data.itemId);
      } else {
        setSubmissionResult('❌ 사이드 문제를 풀지 못했어요. 포인트 획득에 실패했습니다.');
      }
    }
  }, [sideProblemResult, isSubmitting]);

  // 안풀래요 버튼 클릭 시
  const handleForfeit = () => {
    setShowForfeitMessage(true);
  };

  // 아이템 사용 요청을 보내는 함수
  const requestUseItem = (itemId: number) => {
    // console.log('teamId :' + roomId);
    sendMessage('/pub/item/use', { gameId, teamId: userRoomId, itemId });
  };

  return (
    <div className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50">
      <div
        className="bg-primary-black p-8 rounded-md shadow-side w-[30rem] shadow-gray-03"
        onClick={e => e.stopPropagation()}
      >
        <div className="text-center mb-6">
          <h1 className="text-xxl font-semibold mb-1">보너스 문제!</h1>
          <p className="text-primary-orange">
            추가 {isTeam ? '아이템을' : '점수를'} 획득할 수 있습니다.
          </p>
          <p className="text-gray-02">1분 뒤 자동으로 창이 닫힙니다! 빠르게 풀어보세요.</p>
        </div>

        {/* ✅ 안풀래요 버튼을 누른 경우 */}
        {showForfeitMessage ? (
          <div className="text-center mt-6">
            <p className="text-gray-02 font-semibold">
              ❌ 사이드 문제를 풀지 못해 추가 {isTeam ? '아이템' : '점수'} 획득을 못했어요.
            </p>
            <SmallButton onClick={onClose} className="mt-4 px-4 py-2">
              확인
            </SmallButton>
          </div>
        ) : (
          <>
            {isTeam && isMyAnswer && (
              <div className="text-center text-primary-orange font-bold my-4">
                팀원이 사이드 문제를 풀었습니다!
                {/* <SmallButton onClick={onClose} className="mt-4 px-4 py-2">
                  확인
                </SmallButton> */}
              </div>
            )}
            {/* ✅ 제출 결과가 없을 때 문제 표시 */}
            {!submissionResult && (
              <>
                <div className="mb-10">
                  <p className="text-center mb-4">
                    힌트:
                    <span className="text-primary-orange font-semibold"> {problem.title}</span>
                  </p>
                  <p>{problem.description}</p>
                </div>

                {/* ✅ 사용자 입력 필드 */}
                {!isMyAnswer && (
                  <div className="flex gap-2 items-center">
                    <input
                      type="text"
                      value={answer}
                      onChange={e => setAnswer(e.target.value)}
                      placeholder="정답을 입력해주세요."
                      className="w-[15rem] px-4 py-2 rounded-md bg-gray-03"
                      disabled={isSubmitting || isMyAnswer}
                    />
                    <SmallButton
                      onClick={handleSubmit}
                      className="px-4 py-2"
                      disabled={!answer.trim() || isSubmitting}
                    >
                      {isSubmitting ? '제출 중...' : '제출'}
                    </SmallButton>
                    <SmallButton
                      onClick={handleForfeit}
                      className="px-4 py-2"
                      backgroundColor="gray-03"
                    >
                      안풀래요
                    </SmallButton>
                  </div>
                )}
              </>
            )}

            {/* ✅ 제출 결과 표시 */}
            {submissionResult && (
              <div className="text-center mt-6">
                <p
                  className={
                    sideProblemResult?.data.status === 'P'
                      ? 'text-primary-orange font-bold'
                      : 'text-gray-04 font-bold'
                  }
                >
                  {submissionResult}
                </p>
                <SmallButton onClick={onClose} className="mt-4 px-4 py-2">
                  확인
                </SmallButton>
              </div>
            )}
          </>
        )}
      </div>
    </div>
  );
};

export default SideProblemModal;
