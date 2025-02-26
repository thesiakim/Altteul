package com.c203.altteulbe.common.aop;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import com.c203.altteulbe.common.annotation.DistributedLock;
import com.c203.altteulbe.common.utils.CustomSpringELParser;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @DistributedLock 선언 시 수행되는 Aop class
 */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class DistributedLockAop {

	private static final String REDISSON_LOCK_PREFIX = "LOCK:";
	private final RedissonClient redissonClient;
	private final AopForTransaction aopForTransaction;

	@Around("@annotation(com.c203.altteulbe.common.annotation.DistributedLock)")
	public Object lock(final ProceedingJoinPoint joinPoint) throws Throwable {
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		Method method = signature.getMethod();
		DistributedLock distributedLock = method.getAnnotation(DistributedLock.class);

		String key = REDISSON_LOCK_PREFIX + CustomSpringELParser.getDynamicValue(signature.getParameterNames(), joinPoint.getArgs(), distributedLock.key());
		// 락의 이름으로 RLock 인스턴스 획득
		RLock rLock = redissonClient.getLock(key);
		boolean available = false;

		try {
			log.info("락 획득 시도");
			// leaseTime=-1인 경우 Watchdog 활성화 (Redisson이 자동으로 락 갱신)
			if (distributedLock.leaseTime() == -1) {
				available = rLock.tryLock(distributedLock.waitTime(), TimeUnit.SECONDS);
			} else {
				available = rLock.tryLock(distributedLock.waitTime(), distributedLock.leaseTime(), distributedLock.timeUnit());
			}

			// 락 획득 실패 시 실행 중단
			if (!available) {
				log.warn("Lock 획득 실패: {}", key);
				return false;
			}

			// DistributedLock 어노테이션이 선언된 메서드를 별도의 트랜잭션으로 실행
			return aopForTransaction.proceed(joinPoint);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new RuntimeException("Lock 획득 중 인터럽트 발생", e);
		} finally {
			try {
				// 종료 시 락 해제
				rLock.unlock();
			} catch (IllegalMonitorStateException e) {
				log.info("Redisson Lock Already UnLock {} {}", method.getName(), key);
			}
		}
	}
}
