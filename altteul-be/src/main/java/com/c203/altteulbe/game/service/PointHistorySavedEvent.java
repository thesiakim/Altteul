package com.c203.altteulbe.game.service;

import com.c203.altteulbe.game.persistent.entity.PointHistory;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/*
 * PointHistory가 저장될 때 발생하는 이벤트 객체
 */
@Getter
@RequiredArgsConstructor
public class PointHistorySavedEvent {
	private final PointHistory pointHistory;
}