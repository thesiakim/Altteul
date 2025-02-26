package com.c203.altteulbe.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

/*
 * 스케줄러를 멀티 스레드로 동작하기 위한 설정
 */

@Configuration
public class SchedulerConfig implements SchedulingConfigurer {
	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		ThreadPoolTaskScheduler threadPool = new ThreadPoolTaskScheduler();

		int N = Runtime.getRuntime().availableProcessors();  // core 개수
		threadPool.setPoolSize(N*2);                         // 스레드 풀 개수 설정
		threadPool.initialize();                             // 스레드 풀 초기화

		taskRegistrar.setTaskScheduler(threadPool);  // 스케줄러에서 스레드풀 사용
	}
}

