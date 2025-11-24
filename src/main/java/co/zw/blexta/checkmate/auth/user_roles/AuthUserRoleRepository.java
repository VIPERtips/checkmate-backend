package co.zw.blexta.checkmate.auth.user_roles;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface AuthUserRoleRepository extends JpaRepository<AuthUserRole, Long> {
    List<AuthUserRole> findByUserId(Long userId);
}
