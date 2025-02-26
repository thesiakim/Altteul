import { authApi } from '@utils/Api/commonApi';
import axios from 'axios';

// 회원가입 API 요청
export const registerUser = async (formData: FormData) => {
  try {
    // console.log('입력 데이터:', formData);

    const response = await authApi.post('register', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });

    // console.log('응답 데이터:', response);

    if (response.status >= 200 && response.status < 300) {
      // console.log('회원가입 성공');
      return {
        status: response.status,
        message: response.data.message || '회원가입 성공',
      };
    } else {
      throw new Error(response.data.message || '잘못된 응답');
    }
  } catch (error: unknown) {
    console.error('회원가입 API 요청 실패 : ', error);
  }
};

// 로그인 API 요청
export const loginUser = async (username: string, password: string) => {
  try {
    const response = await authApi.post('login', { username, password });

    if (response.status >= 200 && response.status < 300) {
      return response;
    } else {
      throw new Error(response.data.message || '잘못된 응답');
    }
  } catch (error) {
    // console.log('error: ', error);
  }
};

// 아이디 중복확인 요청
export const checkUsername = async (username: string) => {
  try {
    const response = await authApi.get(`id-check?username=${username}`);
    return response.data;
  } catch (error) {
    // console.log('error: ', error);
  }
};

// 닉네임 중복확인 요청
export const checkNickname = async (nickname: string) => {
  try {
    const response = await authApi.get(`nickname-check?nickname=${nickname}`);
    return response.data;
  } catch (error) {
    // console.log('error: ', error);
  }
};
