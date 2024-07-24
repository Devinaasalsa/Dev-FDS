package hbm.fraudDetectionSystem.ApplicationParameters.SpringLogic.AppConfiguration;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class JDBCSlowQueryAspect {
    private Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Value("${spring.jdbc.template.log.LOG_QUERIES_SLOWER_THAN_MS}")
    private long time;

    @Around("execution(* org.springframework.jdbc.core.JdbcTemplate.*(..))")
    public Object measureQueryExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long executionTime = System.currentTimeMillis() - startTime;

        if (executionTime > this.time) {
            LOGGER.info(
                    String.format(
                            "SlowQuery: %s milliseconds. Query: %s",
                            executionTime, joinPoint.getArgs()[0]
                    )
            );
        }

        return result;
    }
}
