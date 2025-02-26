package com.c203.altteulbe.room.util;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MatchKeyParser {

	public static String[] getRoomIdPairs(String matchId) {
		Pattern pattern = Pattern.compile("(\\d+)-(\\d+)");
		Matcher matcher = pattern.matcher(matchId);

		if (matcher.matches()) {
			return new String[]{matcher.group(1), matcher.group(2)};
		}
		return null;
	}
}
