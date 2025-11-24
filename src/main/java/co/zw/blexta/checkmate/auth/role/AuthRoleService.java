package co.zw.blexta.checkmate.auth.role;


import co.zw.blexta.checkmate.common.dto.CreateRoleDto;
import co.zw.blexta.checkmate.common.dto.RoleWithPermissionsDto;
import co.zw.blexta.checkmate.common.dto.UpdateRoleDto;
import co.zw.blexta.checkmate.common.response.ApiResponse;

import java.util.List;
public interface AuthRoleService {

    ApiResponse<RoleWithPermissionsDto> createRole(CreateRoleDto dto);

    ApiResponse<RoleWithPermissionsDto> updateRole(UpdateRoleDto dto);

    ApiResponse<Boolean> deleteRole(Long roleId);

    ApiResponse<RoleWithPermissionsDto> getRole(Long roleId);

    ApiResponse<List<RoleWithPermissionsDto>> getAllRoles();
}
