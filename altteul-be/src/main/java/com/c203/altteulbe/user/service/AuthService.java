package com.c203.altteulbe.user.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.c203.altteulbe.aws.service.S3Service;
import com.c203.altteulbe.aws.util.S3Util;
import com.c203.altteulbe.ranking.persistent.entity.Tier;
import com.c203.altteulbe.ranking.persistent.entity.TodayRanking;
import com.c203.altteulbe.ranking.persistent.repository.today_ranking.TodayRankingRepository;
import com.c203.altteulbe.user.persistent.entity.User;
import com.c203.altteulbe.user.persistent.repository.UserRepository;
import com.c203.altteulbe.user.service.exception.DuplicateNicknameException;
import com.c203.altteulbe.user.service.exception.DuplicateUsernameException;
import com.c203.altteulbe.user.web.dto.request.RegisterUserRequestDto;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final TodayRankingRepository rankingRepository;
	private final S3Service s3Service;
	private String defaultProfileImgKey;

	@PostConstruct
	private void init() {
		this.defaultProfileImgKey = S3Util.getDefaultImgKey();
	}

	public void registerUser(RegisterUserRequestDto request, MultipartFile image) {

		//일치하는 아이디, 닉네임이 존재하는가?
		validateId(request.getUsername());
		validateNickname(request.getNickname(), null);

		String profileImgKey = (image == null || image.isEmpty())
								? defaultProfileImgKey                            // 기본 이미지 objectKey 저장
								: s3Service.uploadFiles(image, "uploads/"); // S3에 업로드 후 objectKey 저장

		User user = User.builder()
			.username(request.getUsername())
			.password(request.getPassword())
			.nickname(request.getNickname())
			.mainLang(request.getMainLang())
			.profileImg(profileImgKey)        // objectKey 저장
			.rankingPoint(0L)
			.provider(User.Provider.LC)
			.userStatus(User.UserStatus.A)
			.tier(new Tier(1L, "BRONZE", 0, 200))
			.build();

		TodayRanking todayRanking = TodayRanking.builder()
			.user(user)
			.tier(user.getTier())
			.rankingPoint(user.getRankingPoint())
			.rankingChange(0L)
			.ranking((int) rankingRepository.count()+1)
			.build();

		user.hashPassword(passwordEncoder);
		userRepository.save(user);
		rankingRepository.save(todayRanking);
	}

	public void validateId(String username) {
		if (userRepository.existsByUsername(username)) {
			throw new DuplicateUsernameException();
		}
	}

	public void validateNickname(String nickname, Long userId) {
		if (userId == null) {
			if (userRepository.existsByNickname(nickname)) {
				throw new DuplicateNicknameException();
			}
		} else {
			userRepository.findByNickname(nickname)
				.ifPresent(user -> {
					if (!user.getUserId().equals(userId)) {
						throw new DuplicateNicknameException();
					}
				});
		}
	}
}
