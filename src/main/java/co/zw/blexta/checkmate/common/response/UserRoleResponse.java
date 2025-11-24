package co.zw.blexta.checkmate.common.response;

public record UserRoleResponse(
        Long id,
        Long userId,
        Long roleId,
        String roleName
) {}
