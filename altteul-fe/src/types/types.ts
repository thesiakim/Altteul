import { CompatClient } from '@stomp/stompjs';

export interface User {
  roomId?: number;
  userId: number;
  nickname: string;
  profileImg: string;
  tierId: number;
}

export type Users = User[];

export interface TestCase {
  testcaseId: number;
  number: number;
  input: string;
  output: string;
}

export interface Problem {
  problemId: number;
  problemTitle: string;
  description: string;
}

export interface GameState {
  gameId: number | null;
  roomId: number | null;
  userRoomId: number | null;
  matchId: string | null;
  users: User[];
  myTeam: MatchData;
  opponent: MatchData;
  problem: Problem | null;
  testcases: TestCase[];
  isFinish?: 'WIN' | 'LOSE' | 'PENDING';

  setGameInfo: (gameId: number, roomId: number) => void;
  setGameId: (gameId: number) => void;
  setroomId: (roomId: number) => void;
  setUserRoomId: (userRoomId: number) => void;
  setMatchId: (matchId: string) => void;
  setUsers: (users: User[]) => void;
  setMyTeam: (data: MatchData) => void;
  setOpponent: (data: MatchData) => void;
  setProblem: (problem: Problem) => void;
  setTestcases: (testcases: TestCase[]) => void;
  setIsFinish?: (isFinish: 'WIN' | 'LOSE' | 'PENDING') => void;
  resetGameInfo: () => void;
}

export interface MatchState {
  matchData: MatchData;
  isLoading: boolean;
  myTeam: MatchData;
  opponent: MatchData;
  matchId: string;

  setMatchData: (data: MatchData) => void;
  setMyTeam: (data: MatchData) => void;
  setOpponent: (data: MatchData) => void;
  setMathId: (matchId: string) => void;
  clear: () => void;
  setLoading: (loading: boolean) => void;
}

type Language = 'python' | 'java';

export interface CodeExecutionState {
  code: string;
  language: Language;
  output: string[];
  setCode: (code: string) => void;
  setLanguage: (language: Language) => void;
  executeCode: () => void;
  clearOutput: () => void;
}

export interface UserInfoResponse {
  status: number;
  message: string;
  data: UserInfo;
}

export interface UserInfo {
  userId: number;
  username: string;
  nickname: string;
  profileImg: string;
  tierName: string;
  tierId: number;
  rankPercentile: number;
  rank: number;
  rankChange: number;
  isOwner: boolean;
}
export interface MatchData {
  gameId?: number;
  roomId?: number;
  leaderId?: number;
  users?: User[];
  remainingUsers?: User[];
  problem?: Problem;
  testcases?: TestCase[];
}

export interface SingleEnterApiResponse {
  data: MatchData;
  message: string;
  status: string;
}

export interface RankingResponse {
  status: number;
  message: string;
  data: {
    curentPage: number;
    totalPages: number;
    totalElements: number;
    last: boolean;
    rankings: Ranking[];
  };
}

export interface Ranking {
  userId?: number;
  ranking: number;
  nickname: string;
  lang: string;
  point: number;
  tierId: number;
  rankChange: number;
  rate: number | null;
}

export interface RankApiFilter {
  page: number | null;
  size: number | null;
  lang: string | null;
  tierId: number | null;
  nickname: string | null;
}

export interface UserGameRecordResponse {
  status: number;
  message: string;
  data: {
    games: UserGameRecord[];
    last: boolean;
    totalPages: number;
    currentPage: number;
    totalElements: number;
  };
}

export interface UserGameRecord {
  problem: Problem;
  gameType: string;
  startedAt: string;
  totalHeadCount: number;
  items: Item[];
  myTeam: TeamInfo;
  opponents: TeamInfo[];
}

export interface Item {
  itemId: number;
  itemName: string;
}

export interface TeamInfo {
  gameResult: string | number;
  lang: string;
  totalHeadCount: number;
  executeTime: number | null;
  executeMemory: number | null;
  point?: number | null;
  bonusPoint?: number | null; // TODO: point와 bonusPoint 합칠 것
  passRate?: number;
  duration: string | null;
  code: string | null;
  createdAt?: string;
  members: MemberInfo[];
}

export interface MemberInfo {
  userId: number;
  nickname: string;
  profileImage: string;
  rank?: number;
  tierId: number;
}

export interface ResultData {
  gameType: string;
  restTeam: TeamInfo[];
  submittedTeam: TeamInfo;
  status: number;
  message: string;
}

export interface CodeInfo {
  executeMemory: number;
  executeTime: number;
  code: string;
}

export type UserSearchContextType = {
  searchQuery: string;
  searchResults: Friend[];
  handleSearch: (query: string) => void;
  resetSearch: () => void; // 검색 초기화
};

export type Friend = {
  userid: number;
  nickname: string;
  profileImg: string;
  isOnline: boolean;
};

export type FriendRequest = {
  friendRequestId: number;
  fromUserId: number;
  toUserId: number;
  requestStatus: 'P' | 'A' | 'R';
  fromUserProfileImg?: string;
  fromUserNickname?: string;
};

// 삭제할것
export type ChatRooms = {
  friendId: number;
  nickname: string;
  profileImg: string;
  isOnline: boolean;
  recentMessage: string;
  isMessageRead: boolean;
  createdAt: string;
};

export type ChatRoom = {
  chatRoomId: number;
  friendId: number;
  nickname: string;
  profileImg: string;
  isOnline: boolean;
  recentMessage: string;
  isMessageRead: boolean;
  messages?: ChatMessage[];
  createdAt: string;
};

export type ChatRoomResponse = {
  data: ChatRoom;
  message: string;
  status: number;
};

export type ChatMessage = {
  chatMessageId: number;
  senderId: number;
  senderNickname: string;
  messageContent: string;
  checked: boolean;
  createdAt: string;
};

export type Notification = {
  id: number;
  type: 'gameInvite' | 'friendRequest';
  from: {
    id: number;
    nickname: string;
    profileImg: string;
  };
  createdAt: string;
};

export interface NotificationUser {
  id: number;
  nickname: string;
  profileImg: string;
  isOnline?: boolean;
}

export interface NotificationItem {
  id: number;
  type: 'friendRequest' | 'gameInvite';
  from: NotificationUser;
  roomId?: number; // gameInvite일 때만 존재
  createdAt: string;
}

export interface FriendsResponse {
  status: number;
  message: string;
  data: {
    isLast: boolean;
    totalPages: number;
    currentPage: number;
    totalElements: number;
    friends: Friend[];
  };
}

export interface ChatRoomsResponse {
  status: number;
  message: string;
  data: ChatRoom[];
}

export interface ChatRoomDetail {
  friendId: number;
  nickname: string;
  profileImg: string;
  isOnline: boolean;
  messages: ChatMessage[];
  createdAt: string;
}

export interface ChatRoomDetailResponse {
  data: {
    friendId: number;
    nickname: string;
    profileImg: string;
    isOnline: boolean;
    messages: {
      chatMessageId: number;
      senderId: number;
      senderNickname: string;
      messageContent: string;
      checked: boolean;
      createdAt: string;
    }[];
    createdAt: string;
  };
  message: string;
  status: number;
}

export interface FriendRequestsResponse {
  data: {
    friendRequests: FriendRequest[];
    isLast: boolean;
    totalPages: number;
    currentPage: number;
    totalElements: number;
  };
  message: string;
  status: string;
}

export interface SearchedUser {
  userId: number;
  nickname: string;
  profileImage: string;
  isOnline: boolean;
}

export interface UserSearchResponse {
  status: number;
  message: string;
  data: {
    userId: number;
    nickname: string;
    profileImg: string;
    isOnline: boolean;
  };
}

export interface Subscription {
  id: string;
  callback: (data: any) => void;
}

export interface SocketStore {
  // 상태
  client: CompatClient | null;
  connected: boolean;
  subscriptions: Map<string, Subscription>;
  reconnectAttempts: number;
  maxReconnectAttempts: number;

  // 메서드
  connect: () => void;
  disconnect: () => void;
  resetConnection: () => void;
  subscribe: (destination: string, callback: (data: any) => void) => void;
  unsubscribe: (destination: string) => void;
  sendMessage: (destination: string, message: any) => void;
  restoreSubscriptions: () => void;
  unsubscribeAll: () => void;
}
