package co.zw.blexta.checkmate.auth.role;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthRoleRepository extends JpaRepository<AuthRole,Long> {
    boolean existsByName(String name);
}
