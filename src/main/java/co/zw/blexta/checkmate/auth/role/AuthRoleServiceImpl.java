package co.zw.blexta.checkmate.auth.role;


import co.zw.blexta.checkmate.common.dto.CreateRoleDto;
import co.zw.blexta.checkmate.common.dto.RoleDto;
import co.zw.blexta.checkmate.common.dto.UpdateRoleDto;
import co.zw.blexta.checkmate.common.exception.ConflictException;
import co.zw.blexta.checkmate.common.exception.ResourceNotFoundException;
import co.zw.blexta.checkmate.common.response.ApiResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthRoleServiceImpl implements AuthRoleService {

    private final AuthRoleRepository roleRepo;

    @Override
    public ApiResponse<RoleDto> createRole(CreateRoleDto dto) {
        if (roleRepo.existsByName(dto.name())) {
            throw new ConflictException("Role already exists: " + dto.name());
        }

        AuthRole role = AuthRole.builder()
                .name(dto.name())
                .description(dto.description())
                .build();
        roleRepo.save(role);

        return ApiResponse.<RoleDto>builder()
                .success(true)
                .message("Role created successfully")
                .data(new RoleDto(role.getId(), role.getName(), role.getDescription()))
                .build();
    }

    @Override
    public ApiResponse<RoleDto> updateRole(UpdateRoleDto dto, Long id) {
        AuthRole role = roleRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + id));

        role.setName(dto.name());
        role.setDescription(dto.description());
        roleRepo.save(role);

        return ApiResponse.<RoleDto>builder()
                .success(true)
                .message("Role updated successfully")
                .data(new RoleDto(role.getId(), role.getName(), role.getDescription()))
                .build();
    }

    @Override
    @Transactional
    public ApiResponse<Boolean> deleteRole(Long id) {
        if (!roleRepo.existsById(id)) {
            throw new ResourceNotFoundException("Role not found with id: " + id);
        }

        roleRepo.deleteById(id);

        return ApiResponse.<Boolean>builder()
                .success(true)
                .message("Role deleted successfully")
                .data(true)
                .build();
    }

    @Override
    public ApiResponse<RoleDto> getRole(Long id) {
        AuthRole role = roleRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + id));

        return ApiResponse.<RoleDto>builder()
                .success(true)
                .message("Role fetched successfully")
                .data(new RoleDto(role.getId(), role.getName(), role.getDescription()))
                .build();
    }

    @Override
    public ApiResponse<List<RoleDto>> getAllRoles() {
        List<RoleDto> roles = roleRepo.findAll().stream()
                .map(r -> new RoleDto(r.getId(), r.getName(), r.getDescription()))
                .toList();

        return ApiResponse.<List<RoleDto>>builder()
                .success(true)
                .message("Roles fetched successfully")
                .data(roles)
                .build();
    }
}
