package co.zw.blexta.checkmate.auth.role;


import co.zw.blexta.checkmate.auth.role_permissions.AuthRolePermission;
import co.zw.blexta.checkmate.auth.role_permissions.AuthRolePermissionRepository;
import co.zw.blexta.checkmate.auth.permissions.AuthPermission;
import co.zw.blexta.checkmate.auth.permissions.AuthPermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RoleAndPermissionSeeder implements CommandLineRunner {

    private final AuthRoleRepository roleRepo;
    private final AuthPermissionRepository permRepo;
    private final AuthRolePermissionRepository rolePermRepo;

    @Override
    public void run(String... args) {

//
//        String roleName = "TechOps"; // cool, professional, sounds important
//        AuthRole techOpsRole = roleRepo.findByName(roleName)
//                .orElseGet(() -> {
//                    AuthRole newRole = AuthRole.builder()
//                            .name(roleName)
//                            .description("Full control over all systems, permissions, and operational workflows")
//                            .build();
//                    return roleRepo.save(newRole);
//                });
//
//
//        List<AuthPermission> allPerms = permRepo.findAll();
//
//
//        for (AuthPermission perm : allPerms) {
//            boolean exists = rolePermRepo.existsByRoleIdAndPermissionId(techOpsRole.getId(), perm.getId());
//            if (!exists) {
//                AuthRolePermission rp = AuthRolePermission.builder()
//                        .role(techOpsRole)
//                        .permission(perm)
//                        .build();
//                rolePermRepo.save(rp);
//            }
//        }
//
//        System.out.println("IT role created/updated with all permissions!");
    }
}
