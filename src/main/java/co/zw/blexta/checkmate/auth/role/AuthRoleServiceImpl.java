package co.zw.blexta.checkmate.auth.role;

import co.zw.blexta.checkmate.common.dto.CreateRoleDto;
import co.zw.blexta.checkmate.common.dto.PermissionDto;
import co.zw.blexta.checkmate.common.dto.RoleWithPermissionsDto;
import co.zw.blexta.checkmate.common.dto.UpdateRoleDto;
import co.zw.blexta.checkmate.common.exception.ConflictException;
import co.zw.blexta.checkmate.common.exception.ResourceNotFoundException;
import co.zw.blexta.checkmate.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class AuthRoleServiceImpl implements AuthRoleService {
    private final AuthRoleRepository roleRepo;

    @Override
    public ApiResponse<RoleWithPermissionsDto> createRole(CreateRoleDto dto) {
        if (roleRepo.existsByName(dto.name())) {
            throw new ConflictException("Role already exists: " + dto.name());
        }

        AuthRole role = AuthRole.builder()
                .name(dto.name())
                .description(dto.description())
                .build();
        roleRepo.save(role);

        RoleWithPermissionsDto roleDto = mapToDto(role);

        return ApiResponse.<RoleWithPermissionsDto>builder()
                .success(true)
                .message("Role created successfully")
                .data(roleDto)
                .build();
    }

    @Override
    public ApiResponse<RoleWithPermissionsDto> updateRole(UpdateRoleDto dto) {
        AuthRole role = roleRepo.findById(dto.id())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + dto.id()));
        role.setName(dto.name());
        role.setDescription(dto.description());
        roleRepo.save(role);

        return ApiResponse.<RoleWithPermissionsDto>builder()
                .success(true)
                .message("Role updated successfully")
                .data(mapToDto(role))
                .build();
    }

    @Override
    public ApiResponse<Boolean> deleteRole(Long roleId) {
        if (!roleRepo.existsById(roleId)) {
            throw new ResourceNotFoundException("Role not found with id: " + roleId);
        }
        roleRepo.deleteById(roleId);
        return ApiResponse.<Boolean>builder()
                .success(true)
                .message("Role deleted successfully")
                .data(true)
                .build();
    }

    @Override
    public ApiResponse<RoleWithPermissionsDto> getRole(Long roleId) {
        AuthRole role = roleRepo.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + roleId));
        return ApiResponse.<RoleWithPermissionsDto>builder()
                .success(true)
                .message("Role fetched successfully")
                .data(mapToDto(role))
                .build();
    }

    @Override
    public ApiResponse<List<RoleWithPermissionsDto>> getAllRoles() {
        List<RoleWithPermissionsDto> roles = roleRepo.findAll().stream()
                .map(this::mapToDto)
                .toList();

        return ApiResponse.<List<RoleWithPermissionsDto>>builder()
                .success(true)
                .message("Roles fetched successfully")
                .data(roles)
                .build();
    }

    private RoleWithPermissionsDto mapToDto(AuthRole role) {
        List<PermissionDto> permissions = role.getPermissions().stream()
                .map(rp -> new PermissionDto(
                        rp.getPermission().getResource(),
                        List.of(rp.getPermission().getAction())
                ))
                .toList();

        return new RoleWithPermissionsDto(
                role.getId(),
                role.getName(),
                role.getDescription(),
                permissions,
                role.getUserRoles() != null ? role.getUserRoles().size() : 0
        );
    }
}
