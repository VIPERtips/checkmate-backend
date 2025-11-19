package co.zw.blexta.checkmate.security;

import java.security.Key;
import java.time.Duration;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import co.zw.blexta.checkmate.auth.users.AuthUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

    @Value("${jwt_secret_key}")
    private String SECRET_KEY;

    private final long ACCESS_EXPIRATION = 1000 * 60 * 60;
    private final long REFRESH_EXPIRATION = 1000 * 60 * 60 * 24 * 7;

    private final RedisTemplate<String, Object> redis;

    public JwtService(RedisTemplate<String, Object> redis) {
        this.redis = redis;
    }


    public String extractSubject(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private boolean isExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }



    public String generateAccessToken(AuthUser user) {
        Map<String, Object> claims = Map.of(
                "email", user.getEmail(),
                "roles", user.getRoles().stream()
                        .map(r -> r.getRole().getName())
                        .toList()
        );

        return buildToken(claims, user.getId().toString(), ACCESS_EXPIRATION);
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


    public boolean isTokenValid(String token, UserDetails user) {
        if (isBlacklisted(token)) return false;

        String userId = extractSubject(token);
        boolean match = userId.equals(((AuthUser) user).getId().toString());

        return match && !isExpired(token);
    }

    public void blacklist(String token) {
        long ttl = extractClaim(token, Claims::getExpiration).getTime()
                - System.currentTimeMillis();

        redis.opsForValue().set("BLX:JWT:" + token, "revoked", Duration.ofMillis(ttl));
    }

    public boolean isBlacklisted(String token) {
        return Boolean.TRUE.equals(redis.hasKey("BLX:JWT:" + token));
    }


    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
