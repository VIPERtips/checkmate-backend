package co.zw.blexta.checkmate.common.dto;

import java.time.LocalDateTime;

public record LoginAuditDto(
        Long id,
        Long userId,
        String emailAttempted,
        boolean isLoggedIn,
        String ipAddress,
        String userAgent,
        LocalDateTime createdAt
) {}
