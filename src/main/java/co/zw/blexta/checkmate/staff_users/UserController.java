package co.zw.blexta.checkmate.staff_users;

import co.zw.blexta.checkmate.common.dto.RegisterUserDto;
import co.zw.blexta.checkmate.common.dto.UpdateUserDto;
import co.zw.blexta.checkmate.common.dto.UserDto;
import co.zw.blexta.checkmate.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDto>> getById(@PathVariable Long id) {
        User user = userService.findById(id);
        UserDto dto = userService.mapToDto(user); // add this mapper to service
        ApiResponse<UserDto> response = ApiResponse.<UserDto>builder()
                .message("User fetched successfully")
                .success(true)
                .data(dto)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/email")
    @Hidden
    public ResponseEntity<ApiResponse<UserDto>> getByEmail(@RequestParam String email) {
        User user = userService.getUserByEmail(email);
        UserDto dto = userService.mapToDto(user);
        ApiResponse<UserDto> response = ApiResponse.<UserDto>builder()
                .message("User fetched successfully")
                .success(true)
                .data(dto)
                .build();
        return ResponseEntity.ok(response);
    }


    @GetMapping
    public ResponseEntity<ApiResponse<List<UserDto>>> getAllUsers() {
        List<UserDto> users = userService.getAllUsers();
        ApiResponse<List<UserDto>> response = ApiResponse.<List<UserDto>>builder()
                .message("Users fetched successfully")
                .success(true)
                .data(users)
                .build();
        return ResponseEntity.ok(response);
    }




    @PostMapping
    public ApiResponse<String> createUser(@RequestBody RegisterUserDto dto) {
        return userService.createUser(dto);
    }

    @PutMapping("/{id}")
    public ApiResponse<UpdateUserDto> updateUser(@RequestBody UpdateUserDto dto, @PathVariable Long id) {
        return userService.updateUser(dto, id);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteUser(@PathVariable Long id) {
        return userService.deleteUserById(id);
    }
}
