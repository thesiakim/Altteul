package com.c203.altteulbe.ranking.batch;

import java.util.Iterator;
import java.util.List;

import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

import com.c203.altteulbe.user.persistent.entity.User;
import com.c203.altteulbe.user.persistent.repository.UserRepository;

import lombok.RequiredArgsConstructor;

/*
 * 유저의 최신 랭킹 정보를 가져오는 ItemReader
 */
@Component
@RequiredArgsConstructor
public class RankingHistoryItemReader implements ItemReader<User> {

	private final UserRepository userRepository;
	private Iterator<User> userIterator;

	@Override
	public User read() {
		if (userIterator == null) {
			List<User> users = userRepository.findAll();
			userIterator = users.iterator();
		}
		return userIterator.hasNext() ? userIterator.next() : null;
	}
}
