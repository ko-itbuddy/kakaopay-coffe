package org.kakaopay.coffee.config.redisson;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RedissonLockAspect {

    private final RedissonClient redissonClient;

    public void redissonLock(ProceedingJoinPoint jointPoint) throws Throwable{
        MethodSignature signature = (MethodSignature) jointPoint.getSignature();
        Method method = signature.getMethod();
        RedissonLock annotation = method.getAnnotation(RedissonLock.class);
        String lockKey = method.getName() + CustomSpringELParser.getDynamicValue(signature.getParameterNames(),
            jointPoint.getArgs(), annotation.value());

        RLock lock = redissonClient.getLock(lockKey);

        try{
            boolean lockable = lock.tryLock(annotation.waitTime(), annotation.leaseTime(), TimeUnit.MICROSECONDS);
            if(!lockable){
                log.info("Lock 획득 실패={}", lockKey);
                return;
            }
            log.info("로직 수행");
            jointPoint.proceed();
        }catch (InterruptedException e){
            log.info("에러 발생");
            throw e;
        }finally {
            log.info("락 해제");
            lock.unlock();
        }
    }
}
