package co.zw.blexta.checkmate.auth.login_audit;

import co.zw.blexta.checkmate.auth.users.AuthUser;
import co.zw.blexta.checkmate.common.dto.CreateLoginAuditDto;
import co.zw.blexta.checkmate.common.dto.LoginAuditDto;
import co.zw.blexta.checkmate.auth.users.AuthUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthLoginAuditServiceImpl implements AuthLoginAuditService {

    private final AuthLoginAuditRepository repo;
    private final AuthUserRepository userRepo;

    @Override
    public LoginAuditDto saveAudit(CreateLoginAuditDto dto) {
        AuthUser user = null;
        if (dto.getUserId() != null) {
            user = userRepo.findById(dto.getUserId()).orElse(null);
        }

        AuthLoginAudit audit = AuthLoginAudit.builder()
                .user(user)
                .emailAttempted(dto.getEmailAttempted())
                .isLoggedIn(dto.isLoggedIn())
                .ipAddress(dto.getIpAddress())
                .userAgent(dto.getUserAgent())
                .build();

        AuthLoginAudit saved = repo.save(audit);

        return map(saved);
    }

    @Override
    public List<LoginAuditDto> getAllAudits() {
        return repo.findAll()
                .stream()
                .map(this::map)
                .toList();
    }

    @Override
    public List<LoginAuditDto> getAuditsByUserId(Long userId) {
        return repo.findAll()
                .stream()
                .filter(a -> a.getUser() != null && a.getUser().getId().equals(userId))
                .map(this::map)
                .toList();
    }

    @Override
    public List<LoginAuditDto> getAuditsByEmail(String email) {
        return repo.findAll()
                .stream()
                .filter(a -> a.getEmailAttempted().equalsIgnoreCase(email))
                .map(this::map)
                .toList();
    }

    private LoginAuditDto map(AuthLoginAudit entity) {
        return new LoginAuditDto(
                entity.getId(),
                entity.getUser() != null ? entity.getUser().getId() : null,
                entity.getEmailAttempted(),
                entity.isLoggedIn(),
                entity.getIpAddress(),
                entity.getUserAgent(),
                entity.getCreatedAt()
        );
    }
}
