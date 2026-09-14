package br.com.psiconnect.consultorio;

import br.com.psiconnect.consultorio.infrastructure.observability.OperationLoggingAspect;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class OperationLoggingTest {
    @Test
    void aguardaTransacaoExternaERegistraRollbackSemFalsoSucesso() throws Throwable {
        Logger logger = (Logger) LoggerFactory.getLogger(OperationLoggingAspect.class);
        var logs = new ListAppender<ILoggingEvent>();
        logs.start();
        logger.addAppender(logs);
        TransactionSynchronizationManager.initSynchronization();
        try {
            var call = invocation();
            when(call.proceed()).thenReturn("resultado sensivel");
            assertThat(new OperationLoggingAspect().trace(call)).isEqualTo("resultado sensivel");
            assertThat(logs.list).noneMatch(e -> e.getFormattedMessage().contains("event=operation_completed"));
            TransactionSynchronizationManager.getSynchronizations().forEach(
                    s -> s.afterCompletion(TransactionSynchronization.STATUS_ROLLED_BACK));
            assertThat(logs.list).anyMatch(e -> e.getFormattedMessage().contains("outcome=rolled_back"));
            assertThat(logs.list).noneMatch(e -> e.getFormattedMessage().contains("resultado sensivel"));
            assertThat(MDC.get("operationId")).isNull();
        } finally {
            TransactionSynchronizationManager.clearSynchronization();
            logger.detachAppender(logs);
            logs.stop();
        }
    }

    @Test
    void preservaExcecaoEContextoAnterior() throws Throwable {
        var call = invocation();
        var failure = new IllegalStateException("CPF sensivel");
        when(call.proceed()).thenThrow(failure);
        MDC.put("operationId", "outer");
        try {
            assertThatThrownBy(() -> new OperationLoggingAspect().trace(call)).isSameAs(failure);
            assertThat(MDC.get("operationId")).isEqualTo("outer");
        } finally {
            MDC.clear();
        }
    }

    private ProceedingJoinPoint invocation() {
        var call = mock(ProceedingJoinPoint.class);
        var signature = mock(Signature.class);
        when(call.getSignature()).thenReturn(signature);
        when(signature.getDeclaringType()).thenReturn(OperationLoggingTest.class);
        when(signature.getName()).thenReturn("cadastrar");
        return call;
    }
}
