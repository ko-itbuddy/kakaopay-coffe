package org.kakaopay.coffee.config.distributionlock;

import java.lang.reflect.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.kakaopay.coffee.api.common.AopForTransaction;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class DistributedLockAop {
    private static final String REDISSON_LOCK_PREFIX = "LOCK";

    private final RedissonClient redissonClient;
    private final AopForTransaction aopForTransaction;

    @Around("@annotation(org.kakaopay.coffee.config.distributionlock.DistributedLock)")
    public Object redissonLock(final ProceedingJoinPoint joinPoint) throws Throwable{
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        DistributedLock distributedLock = method.getAnnotation(DistributedLock.class);

        String key = String.format("%s:%s:%s:%s",
            REDISSON_LOCK_PREFIX,
            method.getDeclaringClass().getName(),
            method.getName(),
            CustomSpringELParser.getDynamicValue(signature.getParameterNames(), joinPoint.getArgs(), distributedLock.value())
        );

        RLock rLock = redissonClient.getLock(key);

        try {
            boolean available = rLock.tryLock(distributedLock.waitTime(), distributedLock.leaseTime(), distributedLock.timeUnit());  // (2)
            if (!available) {
                log.info("key:{}|NOT_AVAILABLE ", key);
                return false;
            }
            log.info("key:{}|AVAILABLE ", key);
            return aopForTransaction.proceed(joinPoint);
        } catch (InterruptedException e) {
            throw new InterruptedException();
        } finally {
            try {
                log.info("key:{}|SUCCESS", key);
                rLock.unlock();
                log.info("key:{}|UNLOCKED ", key);
            } catch (IllegalMonitorStateException e) {
                log.info("key:{}|ALREADY_UNLOCKED", key);
            }
        }
    }
}
