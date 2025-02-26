import { GameState } from 'types/types';
import { create } from 'zustand';

const useGameStore = create<GameState>(set => ({
  // 사용자가 새로고침한 후에도 게임 정보를 유지하기 위해 세션스토리지에 저장
  gameId: Number(sessionStorage.getItem('gameId')) || null,
  roomId: Number(sessionStorage.getItem('roomId')) || null,
  userRoomId: Number(sessionStorage.getItem('userRoomId')) || null,
  matchId: sessionStorage.getItem('matchId') || null,
  users: JSON.parse(sessionStorage.getItem('users') || null),
  myTeam: JSON.parse(sessionStorage.getItem('myTeam')) || null,
  opponent: JSON.parse(sessionStorage.getItem('opponent')) || null,
  problem: JSON.parse(sessionStorage.getItem('problem') || null),
  testcases: JSON.parse(sessionStorage.getItem('testcases') || null),
  isFinish: 'PENDING',

  setGameInfo: (gameId: number, roomId: number) => {
    sessionStorage.setItem('gameId', gameId.toString());
    sessionStorage.setItem('roomId', roomId.toString());
    set({ gameId, roomId });
  },

  setGameId: (gameId: number) => {
    sessionStorage.setItem('gameId', gameId.toString());
    set({ gameId });
  },
  setroomId: (roomId: number) => {
    sessionStorage.setItem('roomId', roomId.toString());
    set({ roomId });
  },

  setUserRoomId: (userRoomId: number) => {
    sessionStorage.setItem('userRoomId', userRoomId.toString());
    set({ userRoomId });
  },

  setMatchId: (matchId: string) => {
    sessionStorage.setItem('matchId', matchId);
    set({ matchId });
  },

  setUsers: users => {
    sessionStorage.setItem('users', JSON.stringify(users));
    set({ users });
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

  setProblem: problem => {
    sessionStorage.setItem('problem', JSON.stringify(problem));
    set({ problem });
  },

  setTestcases: testcases => {
    sessionStorage.setItem('testcases', JSON.stringify(testcases));
    set({ testcases });
  },

  setIsFinish: (state: 'WIN' | 'LOSE' | 'PENDING') => {
    set({ isFinish: state });
  },

  // 새로운 게임 시작 시 원래 게임 정보 초기화
  resetGameInfo: () => {
    sessionStorage.removeItem('gameId');
    sessionStorage.removeItem('roomId');
    sessionStorage.removeItem('userRoomId');
    sessionStorage.removeItem('matchId');
    sessionStorage.removeItem('users');
    sessionStorage.removeItem('problem');
    sessionStorage.removeItem('testcases');
    sessionStorage.removeItem('myTeam');

    set({
      gameId: null,
      roomId: null,
      userRoomId: null,
      matchId: null,
      users: [],
      problem: null,
      testcases: [],
      isFinish: 'PENDING',
      myTeam: null,
    });
  },
}));

export default useGameStore;
