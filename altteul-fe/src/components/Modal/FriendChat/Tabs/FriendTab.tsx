// src/components/Modal/Chat/tabs/FriendTab.tsx
import { useState, useEffect } from 'react';
import { useInView } from 'react-intersection-observer';
import type { Friend } from 'types/types';
import { getFriends } from '@utils/Api/friendChatApi';
import FriendItem from '@components/Modal/FriendChat/Items/FriendItem';
import useFriendChatStore from '@stores/friendChatStore';

const FriendTab = () => {
  const [friends, setFriends] = useState<Friend[]>([]);
  const [currentPage, setCurrentPage] = useState(0);

  const fcStore = useFriendChatStore();
  const { friendsNeedRefresh, resetFriendsRefresh } = fcStore;

  const { ref, inView } = useInView({
    threshold: 0.1,
  });

  const getFriendsList = async () => {
    if (!fcStore.hasMore) return;

    try {
      fcStore.setIsSearching(true);
      const response = await getFriends();
      // console.log(response)
      setFriends(prev =>
        currentPage === 0 ? response.data.friends : [...prev, ...response.data.friends]
      );

      fcStore.setHasMore(response.data.friends.length > 0);
      fcStore.setSearchError(null);
    } catch (error) {
      console.error('친구 목록 조회 실패:', error);
      fcStore.setSearchError('친구 목록을 불러오는데 실패했습니다.');
    } finally {
      fcStore.setIsSearching(false);
    }
  };

  // 새로고침 트리거가 활성화되면 친구 목록을 다시 가져옵니다
  useEffect(() => {
    if (friendsNeedRefresh) {
      getFriendsList();
      resetFriendsRefresh();
    }
  }, [friendsNeedRefresh]);

  useEffect(() => {
    if (inView && !fcStore.isSearching && fcStore.hasMore && !fcStore.searchQuery) {
      setCurrentPage(prev => prev + 1);
    }
  }, [inView]);

  useEffect(() => {
    if (fcStore.currentView === 'main' && fcStore.currentTab === 'friends') {
      getFriendsList();
    }
  }, [fcStore.currentView, fcStore.currentTab]);

  if (fcStore.searchError) {
    return <div className="text-center text-red-500 p-4">{fcStore.searchError}</div>;
  }

  const updateFrinedList = (friendId: number) => {
    setFriends(prev => prev.filter(friend => friend.userid !== friendId));
  };

  return (
    <div className="flex flex-col gap-4 p-4">
      {friends.map(friend => (
        <FriendItem key={friend.userid} friend={friend} onRefresh={updateFrinedList} />
      ))}

      <div ref={ref} className="h-4">
        {fcStore.isSearching && <div className="text-center text-gray-400 py-2">로딩 중...</div>}
      </div>

      {!fcStore.hasMore && friends.length === 0 && (
        <div className="text-center text-gray-400 p-4">친구 목록이 비어있습니다.</div>
      )}
    </div>
  );
};

export default FriendTab;
