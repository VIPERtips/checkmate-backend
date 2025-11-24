package co.zw.blexta.checkmate.auth.role_permissions;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuthRolePermissionRepository extends JpaRepository<AuthRolePermission, Long> {
    List<AuthRolePermission> findByRoleId(Long roleId);
    boolean existsByRoleIdAndPermissionId(Long roleId, Long permissionId);
}
