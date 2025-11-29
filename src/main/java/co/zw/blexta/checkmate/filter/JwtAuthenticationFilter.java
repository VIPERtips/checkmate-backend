package co.zw.blexta.checkmate.filter;

import co.zw.blexta.checkmate.auth.users.AuthUser;
import co.zw.blexta.checkmate.auth.users.AuthUserRepository;
import co.zw.blexta.checkmate.security.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtService jwtService;
    private final AuthUserRepository userRepo;

    public JwtAuthenticationFilter(JwtService jwtService, AuthUserRepository userRepo) {
        this.jwtService = jwtService;
        this.userRepo = userRepo;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            String userId = jwtService.extractSubject(token);
            AuthUser user = userRepo.findById(Long.parseLong(userId)).orElseThrow();

            // Validate token using lastLogoutAt trick
            if (!jwtService.isTokenValid(token, user, user.getLastLogoutAt())) {
                sendUnauthorized(response, "Invalid or expired token");
                return;
            }

            // Set authenticated user in Spring Security context
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authToken);

            filterChain.doFilter(request, response);

        } catch (Exception e) {
            logger.error("JWT authentication failed: {}", e.getMessage());
            sendUnauthorized(response, "Authentication error");
        }
    }

    private void sendUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\":\"" + message + "\"}");
        response.getWriter().flush();
    }
}
