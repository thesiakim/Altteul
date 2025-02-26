package com.c203.altteulbe.user.persistent.repository;

import java.util.List;
import java.util.Optional;

import com.c203.altteulbe.user.persistent.entity.User;

public interface UserCustomRepository {
	boolean existsByUsername(String username);

	boolean existsByNickname(String nickname);

	Optional<User> findByUsername(String username);

	Optional<User> findByProviderAndUsername(User.Provider provider, String username);

	Optional<User> findWithRankingByUserId(Long userId);

	List<User> findAllOrderedByRankingPointTierUsername();

	long countUsers();

	List<User> searchByNickname(String nickname);

}
