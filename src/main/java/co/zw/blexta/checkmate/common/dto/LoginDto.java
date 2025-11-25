package co.zw.blexta.checkmate.common.dto;

public record LoginDto (
        String email,
        String password,
        String userAgent
) {}
