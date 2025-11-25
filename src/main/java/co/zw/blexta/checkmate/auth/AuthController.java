package co.zw.blexta.checkmate.auth;

import co.zw.blexta.checkmate.auth.users.AuthUserService;
import co.zw.blexta.checkmate.common.dto.LoginDto;
import co.zw.blexta.checkmate.common.response.ApiResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthUserService authUserService;

    @PostMapping("/login")
    public ApiResponse<?> login(@RequestBody LoginDto dto){
        return authUserService.authenticate(dto);
    }

    @PostMapping("/logout")
    public ApiResponse<?> logout(@RequestBody Map<String, String> body){
        return authUserService.logout(body.get("accessToken"));
    }
}
