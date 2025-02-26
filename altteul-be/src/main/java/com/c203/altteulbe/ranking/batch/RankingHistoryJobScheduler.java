package com.c203.altteulbe.ranking.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class RankingHistoryJobScheduler {

	private final JobLauncher jobLauncher;
	private final Job rankingHistoryJob;

	@Scheduled(cron = "0 0 0 * * ?")  // 매일 자정 실행
	public void runRankingHistoryJob() {
		log.info("Spring Batch : RankingHistory update");
		try {
			JobParameters jobParameters = new JobParametersBuilder()
				.addLong("timestamp", System.currentTimeMillis())
				.toJobParameters();

			jobLauncher.run(rankingHistoryJob, jobParameters);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
