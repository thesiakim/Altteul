// src/components/common/Modal/AdditionalModal.tsx
import Modal from '@components/Common/Modal';
import Button from '@components/Common/Button/Button';
import useModalStore from '@stores/modalStore';
import {
  MODAL_TYPES,
  GAME_TYPES,
  COMMON_MODAL_TYPES,
  GameType,
  CommonModalType,
} from 'types/modalTypes';
import useGameStore from '@stores/useGameStore';
import { useEffect, useState } from 'react';
import LoadingSpinner from '@components/Common/LoadingSpinner';
import { api } from '@utils/Api/commonApi';
import { useLocation } from 'react-router-dom';
import ErrorPage from '@pages/Error/ErrorPage';

type AdditionalModalProps = {
  isOpen: boolean;
  onClose: () => void;
  type: GameType;
  modalType: CommonModalType;
};

const AdditionalModal = ({ isOpen, onClose, type, modalType }: AdditionalModalProps) => {
  const location = useLocation();
  const { openModal } = useModalStore();
  const { gameId, userRoomId, isFinish } = useGameStore();
  const [isLoading, setIsLoading] = useState(false);
  const [coachResult, setCoachResult] = useState('');
  const isTeam = location.pathname.includes('game/team');
  const [code, setCode] = useState('');
  const [opponentName, setOpponentName] = useState('');
  const finish = isFinish === 'WIN' || isFinish === 'LOSE';

  // ai 코칭
  const fetchAiCoaching = async () => {
    if (userRoomId && gameId && finish) {
      try {
        setIsLoading(true);
        const response = await api.get(`/game/result/feedback`, {
          params: {
            gameId: gameId,
            teamId: userRoomId,
          },
        });

        // console.log('ai 코칭 결과:', response);
        setCoachResult(JSON.parse(response?.data.data.content));
      } catch (error) {
        console.error(error);
        <ErrorPage />;
      } finally {
        setIsLoading(false);
      }
    }
  };

  // 상대 팀 코드 불러오기
  const fetchUserCode = async () => {
    if (userRoomId && gameId && finish) {
      try {
        setIsLoading(true);
        const response = await api.get(`/game/code/${userRoomId}`, {
          params: {
            type: isTeam ? 'T' : 'S',
          },
        });

        // console.log('상대 팀 코드 불러오기:', response);
        setCode(response.data.code);
        if (!isTeam) setOpponentName(response?.data.data.nickname);
      } catch (error) {
        console.error(error);
        <ErrorPage />;
      } finally {
        setIsLoading(false);
      }
    }
  };

  // 모달 타입에 따른 설정
  const getModalConfig = () => {
    if (modalType === COMMON_MODAL_TYPES.CODE) {
      return {
        title: isTeam ? '상대 팀 코드' : `${opponentName}님의 코드`,
        content: code,
      };
    } else {
      return {
        title: 'AI 코칭 결과',
        content: coachResult,
      };
    }
  };

  const handleBack = () => {
    onClose();
    // type에 따라 다른 모달로 이동
    if (type === GAME_TYPES.SINGLE) {
      openModal(MODAL_TYPES.LIST);
    } else {
      openModal(MODAL_TYPES.NAVIGATE, { type: GAME_TYPES.TEAM });
    }
  };

  const config = getModalConfig();

  if (isLoading) {
    return <LoadingSpinner loading={isLoading} />;
  }

  return (
    <Modal
      isOpen={isOpen}
      onClose={onClose}
      width="26rem"
      height="25rem"
      className="bg-primary-black relative overflow-hidden border-2 border-primary-orange shadow-orange"
    >
      <div className="flex flex-col items-center justify-center h-full w-full gap-6">
        <h2 className="text-white text-xl font-bold">{config.title}</h2>

        {/* 내용 표시 영역 */}
        <div className="w-80 h-64 bg-gray-900 rounded-lg overflow-hidden">
          <div className="p-4 font-mono text-sm h-full overflow-y-auto">
            <pre className="text-white whitespace-pre-wrap break-all">
              <code>{config.content}</code>
            </pre>
          </div>
        </div>

        {/* 이전으로 버튼 */}
        <Button
          onClick={handleBack}
          backgroundColor="primary-orange"
          className="px-8 py-2 relative w-80"
        >
          이전으로
        </Button>
      </div>
    </Modal>
  );
};

export default AdditionalModal;
