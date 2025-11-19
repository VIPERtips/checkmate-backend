package co.zw.blexta.checkmate.common.dto;

public record ResetPasswordDto(
        String token,
        String newPassword
) {}
