package co.zw.blexta.checkmate.common.response;

public record RolePermissionResponse(
        Long id,
        Long roleId,
        Long permissionId,
        String resource,
        String action
) {}
