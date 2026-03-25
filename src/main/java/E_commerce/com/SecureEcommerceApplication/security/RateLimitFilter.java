package E_commerce.com.SecureEcommerceApplication.security;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class RateLimitFilter extends OncePerRequestFilter {

    private final RateLimiterService rateLimiterService;
    private final RateLimitEntryPoint rateLimitEntryPoint;


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        String ip  = getClientIp(request);
        String uri = request.getRequestURI();

        boolean allowed;

        if (uri.contains("/api/v1/auth/login")
                || uri.contains("/api/v1/auth/register")) {

            allowed = rateLimiterService.isAllowed(
                    rateLimiterService.authBucket(ip));

        } else if (uri.contains("/api/v1/auth/forgot-password")
                || uri.contains("/api/v1/auth/reset-password")) {

            allowed = rateLimiterService.isAllowed(
                    rateLimiterService.resetPasswordBucket(ip));

        } else if (uri.contains("/api/v1/orders")) {

            allowed = rateLimiterService.isAllowed(
                    rateLimiterService.orderBucket(ip));

        } else {

            allowed = rateLimiterService.isAllowed(
                    rateLimiterService.generalBucket(ip));
        }


        if (!allowed) {
            log.warn("Rate limit exceeded: ip={} uri={}", ip, uri);

            rateLimitEntryPoint.commence(request, response,
                    new InsufficientAuthenticationException("Rate limit exceeded"));
            return;
        }

        chain.doFilter(request, response);
    }

    private String getClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim(); // أول IP لو في proxy
        }
        return request.getRemoteAddr();
    }
}