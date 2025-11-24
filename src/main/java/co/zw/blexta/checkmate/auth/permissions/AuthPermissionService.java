package co.zw.blexta.checkmate.auth.permissions;

import co.zw.blexta.checkmate.common.dto.AuthPermissionDTO;
import co.zw.blexta.checkmate.common.response.ApiResponse;

import java.util.List;

public interface AuthPermissionService {

    ApiResponse<AuthPermissionDTO> createPermission(String resource, String action);

    ApiResponse<List<AuthPermissionDTO>> getAllPermissions();

    ApiResponse<AuthPermissionDTO> getPermission(Long id);

    ApiResponse<String> deletePermission(Long id);
}
