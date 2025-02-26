package com.c203.altteulbe.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.c203.altteulbe.ranking.batch.RankingComposite;
import com.c203.altteulbe.ranking.batch.RankingHistoryItemWriter;
import com.c203.altteulbe.ranking.persistent.entity.RankingHistory;
import com.c203.altteulbe.user.persistent.entity.User;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class BatchConfig {

	private final RankingHistoryItemWriter rankingHistoryItemWriter;
	private final JobRepository jobRepository;
	private final PlatformTransactionManager transactionManager;

	@Bean
	public Job rankingHistoryJob(Step rankingHistoryStep) {
		return new JobBuilder("rankingHistoryJob", jobRepository)
			.incrementer(new RunIdIncrementer())
			.start(rankingHistoryStep)
			.build();
	}

	@Bean
	public Step rankingHistoryStep(
									ItemReader<User> rankingHistoryItemReader,
									ItemProcessor<User, RankingComposite> rankingHistoryItemProcessor,
									ItemWriter<RankingComposite> rankingHistoryItemWriter) {
		return new StepBuilder("rankingHistoryStep", jobRepository)
							.<User, RankingComposite>chunk(100, transactionManager)
							.reader(rankingHistoryItemReader)
							.processor(rankingHistoryItemProcessor)
							.writer(rankingHistoryItemWriter)
							.build();
	}
}


