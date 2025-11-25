package co.zw.blexta.checkmate.staff_users;

import co.zw.blexta.checkmate.common.dto.RegisterUserDto;
import co.zw.blexta.checkmate.common.dto.UpdateUserDto;
import co.zw.blexta.checkmate.common.dto.UserDto;
import co.zw.blexta.checkmate.common.response.ApiResponse;

import java.util.List;

public interface UserService {
    User findById(Long id);
    User getUserByEmail(String email);
    List<UserDto> getAllUsers();
    UserDto mapToDto(User user);



    ApiResponse<String> createUser(RegisterUserDto dto);
    ApiResponse<UpdateUserDto> updateUser(UpdateUserDto dto, Long id);
    ApiResponse<String> deleteUserById(Long id);
}
