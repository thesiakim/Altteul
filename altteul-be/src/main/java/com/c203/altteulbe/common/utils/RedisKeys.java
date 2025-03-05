package com.c203.altteulbe.common.utils;

public class RedisKeys {

	// 친구 관계
	private static final String FRIEND_RELATION_CACHE = "friendRelation";

	// 친구 요청
	private static final String FRIEND_REQUEST_CACHE = "friendRequests";

	// 친구 리스트
	private static final String FRIEND_LIST_CACHE = "friendList";

	// 유저 상태
	private static final String USER_STATUS = "isOnline";

	// 팀전 매칭 진행 중인 방 목록
	public static final String TEAM_MATCHING_ROOMS = "room:team:matching_rooms";

	// 팀전 대기 중인 방 목록
	public static final String TEAM_WAITING_ROOMS = "room:team:waiting_rooms";

	// 개인전 대기 중인 방 목록
	public static final String SINGLE_WAITING_ROOMS = "room:single:waiting_rooms";

	public static final String ROOM_DB_ID = "room:db_id";

	public static final String ROOM_REDIS_ID = "room:redis_id";

	public static String getRoomDbId(Long roomId) {
		return ROOM_DB_ID + ":" + roomId;
	}

	// 친구 요청 키
	public static String geFriendRequestKey(Long userId) {
		return FRIEND_REQUEST_CACHE + ":" + userId;
	}

	// 친구 관계 키
	public static String getFriendRelationKey(Long userId) {
		return FRIEND_RELATION_CACHE + ":" + userId;
	}

	// 유저 상태 키
	public static String getUserStatusKey(Long userId) {
		return USER_STATUS + ":" + userId;
	}

	// 친구 리스트 키
	public static String getFriendListKey(Long userId) {
		return FRIEND_LIST_CACHE + ":" + userId;
	}

	// 게임 관련 키 추가
	public static String TeamMatchId(Long roomId) {
		return "room:team:" + roomId + ":match";
	}

	// 개인전 방 상태
	public static String SingleRoomStatus(Long roomId) {
		return "room:single:" + roomId + ":status";
	}

	// 개인전 방에 속한 유저 목록
	public static String SingleRoomUsers(Long roomId) {
		return "room:single:" + roomId + ":users";
	}

	// 특정 유저가 속한 개인전 방 정보
	public static String userSingleRoom(Long userId) {
		return "user:" + userId + ":single_room";
	}

	// 개인전 방 카운팅 관리
	public static String SingleRoomCountdown(Long roomId) {
		return "room:single:" + roomId + ":countdown";
	}

	// 팀전 방 상태
	public static String TeamRoomStatus(Long roomId) {
		return "room:team:" + roomId + ":status";
	}

	// 팀전 방에 속한 유저 목록
	public static String TeamRoomUsers(Long roomId) {
		return "room:team:" + roomId + ":users";
	}

	// 특정 유저가 속한 팀전 방 정보
	public static String userTeamRoom(Long userId) {
		return "user:" + userId + ":team_room";
	}

	// 팀전 방 카운팅 관리
	public static String TeamRoomCountdown(String roomId) {
		return "room:team:" + roomId + ":countdown";
	}

	// 특정 matchId를 가진 팀들이 풀어야 하는 문제 pk 관리
	public static String TeamRoomProblem(String matchId) {
		return "room:team:" + matchId + ":problem";
	}

	// 초대 정보 저장
	public static String inviteInfo(String roomId, String friendId) {
		return "invite:room:" + roomId + ":user:" + friendId;
	}

	public static String getRoomRedisId(Long roomId) {
		return ROOM_REDIS_ID + ":" + roomId;
	}

	// 연결 해제 대기 상태 키
	public static String getPendingDisconnectKey(Long userId) {
		return "user:" + userId + ":pending_disconnect";
	}
}
