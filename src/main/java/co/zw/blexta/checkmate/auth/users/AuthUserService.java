package co.zw.blexta.checkmate.auth.users;

import co.zw.blexta.checkmate.common.dto.ChangePasswordDto;
import co.zw.blexta.checkmate.common.dto.LoginDto;
import co.zw.blexta.checkmate.common.response.ApiResponse;

public interface AuthUserService {

    ApiResponse<?> authenticate(LoginDto loginDto);

    ApiResponse<?> logout(String accessToken);

    AuthUser registerUser(String email, Long roleId, String fullname);

    ApiResponse<?> getCurrentSession(String accessToken);

    AuthUser getUserById(Long id);
    
    ApiResponse<?> changePassword(Long userId,ChangePasswordDto dto);
    String resetPasswordForUser(AuthUser user, String fullName);
    ApiResponse<?> assignRoleToUser(Long userId, Long roleId);
    ApiResponse<?> removeRoleFromUser(Long userId, Long roleId);
}
