package co.zw.blexta.checkmate.auth.role;


import co.zw.blexta.checkmate.common.dto.CreateRoleDto;
import co.zw.blexta.checkmate.common.dto.RoleDto;
import co.zw.blexta.checkmate.common.dto.UpdateRoleDto;
import co.zw.blexta.checkmate.common.response.ApiResponse;

import java.util.List;

public interface AuthRoleService {

    ApiResponse<RoleDto> createRole(CreateRoleDto dto);

    ApiResponse<RoleDto> updateRole(UpdateRoleDto dto, Long id);

    ApiResponse<Boolean> deleteRole(Long id);

    ApiResponse<RoleDto> getRole(Long id);

    ApiResponse<List<RoleDto>> getAllRoles();
}
