package co.zw.blexta.checkmate.common.dto;

import lombok.Builder;

@Builder
public record AssignRoleDto(
        Long userId,
        Long roleId
) {}
