package co.zw.blexta.checkmate.auth.users;

import co.zw.blexta.checkmate.common.dto.LoginDto;
import co.zw.blexta.checkmate.common.dto.ResetPasswordDto;
import co.zw.blexta.checkmate.common.exception.BadRequestException;
import co.zw.blexta.checkmate.common.exception.ResourceNotFoundException;
import co.zw.blexta.checkmate.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class AuthUserServiceImpl implements AuthUserService {

    private final AuthUserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;

    @Override
    public ApiResponse<LoginDto> authenticate(LoginDto loginDto) {

        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.email(),
                        loginDto.password()
                )
        );

        return ApiResponse.<LoginDto>builder()
                .message("Login successful")
                .success(true)
                .data(loginDto)
                .build();
    }

    @Override
    public ApiResponse<String> registerUser(String email) {

        boolean exists = userRepo.existsByEmail(email);
        if (exists) throw new BadRequestException("Email already registered");

        String tempPassword = generateRandomPassword(8);
        String hashed = passwordEncoder.encode(tempPassword);

        AuthUser user = AuthUser.builder()
                .email(email)
                .password(hashed)
                .enabled(true)
                .accountNonExpired(true)
                .credentialsNonExpired(true)
                .accountNonLocked(true)
                .build();

        userRepo.save(user);

        return ApiResponse.<String>builder()
                .message("User registered")
                .success(true)
                .data("Temporary password: " + tempPassword)
                .build();
    }


    private String generateRandomPassword(int length) {

        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }

        return sb.toString();
    }
}
