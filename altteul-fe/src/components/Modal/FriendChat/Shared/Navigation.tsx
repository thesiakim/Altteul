// src/components/Modal/Chat/shared/Navigation.tsx
import Friend_list from '@assets/icon/friend/Friend_list.svg';
import Chat_bubble from '@assets/icon/friend/Chat_bubble.svg';
import Notifications from '@assets/icon/friend/Notifications.svg';
import s_friend_list from '@assets/icon/friend/s_friend_list.svg';
import s_Chat_bubble from '@assets/icon/friend/s_Chat_bubble.svg';
import s_Notifications from '@assets/icon/friend/s_Notifications.svg';
import useFriendChatStore from '@stores/friendChatStore';

type MainTabType = 'friends' | 'chats' | 'notifications';

const Navigation = () => {
  const fcStore = useFriendChatStore();

  const changeView = (tab:MainTabType) => {
    fcStore.setCurrentTab(tab)
    fcStore.setCurrentView('main')
  }

  return (
    <div className="mt-4 flex justify-around border-t border-gray-700 pt-3">
      <button
        onClick={() => changeView('friends')}
        className="relative flex flex-col items-center p-1 hover:scale-110 rounded-lg"
      >
        <img
          src={fcStore.currentView ==='main' && fcStore.currentTab === 'friends' ? s_friend_list : Friend_list}
          alt="친구목록"
          className="w-10 h-10"
        />
      </button>

      <button
        onClick={() => changeView('chats')}
        className="relative flex flex-col items-center px-4 pt-1.5 hover:scale-110 rounded-lg"
      >
        <img
          src={fcStore.currentTab === 'chats' ? s_Chat_bubble : Chat_bubble}
          alt="채팅목록"
          className="w-9 h-9"
        />
      </button>

      <button
        onClick={() => changeView('notifications')}
        className="relative flex flex-col items-center px-4 py-2 hover:scale-110 rounded-lg"
      >
        <img
          src={fcStore.currentTab === 'notifications' ? s_Notifications : Notifications}
          alt="알림목록"
          className="w-9 h-9"
        />
      </button>
    </div>
  );
};

export default Navigation;