import { ChatRoom } from "types/types";

// src/components/Modal/Chat/Items/ChatItem.tsx 
  interface ChatItemProps {
    room: ChatRoom;
    onRefresh: () => void;
    onSelectChat: () => void;
  }
  
  const ChatItem = ({ room, onRefresh, onSelectChat }: ChatItemProps) => {
    // 마지막 메시지 시간을 상대적 시간으로 변환
    const getRelativeTime = (dateString: string) => {
      const now = new Date();
      const messageDate = new Date(dateString);
      const diffInMinutes = Math.floor((now.getTime() - messageDate.getTime()) / (1000 * 60));
      
      if (diffInMinutes < 1) return '방금 전';
      if (diffInMinutes < 60) return `${diffInMinutes}분 전`;
      
      const diffInHours = Math.floor(diffInMinutes / 60);
      if (diffInHours < 24) return `${diffInHours}시간 전`;
      
      const diffInDays = Math.floor(diffInHours / 24);
      if (diffInDays < 7) return `${diffInDays}일 전`;
      
      // 날짜 직접 표시
      return messageDate.toLocaleDateString('ko-KR', {
        month: 'short',
        day: 'numeric',
      });
    };
  
    return (
      <div 
        onClick={onSelectChat}
        className="flex items-center justify-between bg-gray-04 p-3 rounded-lg cursor-pointer hover:bg-gray-03 transition-colors"
      >
        {/* 왼쪽: 프로필 이미지와 채팅 정보 */}
        <div className="flex items-center gap-3 flex-1 ml-1">
          {/* 프로필 이미지 & 온라인 상태 */}
          <div className="relative">
            <img 
              src={room.profileImg} 
              alt={`${room.nickname}의 프로필`} 
              className="w-12 h-12 rounded-full"
            />
            <div
              className={`absolute top-0 right-0 w-3 h-3 rounded-full border-2 border-gray-800 
                ${room.isOnline ? 'bg-green-500' : 'bg-gray-400'}`}
            />
          </div>
  
          {/* 채팅방 정보 */}
          <div className="flex-1 min-w-0">
            {/* 상단: 닉네임과 시간 */}
            <div className="flex justify-between items-center mb-1">
              <span className="font-semibold text-primary-white">{room.nickname}</span>
              <span className="text-xs text-gray-400">
                {getRelativeTime(room.createdAt)}
              </span>
            </div>
            
            {/* 하단: 마지막 메시지와 안 읽은 메시지 수 */}
            <div className="flex justify-between items-center">
              <p className="text-sm text-gray-500 truncate pr-4">
                {room.recentMessage}
              </p>
              {/* 미확인 메세지 개수 */}
              {/* {room.unreadCount > 0 && (
                <span className="bg-primary-orange text-white text-xs px-2 py-1 rounded-full min-w-[1.5rem] text-center">
                  {room.unreadCount}
                </span>
              )} */}
            </div>
          </div>
        </div>
      </div>
    );
  };
  
  export default ChatItem;