package co.zw.blexta.checkmate.auth.permissions;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PermissionSeeder implements CommandLineRunner {

    private final AuthPermissionRepository permissionRepo;

    @Override
    public void run(String... args) throws Exception {
//
//
//        var resources = List.of(
//                new PermissionData("gadgets", List.of("create", "read", "update", "delete", "assign", "checkin", "checkout")),
//                new PermissionData("users", List.of("create", "read", "update", "delete")),
//                new PermissionData("events", List.of("create", "read", "update", "delete", "assign")),
//                new PermissionData("roles", List.of("create", "read", "update", "delete")),
//                new PermissionData("categories", List.of("create", "read", "update", "delete")),
//                new PermissionData("dashboard", List.of("view_all_stats", "view_team_stats", "view_personal_stats"))
//        );
//
//        for (var r : resources) {
//            for (var action : r.actions()) {
//                boolean exists = permissionRepo.existsByResourceAndAction(r.resource(), action);
//                if (!exists) {
//                    AuthPermission perm = AuthPermission.builder()
//                            .resource(r.resource())
//                            .action(action)
//                            .build();
//                    permissionRepo.save(perm);
//                }
//            }
//        }
//
//        System.out.println("Permissions seeding completed!");
    }

    private record PermissionData(String resource, List<String> actions) {}
}
