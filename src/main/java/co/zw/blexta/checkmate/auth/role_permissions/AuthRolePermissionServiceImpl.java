package co.zw.blexta.checkmate.auth.role_permissions;

import co.zw.blexta.checkmate.auth.permissions.AuthPermission;
import co.zw.blexta.checkmate.auth.permissions.AuthPermissionRepository;
import co.zw.blexta.checkmate.auth.role.AuthRole;
import co.zw.blexta.checkmate.auth.role.AuthRoleRepository;
import co.zw.blexta.checkmate.common.dto.AssignRolePermissionDto;
import co.zw.blexta.checkmate.common.exception.ResourceNotFoundException;
import co.zw.blexta.checkmate.common.response.ApiResponse;
import co.zw.blexta.checkmate.common.response.RolePermissionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthRolePermissionServiceImpl implements AuthRolePermissionService {

    private final AuthRolePermissionRepository repo;
    private final AuthRoleRepository roleRepo;
    private final AuthPermissionRepository permRepo;

    @Override
    public ApiResponse<RolePermissionResponse> assignPermission(AssignRolePermissionDto dto) {

        AuthRole role = roleRepo.findById(dto.roleId())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

        AuthPermission perm = permRepo.findById(dto.permissionId())
                .orElseThrow(() -> new ResourceNotFoundException("Permission not found"));

        AuthRolePermission saved = repo.save(
                AuthRolePermission.builder()
                        .role(role)
                        .permission(perm)
                        .build()
        );

        RolePermissionResponse response = new RolePermissionResponse(
                saved.getId(),
                role.getId(),
                perm.getId(),
                perm.getResource(),
                perm.getAction()
        );

        return ApiResponse.<RolePermissionResponse>builder()
                .message("Permission assigned to role")
                .success(true)
                .data(response)
                .build();
    }

    @Override
    public ApiResponse<String> removePermission(Long id) {

        AuthRolePermission rp = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mapping not found"));

        repo.delete(rp);

        return ApiResponse.<String>builder()
                .message("Permission removed from role")
                .success(true)
                .data("Deleted")
                .build();
    }

    @Override
    public ApiResponse<List<RolePermissionResponse>> getRolePermissions(Long roleId) {

        List<RolePermissionResponse> mapped = repo.findByRoleId(roleId)
                .stream()
                .map(r -> new RolePermissionResponse(
                        r.getId(),
                        roleId,
                        r.getPermission().getId(),
                        r.getPermission().getResource(),
                        r.getPermission().getAction()
                ))
                .toList();

        return ApiResponse.<List<RolePermissionResponse>>builder()
                .message("Role permissions retrieved")
                .success(true)
                .data(mapped)
                .build();
    }
}
