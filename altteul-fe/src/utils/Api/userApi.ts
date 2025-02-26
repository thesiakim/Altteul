import { api, userApi } from '@utils/Api/commonApi';
import { UserGameRecordResponse, UserInfoResponse, UserSearchResponse } from 'types/types';

export const getUserInfo = async (userId: string): Promise<UserInfoResponse> => {
  const response = await userApi.get(`${userId}`);
  return response.data;
};

// ??
export const getUserRecord = async (userId: string): Promise<UserGameRecordResponse> => {
  const response = await api.get(`game/history/${userId}`);
  return response.data;
};

// MainModal에서의 유저 검색 api
export const searchUsers = async (nickname: string) => {
  try {
    const res = await userApi.get('/search', {
      params: { nickname },
    });
    return res.data;
  } catch (error) {
    // console.log(error)
  }
};

//editUserProfile
export const updateProfile = async (formData: FormData) => {
  const response = await userApi.patch('', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  });

  return response;
};
