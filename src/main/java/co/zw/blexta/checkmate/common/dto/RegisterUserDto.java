package co.zw.blexta.checkmate.common.dto;

import lombok.Builder;
import lombok.Data;


@Builder
public record RegisterUserDto(
        String fullName,
        String email,
        String password,
        String role,
        boolean createLogin
) {}
