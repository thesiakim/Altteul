export const MODAL_TYPES = {
  RESULT: 'game-result',
  LIST: 'single-result-list',
  NAVIGATE: 'game-navigate',
  COMMON: 'game-common',
  SIGNUP: 'signup',
  LOGIN: 'login',
  EDIT: 'edit-profile',
  MAIN: 'main',
  FRIEND: 'friend',
  CHAT: 'chat',
} as const;

export const GAME_TYPES = {
  SINGLE: 'single',
  TEAM: 'team',
} as const;

export const RESULT_TYPES = {
  SUCCESS: 'success',
  FAILURE: 'failure',
} as const;

export const COMMON_MODAL_TYPES = {
  CODE: 'code',
  COACHING: 'coaching',
} as const;

export type GameType = (typeof GAME_TYPES)[keyof typeof GAME_TYPES];
export type ResultType = (typeof RESULT_TYPES)[keyof typeof RESULT_TYPES];
export type CommonModalType = (typeof COMMON_MODAL_TYPES)[keyof typeof COMMON_MODAL_TYPES];

// 모달 정보 타입 정의
export interface ModalInfo {
  type?: GameType;
  result?: ResultType;
  modalType?: CommonModalType;
  nickname?: string;
  profileImg?: string;
}
