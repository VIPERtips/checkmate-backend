package co.zw.blexta.checkmate.common.dto;


public record RegisterUserDto(
        String fullName,
        String email,
        String password,
        String role,
        boolean createLogin
) {}
