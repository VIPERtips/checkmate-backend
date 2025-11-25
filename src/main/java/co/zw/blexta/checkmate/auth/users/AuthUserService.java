package co.zw.blexta.checkmate.auth.users;

import co.zw.blexta.checkmate.common.dto.LoginDto;
import co.zw.blexta.checkmate.common.response.ApiResponse;

public interface AuthUserService {

    ApiResponse<?> authenticate(LoginDto loginDto);

    ApiResponse<?> logout(String accessToken);

    AuthUser registerUser(String email, Long roleId, String fullname);

    AuthUser findById(Long id);
}
