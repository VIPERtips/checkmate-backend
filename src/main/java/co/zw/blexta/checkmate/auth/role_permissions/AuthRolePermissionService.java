package co.zw.blexta.checkmate.auth.role_permissions;

import co.zw.blexta.checkmate.common.dto.AssignRolePermissionDto;
import co.zw.blexta.checkmate.common.response.ApiResponse;
import co.zw.blexta.checkmate.common.response.RolePermissionResponse;

import java.util.List;

public interface AuthRolePermissionService {

    ApiResponse<RolePermissionResponse> assignPermission(AssignRolePermissionDto dto);

    ApiResponse<String> removePermission(Long id);

    ApiResponse<List<RolePermissionResponse>> getRolePermissions(Long roleId);
}
