package br.com.psiconnect.consultorio;

import br.com.psiconnect.consultorio.infrastructure.web.RequestLoggingFilter;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.HandlerMapping;

import static org.assertj.core.api.Assertions.*;

class RequestLoggingTest {
    @Test
    void correlacionaSemExporDadosERestauraContexto() throws Exception {
        Logger logger = (Logger) LoggerFactory.getLogger(RequestLoggingFilter.class);
        ListAppender<ILoggingEvent> logs = new ListAppender<>();
        logs.start();
        logger.addAppender(logs);
        MDC.put("requestId", "outer");
        try {
            var request = new MockHttpServletRequest("GET", "/pacientes/nome/NomeSecreto");
            request.setQueryString("cpf=12345678901");
            request.addHeader("X-Request-ID", "NomeSecreto");
            var response = new MockHttpServletResponse();
            new RequestLoggingFilter().doFilter(request, response, (req, res) -> {
                assertThat(MDC.get("requestId")).isEqualTo(response.getHeader("X-Request-ID"));
                request.setAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE, "/pacientes/nome/{nome}");
            });
            assertThat(response.getHeader("X-Request-ID")).matches("[a-f0-9-]{36}");
            assertThat(MDC.get("requestId")).isEqualTo("outer");
            String output = logs.list.stream().map(ILoggingEvent::getFormattedMessage)
                    .collect(java.util.stream.Collectors.joining("\n"));
            assertThat(output).contains("route=/pacientes/nome/{nome}", "status=200", "durationMs=")
                    .doesNotContain("NomeSecreto", "12345678901");
        } finally {
            MDC.clear();
            logger.detachAppender(logs);
            logs.stop();
        }
    }

    @Test
    void limpaContextoQuandoCadeiaFalha() {
        var response = new MockHttpServletResponse();
        assertThatThrownBy(() -> new RequestLoggingFilter().doFilter(
                new MockHttpServletRequest("GET", "/"), response,
                (req, res) -> { throw new IllegalStateException("segredo"); }))
                .isInstanceOf(IllegalStateException.class);
        assertThat(MDC.get("requestId")).isNull();
        assertThat(response.getHeader("X-Request-ID")).isNotBlank();
    }
}
