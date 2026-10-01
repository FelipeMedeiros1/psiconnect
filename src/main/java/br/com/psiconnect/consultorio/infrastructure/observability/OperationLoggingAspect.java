package br.com.psiconnect.consultorio.infrastructure.observability;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
@Order(Ordered.LOWEST_PRECEDENCE - 1)
public class OperationLoggingAspect {
    private static final Logger log = LoggerFactory.getLogger(OperationLoggingAspect.class);

    @Around("execution(public * br.com.psiconnect.consultorio.application..*Service.*(..))")
    public Object trace(ProceedingJoinPoint invocation) throws Throwable {
        String previous = MDC.get("operationId");
        String operationId = UUID.randomUUID().toString();
        String operation = invocation.getSignature().getDeclaringType().getSimpleName()
                + "." + invocation.getSignature().getName();
        long start = System.nanoTime();
        MDC.put("operationId", operationId);
        log.info("event=operation_started operation={}", operation);
        try {
            Object result = invocation.proceed();
            // An existing outer transaction may still commit or roll back later.
            if (TransactionSynchronizationManager.isSynchronizationActive()) {
                String requestId = MDC.get("requestId");
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                    @Override
                    public void afterCompletion(int status) {
                        log.info("event=operation_transaction_completed operation={} operationRef={} requestRef={} outcome={} durationMs={}",
                                operation, operationId, requestId,
                                status == STATUS_COMMITTED ? "committed" : status == STATUS_ROLLED_BACK ? "rolled_back" : "unknown",
                                elapsed(start));
                    }
                });
                log.debug("event=operation_awaiting_transaction operation={}", operation);
            } else {
                log.info("event=operation_completed operation={} durationMs={}", operation, elapsed(start));
            }
            return result;
        } catch (Throwable failure) {
            log.warn("event=operation_failed operation={} durationMs={} errorType={}",
                    operation, elapsed(start), failure.getClass().getName());
            throw failure;
        } finally {
            if (previous == null) MDC.remove("operationId"); else MDC.put("operationId", previous);
        }
    }

    private static long elapsed(long start) {
        return TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
    }
}
