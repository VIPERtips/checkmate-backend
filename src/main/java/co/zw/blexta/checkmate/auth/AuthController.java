package co.zw.blexta.checkmate.auth;

import co.zw.blexta.checkmate.auth.users.AuthUserService;
import co.zw.blexta.checkmate.common.dto.ChangePasswordDto;
import co.zw.blexta.checkmate.common.dto.LoginDto;
import co.zw.blexta.checkmate.common.response.ApiResponse;
import co.zw.blexta.checkmate.common.utils.SessionUtil;
import co.zw.blexta.checkmate.staff_users.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.netty.http.server.HttpServerRequest;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthUserService authUserService;
    private final SessionUtil sessionUtil;

    @PostMapping("/login")
    public ApiResponse<?> login(@RequestBody LoginDto dto){
        return authUserService.authenticate(dto);
    }

    @PostMapping("/logout")
    public ApiResponse<?> logout(@RequestBody Map<String, String> body){
        return authUserService.logout(body.get("accessToken"));
    }
    
    @PostMapping("/change-password")
    public ApiResponse<?> changePassword(@RequestHeader("Authorization") String authHeader, @RequestBody ChangePasswordDto dto){
    	Long userId = sessionUtil.extractUserId(authHeader);
    	return authUserService.changePassword(userId, dto);
    }

    @PostMapping("/me")
    public ApiResponse<?> me(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        return authUserService.getCurrentSession(token);
    }

}
