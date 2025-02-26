package com.c203.altteulbe.room.web.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InviteTeamAnswerRequestDto {
	private String nickname;
	private Long roomId;        // websocket으로 전송한 roomId
	private boolean accepted;   // 초대 수락 여부 (true: 수락, false: 거절)
}
