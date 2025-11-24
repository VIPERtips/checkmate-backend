package co.zw.blexta.checkmate.common.dto;

import java.util.List;

public record RoleWithPermissionsDto(
        Long id,
        String name,
        String description,
        List<PermissionDto> permissions,
        int usersCount
) {}
