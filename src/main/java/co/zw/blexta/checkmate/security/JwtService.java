package co.zw.blexta.checkmate.security;

import co.zw.blexta.checkmate.auth.users.AuthUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt_secret_key}")
    private String SECRET_KEY;

    private final long ACCESS_EXPIRATION = 1000 * 60 * 60;       // 1 hour
    private final long REFRESH_EXPIRATION = 1000 * 60 * 60 * 24 * 7; // 7 days

    public String generateAccessToken(AuthUser user, Map<String, Object> extraClaims) {
        return buildToken(extraClaims, user.getId().toString(), ACCESS_EXPIRATION);
    }

    public String generateRefreshToken(AuthUser user) {
        return buildToken(Map.of(), user.getId().toString(), REFRESH_EXPIRATION);
    }

    private String buildToken(Map<String, Object> extraClaims, String subject, long expiryMs) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiryMs))
                .signWith(getSignKey())
                .compact();
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }

    public String extractSubject(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public boolean isTokenValid(String token, UserDetails user, Date lastLogoutAt) {
        try {
            Claims claims = extractAllClaims(token);

            // User ID check
            if (user != null) {
                String userId = extractSubject(token);
                if (!userId.equals(((AuthUser) user).getId().toString())) return false;
            }

            // Token expiration
            if (claims.getExpiration().before(new Date())) return false;

            // Logout expiration trick
            if (lastLogoutAt != null && claims.getIssuedAt().before(lastLogoutAt)) return false;

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
