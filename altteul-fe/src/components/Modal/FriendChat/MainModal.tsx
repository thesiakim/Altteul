import { useEffect } from 'react';
import BaseModal from '@components/Modal/FriendChat/Shared/Basemodal';
import Navigation from '@components/Modal/FriendChat/Shared/Navigation';
import MainView from '@components/Modal/FriendChat/Views/MainView.tsx';
import ChatView from '@components/Modal/FriendChat/Views/ChatView';
import SearchBar from '@components/Modal/FriendChat/Shared/SearchBar';
import ModalHeader from './Shared/Header';
import useFriendChatStore from '@stores/friendChatStore';
import SearchResultsView from './Views/SearchResultsView';

interface FriendModalProps {
  isOpen: boolean;
  onClose: () => void;
}

const FriendModal = ({ isOpen, onClose }: FriendModalProps) => {
  const fcStore = useFriendChatStore();

  // 모달이 닫힐 때 상태 초기화
  useEffect(() => {
    if (!isOpen) {
      fcStore.resetStore();
    }
  }, [isOpen]);

  return (
    <BaseModal isOpen={isOpen} onClose={onClose}>
      <ModalHeader
        showBackButton={fcStore.currentView === 'chat'}
        onClose={onClose}
        onBack={() => fcStore.setCurrentView('main')}
      />

      <div className="flex flex-col h-full">
        {/* Header: 검색바 (친구탭에서만 표시) */}
        {fcStore.currentView !== 'chat' && fcStore.currentTab === 'friends' && (
          <div className="mt-2">
            <SearchBar />
          </div>
        )}

        {/* 중간 컨텐츠 */}
        <div className="flex-1 overflow-y-auto">
          {fcStore.currentView === 'main' ? (
            <MainView />
          ) : fcStore.currentView === 'search' ? (
            <SearchResultsView />
          ) : (
            <ChatView />
          )}
        </div>

        {/* tab 목록 */}
        {fcStore.currentView !== 'chat' && <Navigation />}
      </div>
    </BaseModal>
  );
};

export default FriendModal;
