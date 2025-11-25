package co.zw.blexta.checkmate.auth.login_audit;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthLoginAuditRepository extends JpaRepository<AuthLoginAudit,Long> {
}
