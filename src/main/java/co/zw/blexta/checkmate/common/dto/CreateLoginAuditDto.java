package co.zw.blexta.checkmate.common.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateLoginAuditDto {
    private Long userId;
    private String emailAttempted;
    private boolean loggedIn;
    private String ipAddress;
    private String userAgent;
}
