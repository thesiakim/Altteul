// src/components/Modal/FriendChat/Items/FriendRequestItem.tsx
import useAuthStore from '@stores/authStore';
import { useSocketStore } from '@stores/socketStore';
import useFriendChatStore from '@stores/friendChatStore';
import { friendRequestResponse } from '@utils/Api/friendChatApi';
import { FriendRequest } from 'types/types';
import requestAccept from '@assets/icon/friend/requestAccept.svg'
import requestReject from '@assets/icon/friend/requestReject.svg'

interface FriendRequestItemProps {
  request: FriendRequest;
  onRefresh: (friendRequestId: number) => void;
}

const FriendRequestItem = ({ request, onRefresh }: FriendRequestItemProps) => {
  const { sendMessage } = useSocketStore();
  const currentUserId = useAuthStore().userId;
  const { triggerFriendsRefresh } = useFriendChatStore();

  const handleResponse = (yn: "P" | "A" | "R") => {
    // 1. API 호출 전에 UI에서 먼저 제거
    onRefresh(request.friendRequestId);
    
    // 2. API 요청
    const response = {
      friendRequestId: request.friendRequestId,
      fromUserId: request.fromUserId, // 요청 보낸 사람 id
      toUserId: Number(currentUserId), // 요청 받은 사람 id
      requestStatus: yn,
    };
    
    friendRequestResponse(response).catch(error => {
      console.error('친구 요청 응답 실패:', error);
      // 실패 시 처리 (필요하다면 상태 되돌리기 등 구현)
    });
    
    sendMessage('/pub/friend/request/process', response);
    
    // 수락인 경우 친구 목록 새로고침 트리거 활성화
    if (yn === 'A') {
      triggerFriendsRefresh();
    }
  };

  return (
    <div className="flex items-center justify-between bg-gray-04 p-3 rounded-lg">
      <div className="flex items-center gap-3">
        <img src={request.fromUserProfileImg} alt="프로필" className="w-10 h-10 rounded-full" />
        <div>
          <p className="font-semibold text-primary-white">{request.fromUserNickname}</p>
          <p className="text-sm text-gray-400">친구 요청이 도착했습니다</p>
        </div>
      </div>
      <div className="flex">
        <button
          onClick={() => handleResponse('A')}
          className="px-2 py-1 rounded-xl hover:bg-primary-white/25"
        >
          <img src={requestAccept} alt="수락" className='w-8 h-8' />
        </button>
        <button
          onClick={() => handleResponse('R')}
          className="px-2 py-1 rounded-xl hover:bg-primary-white/25"
        >
          <img src={requestReject} alt="거절" className='w-8 h-8' />
        </button>
      </div>
    </div>
  );
};

export default FriendRequestItem;