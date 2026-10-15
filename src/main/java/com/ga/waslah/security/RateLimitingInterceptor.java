package com.ga.waslah.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitingInterceptor implements HandlerInterceptor {

    private final Map<String, RequestCounter> requests =
            new ConcurrentHashMap<>();

    private static final int LOGIN_LIMIT = 5;
    private static final int REGISTER_LIMIT = 5;
    private static final int FORGOT_PASSWORD_LIMIT = 3;
    private static final int RESET_PASSWORD_LIMIT = 3;

    private static final long WINDOW_SECONDS = 60;


    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) {

        String path = request.getRequestURI();

        int limit;

        if (path.equals("/auth/login")) {
            limit = LOGIN_LIMIT;

        } else if (path.equals("/auth/register")) {
            limit = REGISTER_LIMIT;

        } else if (path.equals("/auth/forgot-password")) {
            limit = FORGOT_PASSWORD_LIMIT;

        } else if (path.equals("/auth/reset-password")) {
        limit = RESET_PASSWORD_LIMIT;}
        else {
            return true;
        }

        String key =
                request.getRemoteAddr() + ":" + path;

        RequestCounter counter =
                requests.computeIfAbsent(
                        key,
                        k -> new RequestCounter()
                );

        synchronized (counter) {

            long now = Instant.now().getEpochSecond();

            // Reset counter after one minute.
            if (now - counter.startTime >= WINDOW_SECONDS) {
                counter.startTime = now;
                counter.count = 0;
            }

            counter.count++;

            if (counter.count > limit) {

                response.setStatus(
                        HttpStatus.TOO_MANY_REQUESTS.value()
                );

                response.setContentType("application/json");

                try {
                    response.getWriter().write(
                            """
                            {
                                "status": 429,
                                "error": "TOO_MANY_REQUESTS",
                                "message": "Too many requests. Please try again later."
                            }
                            """
                    );
                } catch (Exception ignored) {
                }

                return false;
            }
        }

        return true;
    }


    private static class RequestCounter {

        private long startTime =
                Instant.now().getEpochSecond();

        private int count = 0;
    }
}
