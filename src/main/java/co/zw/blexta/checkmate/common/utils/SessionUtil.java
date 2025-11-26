package co.zw.blexta.checkmate.common.utils;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SessionUtil {

    private final RedisTemplate<String, Object> redis;

    public SessionUtil(RedisTemplate<String, Object> redis) {
        this.redis = redis;
    }

    public Long extractUserId(String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Authorization header missing or invalid");
        }

        String token = authHeader.replace("Bearer ", "").trim();
        Object sessionObj = redis.opsForValue().get("BLX:SESS:" + token);

        if (!(sessionObj instanceof Map<?, ?> session)) {
            throw new RuntimeException("Session expired or invalid");
        }

        Object userIdObj = session.get("userId");

        if (userIdObj == null) {
            throw new RuntimeException("User ID not found in session");
        }

        try {
            return Long.parseLong(userIdObj.toString());
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid user ID format");
        }
    }
}
