import { create } from 'zustand';

interface AuthState {
  token: string;
  userId: number;
  setToken: (newToken: string) => void;
  setUserId: (newUserId: string) => void;
  logout: () => void;
}

const useAuthStore = create<AuthState>(set => ({
  token: sessionStorage.getItem('token') || '',
  userId: Number(sessionStorage.getItem('userId')) || 0,

  setToken: (newToken: string) => {
    sessionStorage.setItem('token', newToken); //로컬에 저장
    set({ token: newToken }); // zustand에 저장
  },

  setUserId: (newUserId: string) => {
    sessionStorage.setItem('userId', newUserId);
    set({ userId: Number(newUserId) });
  },

  logout: () => {
    sessionStorage.clear();
    set({ token: '', userId: null });
  },
}));

export default useAuthStore;
