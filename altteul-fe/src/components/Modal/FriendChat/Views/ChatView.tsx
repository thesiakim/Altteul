// src/components/Modal/Chat/views/ChatView.tsx
import useAuthStore from '@stores/authStore';
import { useSocketStore } from '@stores/socketStore';
import useFriendChatStore from '@stores/friendChatStore';
import { getFriendChatMessages } from '@utils/Api/friendChatApi';
import { useEffect, useState, useRef } from 'react';
import { ChatMessage, ChatRoom } from 'types/types';
import socketResponseMessage from 'types/socketResponseMessage';
import Input from '@components/Common/Input';
import send from '@assets/icon/friend/Send.svg';

const ChatView = () => {
  const [chatRoom, setChatRoom] = useState<ChatRoom | null>(null);
  const [message, setMessage] = useState(''); //입력 메세지
  const [speechBubble, setSpeechBubble] = useState<ChatMessage[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const messageEndRef = useRef<HTMLDivElement>(null);
  const [chatRoomId, setChatRoomId] = useState();

  const { subscribe, unsubscribe, sendMessage } = useSocketStore();
  const { userId } = useAuthStore();
  // friendId
  const { activeChatId, setCurrentView } = useFriendChatStore();

  useEffect(() => {
    fetchMessages();
    if (chatRoomId) {
      subscribe(`/sub/chat/room/${chatRoomId}`, handleMessage);
    }
    return () => {
      unsubscribe(`/sub/chat/room/${chatRoomId}`);
    };
  }, [activeChatId, chatRoomId]);

  const fetchMessages = async () => {
    try {
      setIsLoading(true);
      const response = await getFriendChatMessages(activeChatId);
      // console.log(response);
      setChatRoom(response.data);
      setChatRoomId(response.data.chatroomId);
      setSpeechBubble(response.data.messages);
    } catch (error) {
      console.error('채팅방 로드 실패:', error);
      setError('채팅방을 불러오는데 실패했습니다.');
    } finally {
      setIsLoading(false);
    }
  };

  const handleMessage = (message: socketResponseMessage) => {
    // console.log(message);
    const { type, data } = message;
    if (type === '새 메시지') {
      const newChat = {
        chatMessageId: data.chatMessageId,
        senderId: data.senderId,
        senderNickname: data.senderNickname,
        messageContent: data.messageContent,
        checked: data.checked,
        createdAt: data.createdAt,
      } as ChatMessage;

      setSpeechBubble(prev => [...prev, newChat]);
    }
  };

  useEffect(() => {
    messageEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [speechBubble]);

  const handleSendMessage = () => {
    if (!message.trim() || !chatRoom || !activeChatId) return;
    sendMessage(`/pub/chat/room/${chatRoomId}/message`, {
      chatroomId: chatRoomId,
      senderId: userId,
      content: message.trim(),
    });
    setMessage('');
  };

  //엔터 눌렀을 때 이벤트
  const handleKeyPress = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'Enter') {
      handleSendMessage();
    }
  };

  if (isLoading) return <div className="flex-1 flex items-center justify-center">로딩 중...</div>;
  if (error)
    return <div className="flex-1 flex items-center justify-center text-red-500">{error}</div>;
  if (!chatRoom) return null;

  return (
    <div className="flex flex-col h-full">
      {/* 채팅방 헤더 */}
      <div className="border-b border-gray-700 px-4 pb-3 flex items-center gap-3 mt-0.5">
        <div className="relative">
          <img src={chatRoom.profileImg} alt="프로필" className="w-10 h-10 rounded-full" />
          <div
            className={`absolute top-0 right-0 w-3 h-3 rounded-full border-2 border-gray-800 ${
              chatRoom.isOnline ? 'bg-green-500' : 'bg-gray-400'
            }`}
          />
        </div>
        <div className="flex items-end pt-3">
          <div className="font-medium text-white">{chatRoom.nickname}</div>
          <div className="ml-1 pb-0.5 text-xs">님과 채팅중</div>
        </div>
      </div>

      {/* 메시지 목록 */}
      <div className="flex-1 overflow-y-auto p-4 space-y-4 content-end">
        {speechBubble?.map(message => (
          <div
            key={message.chatMessageId}
            className={`flex ${message.senderId === Number(userId) ? 'justify-end' : 'justify-start'} items-end`}
          >
            {/* 시간표시 */}
            {message.senderId === Number(userId) && (
              <p className="opacity-70 mr-2 text-xs" style={{ fontSize: '0.6rem' }}>
                {new Date(message.createdAt).toLocaleTimeString([], {
                  hour: '2-digit',
                  minute: '2-digit',
                })}
              </p>
            )}

            {/* 말풍선 */}
            <div
              className={`max-w-[70%] rounded-lg p-3 ${
                message.senderId === Number(userId)
                  ? 'bg-primary-orange text-white'
                  : 'bg-gray-700 text-white'
              }`}
            >
              <p>{message.messageContent}</p>
            </div>

            {/* 시간표시 */}
            {message.senderId !== Number(userId) && (
              <p className="opacity-70 ml-2 text-xs" style={{ fontSize: '0.6rem' }}>
                {new Date(message.createdAt).toLocaleTimeString([], {
                  hour: '2-digit',
                  minute: '2-digit',
                })}
              </p>
            )}
          </div>
        ))}
        <div ref={messageEndRef} />
      </div>

      {/* 메시지 입력 */}
      <div className="border-t border-gray-700 p-4">
        <div className="flex  relative w-full">
          <input
            name="chatMessage"
            type="text"
            value={message}
            onChange={e => setMessage(e.target.value)}
            onKeyDown={handleKeyPress}
            placeholder="메시지를 입력하세요"
            className="flex-1 bg-gray-700 rounded-lg px-4 py-2 text-white"
          />
          <button
            onClick={handleSendMessage}
            disabled={!message.trim()}
            className="absolute right-3 top-1/2 -translate-y-1/2 hover:opacity-80 transition-opacity disabled:opacity-50"
            aria-label="검색"
          >
            <img src={send} alt="검색" className="w-5 h-5" />
          </button>
        </div>
      </div>
    </div>
  );
};

export default ChatView;
