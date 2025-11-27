package co.zw.blexta.checkmate.common.dto;

public record RoleDto(
        Long id,
        String name,
        String description,
        Long userCount
) {}