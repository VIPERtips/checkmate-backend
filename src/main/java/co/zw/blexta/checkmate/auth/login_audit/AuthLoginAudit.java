package co.zw.blexta.checkmate.auth.login_audit;

import co.zw.blexta.checkmate.auth.users.AuthUser;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "auth_login_audit")
public class AuthLoginAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private AuthUser user;

    private String emailAttempted;
    private boolean isLoggedIn;
    private String ipAddress;
    private String userAgent;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
