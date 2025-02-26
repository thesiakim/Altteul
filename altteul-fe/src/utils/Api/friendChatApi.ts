import { api, friendApi, teamApi } from '@utils/Api/commonApi';
import { chatApi } from '@utils/Api/commonApi';
import { FriendRequest } from 'types/types';

interface GetFriendsParams {
  page?: number;
  size?: number;
  query?: string; // 검색어 파라미터 추가
}

// 친구 목록 조회 (검색 기능 포함)
export const getFriends = async ({ page = 0, size = 10, query = '' }: GetFriendsParams = {}) => {
  try {
    const { data } = await api.get('friends', {
      params: {
        page,
        size,
        // 검색어가 있는 경우에만 query 파라미터 포함
        ...(query && { query }),
      },
    });
    return data;
  } catch (error) {
    console.error('Failed to fetch friends:', error);
    throw error;
  }
};

// 검색 전용 API가 별도로 필요한 경우를 위한 함수
export const searchFriends = async ({ query, page = 0, size = 10 }: GetFriendsParams) => {
  try {
    const { data } = await friendApi.get('/search', {
      params: { query, page, size },
    });
    return data;
  } catch (error) {
    console.error('Failed to search friends:', error);
    throw error;
  }
};

export const getFriendRequests = async ({ page = 0, size = 10 }: GetFriendsParams) => {
  try {
    const data = await friendApi.get('/request', {
      params: { page, size },
    });
    return data;
  } catch (error) {
    // console.log('error: ', error);
  }
};

// 채팅방 목록 조회
export const getChatRooms = async () => {
  try {
    const { data } = await chatApi.get('');
    return data;
  } catch (error) {
    console.error('채팅방 목록 조회 에러:', error);
    throw error;
  }
};

// 특정 친구와의 채팅방 조회
export const getFriendChatMessages = async (friendId: number) => {
  try {
    const { data } = await chatApi.get(`/friend/${friendId}`);
    return data;
  } catch (error) {
    console.error('채팅방 조회 에러:', error);
    throw error;
  }
};

//팀전 초대 api
export const inviteFriend = async (payload: { inviteeId: number; roomId: number }) => {
  try {
    const data = await teamApi.post('invite', payload);
    return data;
  } catch (e) {
    throw new Error();
  }
};

//친구 신청 api
export const requestFriend = async (toUserId: number) => {
  const res = await friendApi.post('request', { toUserId: toUserId });
  return res;
};

//친구 신청 수락/거절 api
export const friendRequestResponse = async (response: FriendRequest) => {
  await friendApi.post('request/process', response);
};

//친구 삭제 api
export const deleteFriend = async (userId: number, friendId: number) => {
  await friendApi.delete('delete', { data: { userId: userId, friendId: friendId } });
};
