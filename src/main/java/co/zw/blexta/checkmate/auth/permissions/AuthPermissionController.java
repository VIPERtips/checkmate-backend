package co.zw.blexta.checkmate.auth.permissions;

import co.zw.blexta.checkmate.common.dto.AuthPermissionDTO;
import co.zw.blexta.checkmate.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/permissions")
public class AuthPermissionController {

    private final AuthPermissionService service;

    @PostMapping
    public ApiResponse<AuthPermissionDTO> create(
            @RequestParam String resource,
            @RequestParam String action
    ) {
        return service.createPermission(resource, action);
    }

    @GetMapping
    public ApiResponse<List<AuthPermissionDTO>> all() {
        return service.getAllPermissions();
    }

    @GetMapping("/{id}")
    public ApiResponse<AuthPermissionDTO> get(@PathVariable Long id) {
        return service.getPermission(id);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> delete(@PathVariable Long id) {
        return service.deletePermission(id);
    }
}
