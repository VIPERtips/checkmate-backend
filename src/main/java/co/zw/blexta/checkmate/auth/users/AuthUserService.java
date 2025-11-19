package co.zw.blexta.checkmate.auth.users;

import co.zw.blexta.checkmate.common.dto.LoginDto;
import co.zw.blexta.checkmate.common.dto.ResetPasswordDto;
import co.zw.blexta.checkmate.common.response.ApiResponse;

public interface AuthUserService {

    ApiResponse<LoginDto> authenticate(LoginDto loginDto);

    ApiResponse<String> registerUser(String email);

    ApiResponse<String> sendPasswordResetLink(String email);

    ApiResponse<String> resetPassword(ResetPasswordDto resetPasswordDto);
}
