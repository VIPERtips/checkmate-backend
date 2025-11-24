package co.zw.blexta.checkmate.auth.user_roles;

import co.zw.blexta.checkmate.auth.role.AuthRole;
import co.zw.blexta.checkmate.auth.role.AuthRoleRepository;
import co.zw.blexta.checkmate.auth.users.AuthUser;
import co.zw.blexta.checkmate.auth.users.AuthUserRepository;
import co.zw.blexta.checkmate.common.dto.AssignRoleDto;
import co.zw.blexta.checkmate.common.exception.ResourceNotFoundException;
import co.zw.blexta.checkmate.common.response.ApiResponse;
import co.zw.blexta.checkmate.common.response.UserRoleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthUserRoleServiceImpl implements AuthUserRoleService {

    private final AuthUserRepository userRepo;
    private final AuthRoleRepository roleRepo;
    private final AuthUserRoleRepository userRoleRepo;

    @Override
    public ApiResponse<UserRoleResponse> assignRole(AssignRoleDto dto) {

        AuthUser user = userRepo.findById(dto.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        AuthRole role = roleRepo.findById(dto.roleId())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

        AuthUserRole userRole = AuthUserRole.builder()
                .user(user)
                .role(role)
                .build();

        AuthUserRole saved = userRoleRepo.save(userRole);

        UserRoleResponse response = new UserRoleResponse(
                saved.getId(),
                user.getId(),
                role.getId(),
                role.getName()
        );

        return ApiResponse.<UserRoleResponse>builder()
                .message("Role assigned to user")
                .success(true)
                .data(response)
                .build();
    }

    @Override
    public ApiResponse<String> removeRole(Long userRoleId) {

        AuthUserRole userRole = userRoleRepo.findById(userRoleId)
                .orElseThrow(() -> new ResourceNotFoundException("User role not found"));

        userRoleRepo.delete(userRole);

        return ApiResponse.<String>builder()
                .message("Role removed")
                .success(true)
                .data("Role removed from user")
                .build();
    }

    @Override
    public ApiResponse<List<UserRoleResponse>> getUserRoles(Long userId) {

        AuthUser user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<UserRoleResponse> roles = userRoleRepo.findByUserId(userId)
                .stream()
                .map(r -> new UserRoleResponse(
                        r.getId(),
                        userId,
                        r.getRole().getId(),
                        r.getRole().getName()
                ))
                .toList();

        return ApiResponse.<List<UserRoleResponse>>builder()
                .message("User roles retrieved")
                .success(true)
                .data(roles)
                .build();
    }
}
