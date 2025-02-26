// src/components/Modal/Chat/tabs/ChatTab.tsx
import { useState, useEffect } from 'react';
import type { ChatRoom } from 'types/types';
import { getChatRooms } from '@utils/Api/friendChatApi';
import ChatItem from '@components/Modal/FriendChat/Items/ChatItem';
import useFriendChatStore from '@stores/friendChatStore';

const ChatTab = () => {
  const [chatRooms, setChatRooms] = useState<ChatRoom[]>([]);
  const [currentPage, setCurrentPage] = useState(0);
  const [isLast, setIsLast] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const { searchQuery, startChat } = useFriendChatStore();

  const fetchChatRooms = async () => {
    if (isLoading || isLast) return;

    try {
      setIsLoading(true);
      const response = await getChatRooms();
      // console.log(response);
      if (response.status === 200) {
        setChatRooms(prev => (currentPage === 0 ? response.data : [...prev, ...response.data]));
      }
    } catch (error) {
      console.error('채팅방 목록 조회 실패:', error);
      setError('채팅방 목록을 불러오는데 실패했습니다.');
    } finally {
      setIsLoading(false);
    }
  };

  // 초기 로딩 및 페이지 변경 시
  useEffect(() => {
    fetchChatRooms();
  }, [currentPage]);

  if (error) {
    return <div className="text-center text-red-500 p-4">{error}</div>;
  }

  return (
    <div className="flex flex-col gap-4 px-4">
      {/* 알림 타입 선택 탭 */}
      <div className="flex border-b border-gray-700 mt-5">
        <div className="flex-1 pb-3 text-center transition-colors text-primary-orange border-b-2 border-primary-orange">
          이전 대화 목록
        </div>
      </div>
      {chatRooms?.length > 0 ? (
        <>
          {chatRooms.map(room => (
            <ChatItem
              key={room.chatRoomId}
              room={room}
              onRefresh={fetchChatRooms}
              onSelectChat={() => startChat(room.friendId)}
            />
          ))}

          {/* {!isLast && (
            <button
              onClick={() => setCurrentPage(prev => prev + 1)}
              className="w-full py-2 text-gray-400 hover:text-white transition-colors"
              disabled={isLoading}
            >
              {isLoading ? '로딩 중...' : ''}
            </button>
          )} */}
        </>
      ) : (
        <div className="text-center text-gray-400 p-4">'채팅방이 없습니다.'</div>
      )}
    </div>
  );
};

export default ChatTab;
