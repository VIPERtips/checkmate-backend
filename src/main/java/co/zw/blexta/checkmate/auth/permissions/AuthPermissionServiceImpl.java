package co.zw.blexta.checkmate.auth.permissions;

import co.zw.blexta.checkmate.common.dto.AuthPermissionDTO;
import co.zw.blexta.checkmate.common.exception.ResourceNotFoundException;
import co.zw.blexta.checkmate.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthPermissionServiceImpl implements AuthPermissionService {

    private final AuthPermissionRepository repo;

    @Override
    public ApiResponse<AuthPermissionDTO> createPermission(String resource, String action) {
        AuthPermission permission = AuthPermission.builder()
                .resource(resource)
                .action(action)
                .build();

        AuthPermission saved = repo.save(permission);
        AuthPermissionDTO dto = new AuthPermissionDTO(saved.getId(), saved.getResource(), saved.getAction());

        return ApiResponse.<AuthPermissionDTO>builder()
                .message("Permission created")
                .success(true)
                .data(dto)
                .build();
    }

    @Override
    public ApiResponse<List<AuthPermissionDTO>> getAllPermissions() {
        List<AuthPermissionDTO> permissions = repo.findAll().stream()
                .map(p -> new AuthPermissionDTO(p.getId(), p.getResource(), p.getAction()))
                .toList();

        return ApiResponse.<List<AuthPermissionDTO>>builder()
                .message("Permissions retrieved")
                .success(true)
                .data(permissions)
                .build();
    }

    @Override
    public ApiResponse<AuthPermissionDTO> getPermission(Long id) {
        AuthPermission permission = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Permission not found"));

        AuthPermissionDTO dto = new AuthPermissionDTO(permission.getId(), permission.getResource(), permission.getAction());

        return ApiResponse.<AuthPermissionDTO>builder()
                .message("Permission retrieved")
                .success(true)
                .data(dto)
                .build();
    }

    @Override
    public ApiResponse<String> deletePermission(Long id) {
        AuthPermission permission = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Permission not found"));

        repo.delete(permission);

        return ApiResponse.<String>builder()
                .message("Permission deleted")
                .success(true)
                .data("Permission removed")
                .build();
    }
}
