package co.zw.blexta.checkmate.auth.login_audit;



import co.zw.blexta.checkmate.common.dto.CreateLoginAuditDto;
import co.zw.blexta.checkmate.common.dto.LoginAuditDto;

import java.util.List;

public interface AuthLoginAuditService {
    LoginAuditDto saveAudit(CreateLoginAuditDto dto);
    List<LoginAuditDto> getAllAudits();
    List<LoginAuditDto> getAuditsByUserId(Long userId);
    List<LoginAuditDto> getAuditsByEmail(String email);
}
