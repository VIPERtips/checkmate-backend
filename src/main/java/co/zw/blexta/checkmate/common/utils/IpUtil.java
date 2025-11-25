package co.zw.blexta.checkmate.common.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class IpUtil {

    private IpUtil() {}

    public static String getClientIp() {
        try {
            HttpServletRequest request =
                    ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                            .getRequest();

            String header = request.getHeader("X-Forwarded-For");

            if (header != null && !header.isBlank()) {
                return header.split(",")[0].trim();
            }

            return request.getRemoteAddr();

        } catch (Exception e) {
            return "UNKNOWN";
        }
    }
}
