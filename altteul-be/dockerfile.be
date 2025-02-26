# 그레들 버전, JDK 버전에 따라 다르게 수정
FROM gradle:8.1.0-jdk17-alpine AS builder
WORKDIR /build

# 그레들 파일이 변경되었을 때만 새롭게 의존 패키지 다운로드 받게 함.
COPY build.gradle settings.gradle /build/
RUN gradle dependencies --refresh-dependencies -x test

# 빌더 이미지에서 애플리케이션 빌드
COPY . /build
RUN chmod +x ./gradlew
RUN gradle clean build -x test --parallel

# APP
FROM openjdk:17-slim
WORKDIR /app

# 타임존 설정
ENV TZ=Asia/Seoul
RUN apt-get update \
    && apt-get install -y tzdata \
    && ln -snf /usr/share/zoneinfo/$TZ /etc/localtime \
    && echo $TZ > /etc/timezone \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

# 빌더 이미지에서 jar 파일만 복사
COPY --from=builder /build/build/libs/*-SNAPSHOT.jar ./app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "-Djava.security.egd=file:/dev/./urandom", "-Dsun.net.inetaddr.ttl=0", "app.jar"]