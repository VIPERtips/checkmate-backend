package co.zw.blexta.checkmate.staff_users;

import co.zw.blexta.checkmate.auth.users.AuthUser;
import co.zw.blexta.checkmate.auth.users.AuthUserService;
import co.zw.blexta.checkmate.common.dto.RegisterUserDto;
import co.zw.blexta.checkmate.common.dto.UpdateUserDto;
import co.zw.blexta.checkmate.common.dto.UserDto;
import co.zw.blexta.checkmate.common.exception.BadRequestException;
import co.zw.blexta.checkmate.common.exception.ConflictException;
import co.zw.blexta.checkmate.common.exception.ResourceNotFoundException;
import co.zw.blexta.checkmate.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final AuthUserService authUserService;
    private final UserRepository userRepo;

    @Override
    public User findById(Long id) {
        return userRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email " + email));
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepo.findAll()
                .stream()
                .map(this::mapToDto)
                .toList();
    }


    @Override
    @Transactional
    public ApiResponse<String> createUser(RegisterUserDto dto) {
        if (dto == null) throw new BadRequestException("Register payload is required");

        boolean existsInStaff = userRepo.existsByEmail(dto.email());
        if (existsInStaff) throw new ConflictException("A staff user with that email already exists");

        User user = User.builder()
                .fullName(dto.fullName())
                .email(dto.email())
                .build();

        if (dto.createLogin()) {
            AuthUser authUser = authUserService.registerUser(dto.email(), dto.roleId(), dto.fullName());
            user.setAuthUser(authUser);
        }

        userRepo.save(user);

        return ApiResponse.<String>builder()
                .message("User created successfully")
                .success(true)
                .data(null)
                .build();
    }


    @Override
    @Transactional
    public ApiResponse<UpdateUserDto> updateUser(UpdateUserDto dto, Long id) {
        if (dto == null) throw new BadRequestException("Update payload is required");

        User user = userRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));

        if (dto.fullName() != null) user.setFullName(dto.fullName());
        if (dto.email() != null) {
            if (userRepo.existsByEmail(dto.email())) {
                throw new ConflictException("Another user with that email already exists");
            }
            user.setEmail(dto.email());
        }

        userRepo.save(user);

        return ApiResponse.<UpdateUserDto>builder()
                .message("User updated successfully")
                .success(true)
                .data(dto)
                .build();
    }

    @Override
    @Transactional
    public ApiResponse<String> deleteUserById(Long id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));

        userRepo.delete(user);

        return ApiResponse.<String>builder()
                .message("User deleted successfully")
                .success(true)
                .data(null)
                .build();
    }

    public UserDto mapToDto(User user) {
        String roleName = null;
        if (user.getAuthUser() != null && user.getAuthUser().getRoles() != null && !user.getAuthUser().getRoles().isEmpty()) {

            roleName = user.getAuthUser().getRoles().iterator().next().getName();
        }

        return UserDto.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(roleName)
                .createdAt(user.getCreatedAt())
                .build();
    }


}
