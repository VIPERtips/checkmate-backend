package co.zw.blexta.checkmate.auth.users;

import co.zw.blexta.checkmate.auth.role.AuthRole;
import co.zw.blexta.checkmate.auth.role.AuthRoleRepository;
import co.zw.blexta.checkmate.common.dto.LoginDto;
import co.zw.blexta.checkmate.common.exception.BadRequestException;
import co.zw.blexta.checkmate.common.exception.ResourceNotFoundException;
import co.zw.blexta.checkmate.common.response.ApiResponse;
import co.zw.blexta.checkmate.notification.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthUserServiceImpl implements AuthUserService {

    private final AuthUserRepository userRepo;
    private final AuthRoleRepository roleRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;
    private final EmailService emailService;

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
    @Transactional
    public AuthUser registerUser(String email, Long roleId, String fullname) {

        if (userRepo.existsByEmail(email)) {
            throw new BadRequestException("Email already registered");
        }

        String tempPassword = generateRandomPassword(8);
        String hashedPassword = passwordEncoder.encode(tempPassword);

        // Fetch role
        AuthRole role = roleRepo.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

        // Create user and assign role directly
        AuthUser user = AuthUser.builder()
                .email(email)
                .password(hashedPassword)
                .enabled(true)
                .accountNonExpired(true)
                .credentialsNonExpired(true)
                .accountNonLocked(true)
                .roles(Set.of(role))
                .build();

        userRepo.save(user);

        emailService.sendAccountOnboardingEmail(user.getEmail(), fullname, tempPassword);
        return user;
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
