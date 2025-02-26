package com.c203.altteulbe.common.security.service;

import java.util.Map;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.c203.altteulbe.aws.util.S3Util;
import com.c203.altteulbe.common.dto.Language;
import com.c203.altteulbe.ranking.persistent.entity.Tier;
import com.c203.altteulbe.ranking.persistent.entity.TodayRanking;
import com.c203.altteulbe.ranking.persistent.repository.today_ranking.TodayRankingRepository;
import com.c203.altteulbe.user.persistent.entity.User;
import com.c203.altteulbe.user.persistent.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class Default0Auth2UserServiceImpl extends DefaultOAuth2UserService {
	private final UserRepository userRepository;
	private final TodayRankingRepository rankingRepository;

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) {
		OAuth2User oAuth2User = super.loadUser(userRequest);
		Map<String, Object> attributes = oAuth2User.getAttributes();

		// GitHub에서 사용자 정보 추출
		String username = attributes.get("id").toString();
		String nickname = attributes.get("login").toString();

		String profileImgKey = S3Util.getDefaultImgKey();   // 기본 이미지 objectKey 저장


		return userRepository.findByProviderAndUsername(User.Provider.GH, username)
			.orElseGet(() -> {
				// 신규 사용자 생성
				User user = User.builder()
					.username(username)
					.nickname(nickname)
					.provider(User.Provider.GH)
					.mainLang(Language.PY)
					.userStatus(User.UserStatus.A)
					.tier(new Tier(1L, "BRONZE", 0, 200))
					.profileImg(profileImgKey)
					.rankingPoint(0L)
					.build();

				TodayRanking todayRanking = TodayRanking.builder()
					.user(user)
					.tier(user.getTier())
					.rankingPoint(user.getRankingPoint())
					.rankingChange(0L)
					.ranking((int) rankingRepository.count()+1)
					.build();

				userRepository.save(user);
				rankingRepository.save(todayRanking);
				return user;
			});
	}
}