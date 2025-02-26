// src/components/common/Modal/GameNavigateModal.tsx
import React, { useState } from 'react';
import Modal from '@components/Common/Modal';
import Button from '@components/Common/Button/Button';
import useModalStore from '@stores/modalStore';
import { useNavigate } from 'react-router-dom';
import { GAME_TYPES, COMMON_MODAL_TYPES, GameType } from 'types/modalTypes';
import { api } from '@utils/Api/commonApi';
import useGameStore from '@stores/useGameStore';
import useAuthStore from '@stores/authStore';
import { useSocketStore } from '@stores/socketStore';
import ErrorPage from '@pages/Error/ErrorPage';
import { Feedback } from '@components/Modal/Result/ResultItem';
import LoadingSpinner from '@components/Common/LoadingSpinner';

type NavigateModalProps = {
  isOpen: boolean;
  onClose: () => void;
  type: GameType;
};

const NavigateModal = ({ isOpen, onClose, type }: NavigateModalProps) => {
  const { openModal } = useModalStore();
  const {
    myTeam,
    gameId,
    matchId,
    resetGameInfo,
    isFinish,
    userRoomId: singleRoomId,
  } = useGameStore();
  const gameStore = useGameStore();
  const { token, userId } = useAuthStore();
  const socket = useSocketStore();
  const navigate = useNavigate();
  const isTeam = location.pathname.includes('/game/team');
  const [showModal, setShowModal] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [feedbacks, setFeedbacks] = useState<Feedback | null>(null);
  const [modalType, setModalType] = useState<'Feedback' | 'OpponentCode' | null>(null);
  const [userCodes, setUserCodes] = useState<{ nickname: string; code: string }>(null);
  const userRoomId = isTeam ? myTeam?.roomId : singleRoomId;

  // 한 문제 더 도전하기
  const handleContinue = async () => {
    console.log('isFinish:', isFinish);
    console.log('userRoomId:', userRoomId);

    if ((userRoomId && isFinish === 'WIN') || (userRoomId && isFinish === 'LOSE')) {
      console.log('한문제 더 도전하기 클릭');

      try {
        const response = await api.post(
          '/game/leave',
          {
            roomId: isTeam ? myTeam.roomId : userRoomId,
          },
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          }
        );

        if (response.status === 200) {
          socket.unsubscribe(`/sub/game/${gameId}/submission/result`);
          socket.unsubscribe(`/sub/${gameId}/${userRoomId}/team-submission/result`);
          socket.unsubscribe(`/sub/${gameId}/${userRoomId}/side-problem/receive`);
          if (!isTeam) {
            socket.unsubscribe(`/sub/single/room/${gameId}`);
          } else if (isTeam) {
            socket.unsubscribe(`/sub/${gameId}/${userRoomId}/opponent-submission/result`);
            socket.unsubscribe(`/sub/team/room/${matchId}`);
          }
          onClose();
          navigate('/match/select');
          resetGameInfo();
        }
      } catch (error) {
        console.error(error);
        <ErrorPage />;
      }
    }
  };
  // console.log('gameId:', gameId);
  // console.log('teamId:', userRoomId);

  // AI 코칭 결과 보기
  const handleAiCoaching = async () => {
    if ((userRoomId && isFinish === 'WIN') || (userRoomId && isFinish === 'LOSE')) {
      setShowModal(true);
      setModalType('Feedback');
      try {
        setIsLoading(true);
        const response = await api.get(`/game/result/feedback`, {
          params: {
            gameId: gameId,
            teamId: userRoomId,
          },
        });

        setFeedbacks(JSON.parse(response?.data.data.content) || null);
      } catch (error) {
        console.error(error);
        <ErrorPage />;
      } finally {
        setIsLoading(false);
      }
    }
  };

  // 상대 코드 보기
  const handleOpponentCode = async () => {
    if ((userRoomId && isFinish === 'WIN') || (userRoomId && isFinish === 'LOSE')) {
      setShowModal(true);
      setModalType('OpponentCode');

      try {
        setIsLoading(true);
        const response = await api.get(`/game/code/${userRoomId}`, {
          params: {
            type: isTeam ? 'T' : 'S',
            gameId: gameId,
          },
        });

        // console.log(response);

        setUserCodes(response?.data.data.userCodes[0] || null);
        // console.log(userCodes);
      } catch (error) {
        console.error(error);
        <ErrorPage />;
      } finally {
        setIsLoading(false);
      }
    }
  };

  const handleNavigateMain = async () => {
    if ((userRoomId && isFinish === 'WIN') || (userRoomId && isFinish === 'LOSE')) {
      try {
        const response = await api.post(
          '/game/leave',
          {
            roomId: isTeam ? myTeam.roomId : userRoomId,
          },
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          }
        );

        if (response.status === 200) {
          socket.unsubscribe(`/sub/game/${gameId}/submission/result`);
          socket.unsubscribe(`/sub/${gameId}/${userRoomId}/team-submission/result`);
          socket.unsubscribe(`/sub/${gameId}/${userRoomId}/side-problem/receive`);
          if (!isTeam) {
            socket.unsubscribe(`/sub/single/room/${gameId}`);
          } else if (isTeam) {
            socket.unsubscribe(`/sub/${gameId}/${userRoomId}/opponent-submission/result`);
            socket.unsubscribe(`/sub/team/room/${matchId}`);
          }
          onClose();
          navigate('/');
          resetGameInfo();
        }
      } catch (error) {
        console.error(error);
        <ErrorPage />;
      }
    }
  };

  const handleClose = () => {
    setShowModal(false);
  };

  if (isLoading) return <LoadingSpinner loading={isLoading} />;

  return (
    <>
      <Modal
        isOpen={isOpen}
        onClose={onClose}
        minHeight="8rem"
        className="bg-primary-black relative overflow-hidden border-2 border-primary-orange shadow-orange p-12"
      >
        <div className="flex flex-col items-center justify-center h-full w-full gap-4">
          {/* 한 문제 더 도전하기 - 공통 */}
          <Button
            onClick={handleContinue}
            backgroundColor="primary-orange"
            className="px-8 py-2 w-80"
          >
            한 문제 더 도전하기
          </Button>

          {/* 팀전일 때만 보이는 버튼들 */}
          {type === GAME_TYPES.TEAM && (
            <>
              <Button
                onClick={handleAiCoaching}
                backgroundColor="primary-orange"
                className="px-8 py-2 w-80"
              >
                AI 코칭 결과 보기
              </Button>

              <Button
                onClick={handleOpponentCode}
                backgroundColor="primary-orange"
                className="px-8 py-2 w-80"
              >
                상대 팀 코드 보기
              </Button>
            </>
          )}

          {/* 메인으로 - 공통 */}
          <Button
            onClick={handleNavigateMain}
            backgroundColor="primary-orange"
            className="px-8 py-2 relative w-80"
          >
            메인으로
          </Button>
        </div>
      </Modal>
      {showModal && isTeam && (
        <div
          className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50"
          style={{ zIndex: 999 }}
        >
          <div
            className="bg-primary-black p-8 rounded-md shadow-side min-w-[30rem] max-w-[50rem] shadow-gray-03 text-center min-h-[20rem] flex flex-col"
            style={{ zIndex: 60 }}
          >
            {modalType === 'Feedback' ? (
              <>
                <h2 className="text-2xl font-semibold text-primary-white mb-8">
                  🤖 AI 코칭 결과 🔎
                </h2>
                <div className="bg-primary-black text-primary-white overflow-auto text-left flex-1">
                  <p className="min-h-[10rem] max-h-[33rem] text-center">
                    {feedbacks ? (
                      <>
                        <h3 className="text-md font-bold text-balance text-gray-01">
                          {feedbacks.summary}
                        </h3>
                        {feedbacks.algorithmType?.length > 0 && (
                          <div className="mt-4 mb-4">
                            <ul className="flex flex-wrap gap-2 mt-2 justify-center">
                              {feedbacks.algorithmType.map((type, index) => (
                                <li
                                  key={index}
                                  className="px-3 py-1 bg-gray-03 text-gray-01 rounded-md text-sm"
                                >
                                  {type}
                                </li>
                              ))}
                            </ul>
                          </div>
                        )}
                        <ul className="mb-6">
                          {feedbacks.feedback?.map((item, index) => (
                            <li
                              key={index}
                              className="my-2 p-2 border-b border-gray-03 last:border-none"
                            >
                              <p className="text-primary-orange font-semibold text-balance">
                                {item.code}
                              </p>
                              <p className="text-gray-02 text-balance">{item.description}</p>
                            </li>
                          ))}
                        </ul>
                      </>
                    ) : (
                      '코칭 정보가 없습니다.'
                    )}
                  </p>
                </div>
              </>
            ) : (
              <>
                <h2 className="text-lg font-semibold text-primary-white mb-8">상대 팀의 코드</h2>
                <div className="bg-primary-black text-primary-white overflow-auto text-left flex-1 flex items-center justify-center">
                  {userCodes.code.length > 0 ? (
                    <pre className="bg-gray-06 max-h-[20rem]">
                      <code className="text-sm">{userCodes.code}</code>
                    </pre>
                  ) : (
                    <p className="text-center">아직 상대팀이 문제를 풀지 못했어요. 😓</p>
                  )}
                </div>
              </>
            )}
            <div>
              <button
                onClick={handleClose}
                className="px-4 py-2 bg-primary-orange text-primary-white rounded-md hover:bg-secondary-orange transition-colors"
              >
                닫기
              </button>
            </div>
          </div>
        </div>
      )}
    </>
  );
};

export default NavigateModal;
