// src/components/Modal/Chat/tabs/NotificationTab.tsx
import { useState, useEffect } from 'react';
import useFriendChatStore from '@stores/friendChatStore';
import FriendRequestItem from '@components/Modal/FriendChat/Items/FriendRequestItem';
import { getFriendRequests } from '@utils/Api/friendChatApi';
import { FriendRequest } from 'types/types';

const NotificationTab = () => {
  const [friendRequests, setFriendRequests] = useState<FriendRequest[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const fcStore = useFriendChatStore();

  // 알림 데이터 가져오기
  const fetchNotifications = async () => {
    try {
      setIsLoading(true);
      const response = await getFriendRequests({ page: 0, size: 10 });
      setFriendRequests(response.data.data.friendRequests);
    } catch (error) {
      console.error('알림 로드 실패:', error);
      setError('알림을 불러오는데 실패했습니다.');
    } finally {
      setIsLoading(false);
    }
  };

  // 탭 변경 시 데이터 새로고침
  useEffect(() => {
    fetchNotifications();
  }, []);

  // 친구 요청 수락/거절시 리스트에서 즉시 제거
  const removeRequestFromList = (friendRequestId: number) => {
    setFriendRequests(prev => prev.filter(
      request => request.friendRequestId !== friendRequestId
    ));
  };

  return (
    <div className="flex flex-col h-full gap-4 px-4">
      {/* 알림 타입 선택 탭 */}
      <div className="flex border-b border-gray-700 mt-5">
        <div className="flex-1 pb-3 text-center transition-colors text-primary-orange border-b-2 border-primary-orange">
          친구 요청
        </div>
      </div>

      {/* 알림 목록 */}
      <div className="flex-1 overflow-y-auto">
        {isLoading ? (
          <div className="text-center text-gray-400 py-4">로딩 중...</div>
        ) : error ? (
          <div className="text-center text-red-500 py-4">{error}</div>
        ) : (
          <div className="space-y-4">
            {/* 친구 요청 목록 */}
            {friendRequests.length !== 0 ? (
              friendRequests.map(request => (
                <FriendRequestItem
                  key={request.friendRequestId}
                  request={request}
                  onRefresh={removeRequestFromList}
                />
              ))
            ) : (
              <div className="text-center text-gray-400 py-4">새로운 친구 요청이 없습니다.</div>
            )}
          </div>
        )}
      </div>
    </div>
  );
};

export default NotificationTab;