package co.zw.blexta.checkmate.auth.role;

import co.zw.blexta.checkmate.common.dto.CreateRoleDto;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoleSeeder implements CommandLineRunner {

    private final AuthRoleService roleService;

    @Override
    public void run(String... args) {
        seedRole("ADMIN", "Administrator role with full permissions");
        seedRole("IT", "IT role with technical permissions");
    }

    private void seedRole(String name, String description) {
        try {
            CreateRoleDto dto = new CreateRoleDto(name, description);
            roleService.createRole(dto);
            System.out.println("Seeded role: " + name);
        } catch (Exception e) {
            // If role exists, just skip
            if (e.getMessage().contains("Role already exists")) {
                System.out.println("Role already exists, skipping: " + name);
            } else {
                e.printStackTrace();
            }
        }
    }
}
