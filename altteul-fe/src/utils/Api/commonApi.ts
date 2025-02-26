// src/config/api.ts
import axios from 'axios';

const BASE_URL = import.meta.env.MODE === 'production'
  ? import.meta.env.VITE_API_URL_PROD
  : import.meta.env.VITE_API_URL_DEV;
  
const BASE_PATH = import.meta.env.VITE_API_BASE_PATH;

// 기본 API 설정
const createApiInstance = (additionalPath = '') => {
  const instance = axios.create({
    baseURL: `${BASE_URL}${BASE_PATH}${additionalPath}`,
    withCredentials: true,
  });

  // 토큰이 필요한 요청에 대한 인터셉터
  instance.interceptors.request.use((config) => {
    const token = sessionStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  });

  return instance;
};

// API 인스턴스들
export const api = createApiInstance(); // 기본 api 인스턴스
export const authApi = api; // auth는 기본 api 인스턴스 사용
export const sigleApi = createApiInstance('single');
export const teamApi = createApiInstance('team');
export const rankApi = createApiInstance('ranking');
export const userApi = createApiInstance('user')
export const friendApi = createApiInstance('friend')
export const chatApi = createApiInstance('chatroom')