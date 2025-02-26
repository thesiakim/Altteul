// 친구 목록 아이템
// src/components/Modal/Chat/items/FriendItem.tsx

import { useSocketStore } from '@stores/socketStore';
import useAuthStore from '@stores/authStore';
import useFriendChatStore from '@stores/friendChatStore';
import { deleteFriend, inviteFriend } from '@utils/Api/friendChatApi';
import startChat from '@assets/icon/friend/startChat.svg';
import deleteUser from '@assets/icon/friend/deleteUser.svg';
import inviteFriendIcon from '@assets/icon/friend/inviteFriend.svg';
import success from '@assets/icon/friend/success.svg';
import fail from '@assets/icon/friend/fail.svg';
import { useState } from 'react';
import { toast } from 'react-toastify';
import axios, { AxiosError } from 'axios';

interface FriendItemProps {
  friend: {
    userid: number;
    nickname: string;
    profileImg: string;
    isOnline: boolean;
  };
  onRefresh?: (friendId: number) => void;
}

const FriendItem = ({ friend, onRefresh }: FriendItemProps) => {
  const { sendMessage } = useSocketStore();
  const fcStore = useFriendChatStore();
  const userId = useAuthStore().userId;
  const roomId = JSON.parse(sessionStorage.getItem('matchData'))?.roomId || null;
  const [isInvite, setIsInvite] = useState(false);
  const [isInviteSuccess, setIsInviteSuccess] = useState(true);

  // 게임 초대
  const handleGameInvite = async () => {
    const payload = {
      inviteeId: friend.userid,
      roomId: roomId,
    };

    try {
      const data = await inviteFriend(payload);
      setIsInviteSuccess(true);
      toast.success(`${friend.nickname}님에게 초대를 보냈습니다.`, {
        position: 'top-center',
        autoClose: 3000,
      });
    } catch (error) {
      setIsInviteSuccess(false);

      // 에러 응답에서 메시지 추출
      let errorMessage = '초대 요청 중 오류가 발생했습니다.';

      if (axios.isAxiosError(error)) {
        const axiosError = error as AxiosError<{ message?: string }>;

        // 서버 응답이 있는 경우
        if (axiosError.response) {
          if (axiosError.response.status === 461) {
            errorMessage = axiosError.response.data?.message || '초대할 수 없는 상태입니다.';
          } else {
            errorMessage = axiosError.response.data?.message || errorMessage;
          }
        }
      }

      toast.error(errorMessage, {
        position: 'top-center',
        autoClose: 4000,
      });
    } finally {
      setIsInvite(true);
    }
  };

  // 친구 삭제
  const handleDeleteFriend = async () => {
    // 먼저 확인 대화상자 표시
    toast.info(
      <div>
        <p className="w-full">{`${friend.nickname}님을 친구 목록에서 삭제하시겠습니까?`}</p>
        <div className="mt-3 flex justify-end gap-3">
          <button
            onClick={() => {
              toast.dismiss();
              processFriendDelete();
            }}
            className="px-4 py-2 bg-red-500 text-white rounded-md"
          >
            삭제
          </button>
          <button
            onClick={() => toast.dismiss()}
            className="px-4 py-2 bg-gray-500 text-white rounded-md"
          >
            취소
          </button>
        </div>
      </div>,
      {
        position: 'top-center',
        autoClose: false,
        closeButton: false,
        closeOnClick: false,
      }
    );
  };

  // 실제 친구 삭제 처리
  const processFriendDelete = async () => {
    try {
      const payload = { userId: userId, friendId: friend.userid };
      await deleteFriend(Number(userId), friend.userid);
      sendMessage('/pub/friend/delete', payload);
      // console.log('친구 삭제 요청 전송', payload);

      toast.success(`${friend.nickname}님이 친구 목록에서 삭제되었습니다.`, {
        position: 'top-center',
        autoClose: 3000,
      });

      if (onRefresh) {
        onRefresh(friend.userid); // 친구 목록 새로고침(해당 요소 제거)
      }
    } catch (error) {
      console.error('친구 삭제 실패:', error);

      let errorMessage = '친구 삭제 중 오류가 발생했습니다.';

      if (axios.isAxiosError(error)) {
        const axiosError = error as AxiosError<{ message?: string }>;
        if (axiosError.response?.data?.message) {
          errorMessage = axiosError.response.data.message;
        }
      }

      toast.error(errorMessage, {
        position: 'top-center',
        autoClose: 3000,
      });
    }
  };

  const handleChat = async () => {
    fcStore.setActiveChatId(friend.userid);
    fcStore.setCurrentView('chat');
  };

  const showInviteButton = ['/match/team/composition'].includes(location.pathname);

  return (
    <div className="flex items-center justify-between bg-gray-04 p-3 rounded-lg">
      <div className="flex items-center gap-3">
        <div className="relative">
          <img src={friend.profileImg} alt="프로필" className="ml-2 w-10 h-10 rounded-full" />
          <div
            className={`absolute top-0 right-0 w-3 h-3 rounded-full border-2 ${
              friend.isOnline ? 'bg-green-500' : 'bg-gray-400'
            }`}
          />
        </div>
        <p className="font-semibold text-primary-white">{friend.nickname}</p>
      </div>
      |
      <div className="flex gap-0.5">
        {showInviteButton && !isInvite && (
          <button
            onClick={handleGameInvite}
            className="px-3 py-1 rounded-xl hover:bg-primary-white/25 "
          >
            <img src={inviteFriendIcon} alt="초대" className="w-7 h-7" />
          </button>
        )}

        {isInvite && isInviteSuccess && (
          <img src={success} alt="초대성공" className="w-8 h-8 mx-2.5 my-1" />
        )}
        {isInvite && !isInviteSuccess && (
          <img src={fail} alt="초대실패" className="w-7 h-7 mx-2.5 my-1" />
        )}

        <button onClick={handleChat} className="px-3 py-1 rounded-xl hover:bg-primary-white/25">
          <img src={startChat} alt="채팅" className="w-5 h-5" />
        </button>
        <button
          onClick={handleDeleteFriend}
          className="px-2 py-1 rounded-xl hover:bg-primary-white/25"
        >
          <img src={deleteUser} alt="차단" className="w-7 h-7" />
        </button>
      </div>
    </div>
  );
};

export default FriendItem;
