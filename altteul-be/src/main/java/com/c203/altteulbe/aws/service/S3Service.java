package com.c203.altteulbe.aws.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {
	private final S3Client s3Client;
	private final Region awsRegion;

	@Value("${cloud.aws.s3.bucket}")
	private String bucketName;

	/**
	 * S3에 파일 업로드
	 *
	 * @param file MultipartFile 객체
	 * @param folder 업로드할 폴더 경로 (예: "uploads/")
	 * @return 업로드된 파일의 S3 key
	 */
	public String uploadFiles(MultipartFile file, String folder) {
		String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
		String objectKey = folder + fileName;

		try {
			// S3에 파일 업로드
			s3Client.putObject(PutObjectRequest.builder()
					.bucket(bucketName)
					.key(objectKey)
					.contentType(file.getContentType())
					.build(),
				RequestBody.fromBytes(file.getBytes()));

			log.info("파일 업로드 성공: {}", objectKey);
			return objectKey;
		} catch (IOException e) {
			log.error("파일 업로드 실패: {}", e.getMessage());
			throw new RuntimeException("파일 업로드 중 오류 발생", e);
		}
	}

	/**
	 * S3 파일 삭제
	 */
	public void deleteFile(String objectKey) {
		try {
			s3Client.deleteObject(DeleteObjectRequest.builder()
				.bucket(bucketName)
				.key(objectKey)
				.build());

			log.info("파일 삭제 성공: {}", objectKey);
		} catch (Exception e) {
			log.error("파일 삭제 실패: {}", e.getMessage());
			throw new RuntimeException("파일 삭제 중 오류 발생", e);
		}
	}
}
