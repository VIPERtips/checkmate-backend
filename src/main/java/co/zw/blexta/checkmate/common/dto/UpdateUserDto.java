package co.zw.blexta.checkmate.common.dto;

public record UpdateUserDto(
        String fullName,
        String email,
        Long id
) {}
