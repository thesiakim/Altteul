package com.c203.altteulbe.ranking.util;

import static com.c203.altteulbe.ranking.persistent.entity.QTier.*;
import static com.c203.altteulbe.user.persistent.entity.QUser.*;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.util.StringUtils;

/*
 * 동적 쿼리를 위한 조건 빌딩
 */
public class TodayRankingPredicate {
	public static BooleanExpression nicknameContains(String nickname) {
		return StringUtils.hasText(nickname) ? user.nickname.containsIgnoreCase(nickname) : null;
	}

	public static BooleanExpression tierEquals(Long tierId) {
		return (tierId != null) ? tier.id.eq(tierId.longValue()) : null;
	}

	public static BooleanExpression langEquals(String lang) {
		return StringUtils.hasText(lang) ? user.mainLang.stringValue().eq(lang) : null;
	}
}

