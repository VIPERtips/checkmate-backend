package co.zw.blexta.checkmate.auth.role;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AuthRoleRepository extends JpaRepository<AuthRole,Long> {
    boolean existsByName(String name);

    Optional<AuthRole> findByName(String roleName);
    @Query("SELECT COUNT(u) FROM AuthUser u JOIN u.roles r WHERE r.id = :roleId")
    Long countUsers(Long roleId);
}
