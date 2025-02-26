// src/stores/matchStore.ts
import { create } from 'zustand';
import { MatchState } from 'types/types';

export const useMatchStore = create<MatchState>(set => ({
  matchData: JSON.parse(sessionStorage.getItem('matchData')) || null,
  myTeam: JSON.parse(sessionStorage.getItem('myTeam')) || null,
  opponent: JSON.parse(sessionStorage.getItem('opponent')) || null,
  matchId: sessionStorage.getItem('matchId') || '',
  isLoading: false,

  setMatchData: data => {
    sessionStorage.setItem('matchData', JSON.stringify(data));
    set({
      matchData: data,
    });
  },

  setMyTeam: data => {
    sessionStorage.setItem('myTeam', JSON.stringify(data));
    set({
      myTeam: data,
    });
  },

  setOpponent: data => {
    sessionStorage.setItem('opponent', JSON.stringify(data));
    set({
      opponent: data,
    });
  },

  setMathId: (matchId: string) => {
    sessionStorage.setItem('matchId', matchId);
    set({ matchId });
  },

  clear: () => {
    sessionStorage.removeItem('matchData');
    sessionStorage.removeItem('alliance');
    sessionStorage.removeItem('opponent');
    sessionStorage.removeItem('matchId');
    set({
      matchData: null,
    });
  },

  setLoading: loading => set({ isLoading: loading }),
}));
