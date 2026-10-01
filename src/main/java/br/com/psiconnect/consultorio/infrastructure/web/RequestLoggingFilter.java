package br.com.psiconnect.consultorio.infrastructure.web;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerMapping;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestLoggingFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                  FilterChain chain) throws ServletException, IOException {
        String previous = MDC.get("requestId");
        // Always generate locally: client headers may contain personal data or forged IDs.
        String requestId = UUID.randomUUID().toString();
        MDC.put("requestId", requestId);
        response.setHeader("X-Request-ID", requestId);
        long start = System.nanoTime();
        boolean failed = false;
        log.info("event=http_started");
        try {
            chain.doFilter(request, response);
        } catch (IOException | ServletException | RuntimeException | Error failure) {
            failed = true;
            log.error("event=http_unhandled_error errorType={}", failure.getClass().getName());
            throw failure;
        } finally {
            Object route = request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
            int status = failed ? 500 : response.getStatus();
            var level = status >= 500 ? log.atError() : status >= 400 ? log.atWarn() : log.atInfo();
            level.log("event=http_completed method={} route={} status={} durationMs={}",
                    safeMethod(request.getMethod()), route == null ? "unmapped" : route,
                    status, TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start));
            if (previous == null) MDC.remove("requestId"); else MDC.put("requestId", previous);
        }
    }

    private String safeMethod(String method) {
        return switch (method) {
            case "GET", "POST", "PUT", "PATCH", "DELETE", "HEAD", "OPTIONS", "TRACE" -> method;
            default -> "OTHER";
        };
    }
}
