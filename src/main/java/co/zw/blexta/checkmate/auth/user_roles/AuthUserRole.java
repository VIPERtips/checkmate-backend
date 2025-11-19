package co.zw.blexta.checkmate.auth.user_roles;

import co.zw.blexta.checkmate.auth.role.AuthRole;
import co.zw.blexta.checkmate.auth.users.AuthUser;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "auth_user_roles")
public class AuthUserRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private AuthUser user;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private AuthRole role;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
