package com.c203.altteulbe.aws.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.c203.altteulbe.config.AWSConfig;

@Component
public class S3Util {

	private static String s3BaseUrl;

	@Autowired
	public S3Util(AWSConfig awsConfig) {
		S3Util.s3BaseUrl = awsConfig.getS3BaseUrl(); // Base URL을 정적으로 저장
	}

	// 기본 이미지 object key 반환
	public static String getDefaultImgKey() {
		return "uploads/1739326628591_People.svg";
	}

	// 이미지 출력을 위해 전체 주소 반환
	public static String getImgUrl(String key) {
		return s3BaseUrl + key;
	}
}
