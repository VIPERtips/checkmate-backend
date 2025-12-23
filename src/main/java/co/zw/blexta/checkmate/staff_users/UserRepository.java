package co.zw.blexta.checkmate.staff_users;

import org.springframework.data.jpa.repository.JpaRepository;

import co.zw.blexta.checkmate.auth.users.AuthUser;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    User findByAuthUserId(Long id);

    List<User> findByActive(boolean active);

    List<User> findAllByActiveAndCompanyId(boolean active, Long companyId);

	long countByCompanyId(Long companyId);

	Optional<User> findByAuthUser(AuthUser authUser);

}
