import useGameStore from '@stores/useGameStore';
import { useLocation, useNavigate } from 'react-router-dom';
import logo from '@assets/icon/Altteul.svg';
import { useState } from 'react';
import ConfirmModal from '@components/Common/ConfirmModal';
import useModalStore from '@stores/modalStore';
import { GAME_TYPES, MODAL_TYPES, RESULT_TYPES } from 'types/modalTypes';
import { useSocketStore } from '@stores/socketStore';
import useAuthStore from '@stores/authStore';
import { api } from '@utils/Api/commonApi';
import ErrorPage from '@pages/Error/ErrorPage';

const GameGnb = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const isTeam = location.pathname.includes('/game/team');
  const { problem } = useGameStore();
  const { token } = useAuthStore();
  const { userRoomId, gameId, matchId, resetGameInfo } = useGameStore();
  const socket = useSocketStore();

  const [showModal, setShowModal] = useState(false);

  const handleOutConfirm = () => {
    setShowModal(true);
  };
  const handleNavigate = async () => {
    setShowModal(false);
    if (userRoomId) {
      try {
        const response = await api.post(
          '/game/leave',
          {
            roomId: userRoomId,
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
          setTimeout(() => {
            navigate('/');
            resetGameInfo();
          }, 200);
        }
      } catch (error) {
        console.error(error);
        <ErrorPage />;
      }
    }
  };

  return (
    <>
      <nav className="fixed top-0 w-full bg-primary-black z-50 px-8 text-sm">
        <div className="flex item-center justify-between h-[3.5rem]">
          {/* 좌측 영역 */}
          <div className="flex items-center mr-auto">
            <button onClick={handleOutConfirm} className="flex items-center">
              <img src={logo} alt="홈으로" className="w-5/6" />
            </button>
            <div className="flex space-x-1 item-center h-full gap-1">
              <p className="py-4 text-gray-02 font-semibold">알고리즘 배틀 &gt;</p>
              {isTeam ? (
                <>
                  <p className="py-4 text-gray-02 font-semibold">팀전 &gt;</p>
                </>
              ) : (
                <>
                  <p className="py-4 text-gray-02 font-semibold">개인전 &gt;</p>
                </>
              )}
              <p className="py-3 text-primary-white font-semibold text-lg">
                {problem?.problemId}. {problem?.problemTitle}
              </p>
            </div>
          </div>

          {/* 우측 영역 */}
          {/* TODO: 나가기 클릭 시 진짜 나갈건지 확인하는 모달 추가 */}
          <div className="flex items-center space-x-4 ml-auto">
            <button
              onClick={handleOutConfirm}
              className="px-3 py-1 bg-primary-orange text-primary-white rounded-lg hover:bg-secondary-orange hover:text-gray-01 transition-colors"
            >
              나가기
            </button>
          </div>
        </div>
      </nav>
      {showModal && (
        <ConfirmModal onConfirm={handleNavigate} onCancel={() => setShowModal(false)} />
      )}
    </>
  );
};

export default GameGnb;
