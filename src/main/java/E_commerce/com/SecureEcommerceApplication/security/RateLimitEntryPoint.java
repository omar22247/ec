package E_commerce.com.SecureEcommerceApplication.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RateLimitEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        response.setStatus(429);
        response.setContentType("application/json");

        response.getWriter().write("""
        {
            "status": 429,
            "error": "Too Many Requests",
            "message": "Rate limit exceeded. Please try again later.",
            "path": "%s",
            "timestamp": "%s"
        }
        """.formatted(
                request.getRequestURI(),
                java.time.OffsetDateTime.now()
        ));
    }
}
