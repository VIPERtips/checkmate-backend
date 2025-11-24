package co.zw.blexta.checkmate.auth.user_roles;

import co.zw.blexta.checkmate.common.dto.AssignRoleDto;
import co.zw.blexta.checkmate.common.response.ApiResponse;
import co.zw.blexta.checkmate.common.response.UserRoleResponse;


import java.util.List;

public interface AuthUserRoleService {

    ApiResponse<UserRoleResponse> assignRole(AssignRoleDto dto);

    ApiResponse<String> removeRole(Long userRoleId);

    ApiResponse<List<UserRoleResponse>> getUserRoles(Long userId);
}
