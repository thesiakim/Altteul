package com.c203.altteulbe.game.service.exception;

import org.springframework.http.HttpStatus;

import com.c203.altteulbe.common.exception.BusinessException;

public class GameNotParticipatedException extends BusinessException  {
	public GameNotParticipatedException() {
		super("게임에 참여하지 않았습니다.", HttpStatus.BAD_REQUEST);
	}
}
