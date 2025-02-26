// hooks/useGameInvites.ts
// 게임초대

import { useState, useCallback, useEffect } from 'react';
import { NotificationItem } from 'types/types';

interface UseGameInvitesReturn {
  gameInvites: NotificationItem[];
  isLoading: boolean;
  error: string | null;
  handleAcceptInvite: (inviteId: number, roomId: number) => Promise<void>;
  handleRejectInvite: (inviteId: number) => Promise<void>;
}

export const useGameInvites = (userId: number): UseGameInvitesReturn => {
  const [gameInvites, setGameInvites] = useState<NotificationItem[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // 웹소켓 연결 설정
  useEffect(() => {
    const socket = new WebSocket(`/sub/invite/${userId}`);

    socket.onmessage = event => {
      const data = JSON.parse(event.data);
      if (data.type === 'INVITE_REQUEST_RECEIVED') {
        // 새로운 게임 초대를 받았을 때
        setGameInvites(prev => [
          ...prev,
          {
            id: Date.now(), // 임시 ID
            type: 'gameInvite',
            from: {
              id: data.data.inviterId,
              nickname: data.data.inviterNickname,
              profileImg: data.data.inviterProfileImg,
              isOnline: true,
            },
            roomId: data.data.roomId,
            createdAt: new Date().toISOString(),
          },
        ]);
      }
    };

    return () => {
      socket.close();
    };
  }, [userId]);

  const handleAcceptInvite = async (inviteId: number, roomId: number) => {
    try {
      await fetch('api/team/invite/reaction', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          inviteId,
          roomId,
          accepted: true,
        }),
      });
      setGameInvites(prev => prev.filter(invite => invite.id !== inviteId));
    } catch (err) {
      setError('게임 초대 수락에 실패했습니다.');
    }
  };

  const handleRejectInvite = async (inviteId: number) => {
    try {
      await fetch('api/team/invite/reaction', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          inviteId,
          accepted: false,
        }),
      });
      setGameInvites(prev => prev.filter(invite => invite.id !== inviteId));
    } catch (err) {
      setError('게임 초대 거절에 실패했습니다.');
    }
  };

  return {
    gameInvites,
    isLoading,
    error,
    handleAcceptInvite,
    handleRejectInvite,
  };
};
