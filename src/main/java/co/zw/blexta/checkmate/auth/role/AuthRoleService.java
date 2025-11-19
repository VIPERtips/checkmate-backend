package co.zw.blexta.checkmate.auth.role;


import co.zw.blexta.checkmate.common.dto.CreateRoleDto;
import co.zw.blexta.checkmate.common.dto.UpdateRoleDto;
import co.zw.blexta.checkmate.common.response.ApiResponse;

import java.util.List;

public interface AuthRoleService {

    ApiResponse<AuthRole> createRole(CreateRoleDto dto);

    ApiResponse<AuthRole> updateRole(UpdateRoleDto dto);

    ApiResponse<Boolean> deleteRole(Long roleId);

    ApiResponse<AuthRole> getRole(Long roleId);

    ApiResponse<List<AuthRole>> getAllRoles();
}
