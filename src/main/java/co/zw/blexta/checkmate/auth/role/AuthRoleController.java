package co.zw.blexta.checkmate.auth.role;

import co.zw.blexta.checkmate.common.dto.CreateRoleDto;
import co.zw.blexta.checkmate.common.dto.UpdateRoleDto;
import co.zw.blexta.checkmate.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class AuthRoleController {

    private final AuthRoleService roleService;

    @PostMapping
    public ApiResponse<AuthRole> create(@RequestBody CreateRoleDto dto) {
        return roleService.createRole(dto);
    }

    @PutMapping
    public ApiResponse<AuthRole> update(@RequestBody UpdateRoleDto dto) {
        return roleService.updateRole(dto);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> delete(@PathVariable Long id) {
        return roleService.deleteRole(id);
    }

    @GetMapping("/{id}")
    public ApiResponse<AuthRole> getOne(@PathVariable Long id) {
        return roleService.getRole(id);
    }

    @GetMapping
    public ApiResponse<List<AuthRole>> getAll() {
        return roleService.getAllRoles();
    }
}
