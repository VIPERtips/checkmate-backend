package co.zw.blexta.checkmate.auth.users;

import co.zw.blexta.checkmate.auth.login_audit.AuthLoginAuditService;
import co.zw.blexta.checkmate.auth.role.AuthRole;
import co.zw.blexta.checkmate.auth.role.AuthRoleRepository;
import co.zw.blexta.checkmate.common.dto.ChangePasswordDto;
import co.zw.blexta.checkmate.common.dto.CreateLoginAuditDto;
import co.zw.blexta.checkmate.common.dto.LoginDto;
import co.zw.blexta.checkmate.common.exception.BadRequestException;
import co.zw.blexta.checkmate.common.exception.ResourceNotFoundException;
import co.zw.blexta.checkmate.common.response.ApiResponse;
import co.zw.blexta.checkmate.common.utils.IpUtil;
import co.zw.blexta.checkmate.notification.EmailService;
import co.zw.blexta.checkmate.security.JwtService;
import co.zw.blexta.checkmate.staff_users.User;
import co.zw.blexta.checkmate.staff_users.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Date;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthUserServiceImpl implements AuthUserService {

    private final AuthUserRepository userRepo;
    private final AuthRoleRepository roleRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;
    private final EmailService emailService;
    private final JwtService jwtService;
    private final AuthLoginAuditService authLoginAuditService;
    private final UserRepository staffUserRepo;
    private final UserRepository staffRepo;

    @Override
    public ApiResponse<?> authenticate(LoginDto loginDto) {

        boolean loginSuccess = false;
        AuthUser user = null;
        String accessToken = null;
        String refreshToken = null;

        try {
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDto.email(),
                            loginDto.password()
                    )
            );

            user = userRepo.findByEmail(loginDto.email())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            User staffUser = null;
            String fullName = null;
            if (user.getId() != null) {
                staffUser = staffUserRepo.findByAuthUserId(user.getId());
                if (staffUser != null) fullName = staffUser.getFullName();
            }
            
            
            boolean isFirstLogin = (user.getLastLoginAt() == null);
            if(isFirstLogin) {
            	user.setLastLoginAt(new Date());
            	userRepo.save(user);
            }

            Map<String, Object> sessionData = Map.of(
                    "userId", user.getId(),
                    "email", user.getEmail(),
                    "name", fullName,
                    "roles", user.getRoles().stream()
                            .map(r -> r.getName().toLowerCase())
                            .toList()
            );

            accessToken = jwtService.generateAccessToken(user, sessionData);
            refreshToken = jwtService.generateRefreshToken(user);

            loginSuccess = true;

            return ApiResponse.builder()
                    .success(true)
                    .message("Login successful")
                    .data(Map.of(
                            "accessToken", accessToken,
                            "refreshToken", refreshToken,
                            "user", sessionData
                            ,"isFirstLogin", isFirstLogin
                    ))
                    .build();

        } finally {
            String emailToLog = (user != null) ? user.getEmail() : loginDto.email();
            Long userIdToLog = (user != null) ? user.getId() : null;

            CreateLoginAuditDto auditDto = CreateLoginAuditDto.builder()
                    .userId(userIdToLog)
                    .emailAttempted(emailToLog)
                    .loggedIn(loginSuccess)
                    .ipAddress(IpUtil.getClientIp())
                    .userAgent(loginDto.userAgent())
                    .build();

            authLoginAuditService.saveAudit(auditDto);
        }
    }

    @Override
    public ApiResponse<?> logout(String accessToken) {
        AuthUser user = userRepo.findById(Long.parseLong(jwtService.extractSubject(accessToken)))
                .orElse(null);

        if (user != null) {
            user.setLastLogoutAt(new Date());
            userRepo.save(user);
        }

        return ApiResponse.builder()
                .success(true)
                .message("Logged out, token invalidated")
                .build();
    }

    @Override
    public ApiResponse<?> getCurrentSession(String accessToken) {

        AuthUser user = userRepo.findById(Long.parseLong(jwtService.extractSubject(accessToken)))
                .orElse(null);

        if (user == null || !jwtService.isTokenValid(accessToken, null, user.getLastLogoutAt())) {
            return ApiResponse.builder()
                    .success(false)
                    .message("Session expired or invalid")
                    .data(null)
                    .build();
        }

        var claims = jwtService.extractAllClaims(accessToken);
        Map<String, Object> sessionData = Map.of(
                "userId", claims.get("userId"),
                "email", claims.get("email"),
                "name", claims.get("name"),
                "roles", claims.get("roles")
        );

        return ApiResponse.builder()
                .success(true)
                .message("Session active")
                .data(Map.of(
                        "accessToken", accessToken,
                        "user", sessionData
                ))
                .build();
    }

    @Override
    public AuthUser getUserById(Long id) {
        return userRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found for :" + id));
    }

    @Override
    @Transactional
    public AuthUser registerUser(String email, Long roleId, String fullname) {

        if (userRepo.existsByEmail(email)) {
            throw new BadRequestException("Email already registered");
        }

        String tempPassword = generateRandomPassword(8);
        String hashedPassword = passwordEncoder.encode(tempPassword);

        AuthRole role = roleRepo.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

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
        System.out.println("The generated password is: "+tempPassword);
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

	@Override
	public ApiResponse<?> changePassword(Long userId, ChangePasswordDto dto) {
		AuthUser user = userRepo.findById(userId)
				.orElseThrow(()-> new ResourceNotFoundException("User not found"));
		
		User staffUser = staffRepo.findByAuthUserId(userId);
		
		
		if(!passwordEncoder.matches(dto.oldPassword(), user.getPassword())) {
			throw new BadRequestException("Old password is incorrect");
		}
		
		if(!dto.newPassword().equals(dto.confirmPassword())) {
			throw new BadRequestException("New passwords do not match");
		}
		
		String hashedNew = passwordEncoder.encode(dto.newPassword());
		user.setPassword(hashedNew);
		
		userRepo.save(user);
		
		emailService.sendPasswordChangedNotification(user.getEmail(), staffUser.getFullName());
		
		  return ApiResponse.builder()
	                .success(true)
	                .message("Password updated successfully")
	                .data(null)
	                .build();
	}
}
