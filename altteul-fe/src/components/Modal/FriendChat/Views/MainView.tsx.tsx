import FriendTab from '@components/Modal/FriendChat/Tabs/FriendTab';
import ChatTab from '@components/Modal/FriendChat/Tabs/ChatTab';
import NotificationTab from '@components/Modal/FriendChat/Tabs/NotificationTab';
import useFriendChatStore from '@stores/friendChatStore';
import SearchResults from './SearchResultsView';

const MainView = () => {
  const { currentTab } = useFriendChatStore();

  const renderTabContent = () => {
    switch (currentTab) {
      case 'friends':
        return <FriendTab />;
      case 'chats':
        return <ChatTab />;
      case 'notifications':
        return <NotificationTab />;
      default:
        return null;
    }
  };

  return <div className="flex-1 overflow-y-auto">{renderTabContent()}</div>;
};

export default MainView;
