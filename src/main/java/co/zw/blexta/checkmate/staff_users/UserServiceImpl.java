package co.zw.blexta.checkmate.staff_users;

import co.zw.blexta.checkmate.auth.users.AuthUser;
import co.zw.blexta.checkmate.auth.users.AuthUserRepository;
import co.zw.blexta.checkmate.auth.users.AuthUserService;
import co.zw.blexta.checkmate.common.dto.RegisterUserDto;
import co.zw.blexta.checkmate.common.dto.UpdateUserDto;
import co.zw.blexta.checkmate.common.dto.UserDto;
import co.zw.blexta.checkmate.common.exception.BadRequestException;
import co.zw.blexta.checkmate.common.exception.ConflictException;
import co.zw.blexta.checkmate.common.exception.ResourceNotFoundException;
import co.zw.blexta.checkmate.common.response.ApiResponse;
import co.zw.blexta.checkmate.notification.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final AuthUserService authUserService;
	private final UserRepository userRepo;
	private final AuthUserRepository authUserRepository;
	private final EmailService emailService;

	@Override
	public User findById(Long id) {
		return userRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
	}

	@Override
	public User getUserByEmail(String email) {
		return userRepo.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("User not found with email " + email));
	}

	public List<UserDto> getUsersByStatus(String status) {

	    List<User> users;

	    switch (status.toLowerCase()) {
	        case "inactive":
	            users = userRepo.findByActive(false);
	            break;

	        case "all":
	            users = userRepo.findAll();
	            break;

	        default:
	            users = userRepo.findByActive(true);
	            break;
	    }

	    return users.stream()
	            .map(this::mapToDto)
	            .toList();
	}


	@Override
	@Transactional
	public ApiResponse<String> createUser(RegisterUserDto dto) {
		if (dto == null)
			throw new BadRequestException("Register payload is required");

		User existing = userRepo.findByEmail(dto.email()).orElse(null);

		if (existing != null && existing.isActive()) {
			throw new ConflictException("A staff user with that email already exists");
		}


		User user;
		if (existing != null && !existing.isActive()) {
			user = existing;
			user.setActive(true);
			user.setFullName(dto.fullName());
			user.setEmail(dto.email());
		} else {
			user = User.builder().fullName(dto.fullName()).email(dto.email()).build();
		}

		if (dto.createLogin()) {
			AuthUser authUser;

			if (authUserRepository.existsByEmail(dto.email())) {
				authUser = authUserRepository.findByEmail(dto.email()).orElseThrow();

				if (!authUser.isEnabled()) {
					authUser.setEnabled(true);
					authUserService.resetPasswordForUser(authUser, dto.fullName());
					authUserRepository.save(authUser);
				}

			} else {
				authUser = authUserService.registerUser(dto.email(), dto.roleId(), dto.fullName());
			}

			user.setAuthUser(authUser);
		}

		userRepo.save(user);

		return ApiResponse.<String>builder().message("User created successfully").success(true).data(null).build();
	}

	@Override
	@Transactional
	public ApiResponse<UpdateUserDto> updateUser(UpdateUserDto dto, Long id) {
		if (dto == null)
			throw new BadRequestException("Update payload is required");

		User existingUser = userRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));

		String oldEmail = existingUser.getEmail();
		String oldFullName = existingUser.getFullName();

		if (dto.fullName() != null)
			existingUser.setFullName(dto.fullName());

		if (dto.email() != null && !dto.email().equalsIgnoreCase(existingUser.getEmail())) {
			if (userRepo.existsByEmail(dto.email())) {
				throw new ConflictException("Another user with that email exists");
			}

			existingUser.setEmail(dto.email());

			AuthUser authUser = existingUser.getAuthUser();
			if (authUser != null) {
				authUser.setEmail(dto.email());
				authUserRepository.save(authUser);

				emailService.sendAccountUpdateNotification(dto.email(), oldEmail, existingUser.getFullName());
			}
		}

		if (!oldFullName.equals(existingUser.getFullName())) {
			AuthUser authUser = existingUser.getAuthUser();
			if (authUser != null) {
				emailService.sendAccountUpdateNotification(existingUser.getEmail(), existingUser.getEmail(),
						existingUser.getFullName());
			}
		}

		userRepo.save(existingUser);

		return ApiResponse.<UpdateUserDto>builder().message("User updated successfully").success(true).data(dto)
				.build();
	}

	@Override
	@Transactional
	public ApiResponse<String> deleteUserById(Long id) {
		User user = userRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
		AuthUser authUser = user.getAuthUser();
		if (authUser != null) {
			authUser.setEnabled(false);
			authUserRepository.save(authUser);
			emailService.sendAccountDeactivationEmail(authUser.getEmail(), user.getFullName());
		}
		user.setActive(false);
		userRepo.save(user);

		return ApiResponse.<String>builder().message("User deleted successfully").success(true).data(null).build();
	}

	public UserDto mapToDto(User user) {

		List<String> roles = List.of();

		if (user.getAuthUser() != null && user.getAuthUser().getRoles() != null) {
			roles = user.getAuthUser().getRoles()
					.stream()
					.map(r -> r.getName())
					.toList();
		}

		return UserDto.builder()
				.id(user.getId())
				.fullName(user.getFullName())
				.email(user.getEmail())
				.roles(roles)
				.createdAt(user.getCreatedAt())
				.status(user.isActive() ? "active" : "inactive")
				.build();
	}


}
