// src/components/Modal/FriendChat/Views/SearchResults.tsx
import { useEffect, useState } from 'react';
import { useInView } from 'react-intersection-observer';
import useFriendChatStore from '@stores/friendChatStore';
import { searchUsers } from '@utils/Api/userApi';
import SearchResultItem from '@components/Modal/FriendChat/Items/SearchResultItem';
import { getFriends } from '@utils/Api/friendChatApi';

interface User {
  userId: number;
  userid?: number;
  nickname: string;
  profileImg: string;
  isOnline: boolean;
}

const SearchResultsView = () => {
  const { ref, inView } = useInView({
    threshold: 0.1,
  });
  const fcStore = useFriendChatStore();
  const [searchUser, setSearchUser] = useState<User[]>([]);

  useEffect(() => {
    getSearchResult(fcStore.searchQuery);
    if (fcStore.searchQuery === '') {
      fcStore.setCurrentView('main');
    }
  }, [fcStore.searchQuery]);

  //검색 결과 목록 생성
  const getSearchResult = async (nickname: string) => {
    try {
      fcStore.setIsSearching(true);
      const userList = await searchUsers(nickname); //검색 결과 api
      const friendList = await getFriends({}); //친구 목록 조회 api
      const friendIds = friendList.data.friends.map((f: User) => f.userid);
      const others = userList.data.filter((user: User) => !friendIds.includes(user.userId));
      setSearchUser(others);
    } catch (error) {
      // console.log('검색결과 조회 중 error 발생: ', error);
    } finally {
      fcStore.setIsSearching(false);
    }
  };

  return (
    <div className="flex flex-col gap-4 p-4">
      {searchUser.map(user => (
        <SearchResultItem key={user.userId} user={user} />
      ))}

      <div ref={ref} className="h-4">
        {fcStore.isSearching && <div className="text-center text-gray-400 py-2">로딩 중...</div>}
      </div>

      {searchUser.length === 0 && (
        <div className="text-center text-gray-400 p-4">검색 결과가 없습니다.</div>
      )}
    </div>
  );
};

export default SearchResultsView;
