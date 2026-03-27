package com.notes_api.security.bucket4j;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RateLimitingFilter extends OncePerRequestFilter {
    private final RateLimitProperties properties;
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Autowired
    public RateLimitingFilter(RateLimitProperties properties) {
        this.properties = properties;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if ("/auth/login".equals(request.getRequestURI()) && "POST".equalsIgnoreCase(request.getMethod())) {
            String ip = request.getRemoteAddr();
            Bucket bucket = buckets.computeIfAbsent(ip, b -> {
                Bandwidth limit = Bandwidth.builder()
                        .capacity(properties.getCapacity())
                        .refillGreedy(properties.getCapacity(), Duration.ofMinutes(1))
                        .build();
                return Bucket.builder().addLimit(limit).build();
            });

            ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

            if (probe.isConsumed()) {
                filterChain.doFilter(request, response);
            } else {
                long waitForRefill = Duration.ofNanos(probe.getNanosToWaitForRefill()).toSeconds() + 1;
                response.setStatus(429);
                response.setHeader("Retry-After", String.valueOf(waitForRefill));
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"too many requests, wait " + waitForRefill + " seconds\"}");
            }
            return;
        }

        filterChain.doFilter(request, response);
    }
}