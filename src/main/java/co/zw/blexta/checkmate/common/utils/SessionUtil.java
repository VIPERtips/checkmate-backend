package co.zw.blexta.checkmate.common.utils;

import co.zw.blexta.checkmate.security.JwtService;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Component;

@Component
public class SessionUtil {

    private final JwtService jwtService;

    public SessionUtil(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    public Long extractUserId(String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Authorization header missing or invalid");
        }

        String token = authHeader.replace("Bearer ", "").trim();
        Claims claims = jwtService.extractAllClaims(token);

        Object userIdObj = claims.get("userId");
        if (userIdObj == null) throw new RuntimeException("User ID not found in token");

        try {
            return Long.parseLong(userIdObj.toString());
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid user ID format");
        }
    }
}
