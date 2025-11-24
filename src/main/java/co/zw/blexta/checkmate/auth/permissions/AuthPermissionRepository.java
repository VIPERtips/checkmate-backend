package co.zw.blexta.checkmate.auth.permissions;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthPermissionRepository extends JpaRepository<AuthPermission, Long> {
    boolean existsByResourceAndAction(String resource, String action);
}
