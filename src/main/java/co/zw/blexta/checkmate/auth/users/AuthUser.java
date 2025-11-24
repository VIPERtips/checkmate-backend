package co.zw.blexta.checkmate.auth.users;

import co.zw.blexta.checkmate.auth.login_audit.AuthLoginAudit;
import co.zw.blexta.checkmate.auth.user_roles.AuthUserRole;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "auth_users")
public class AuthUser implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;
    private boolean enabled = true;
    private boolean accountNonLocked = true;
    private boolean credentialsNonExpired = true;
    private boolean accountNonExpired = true;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<AuthUserRole> roles;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<AuthLoginAudit> loginAudits;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(r -> (GrantedAuthority) () -> "ROLE_" + r.getRole().getName().toUpperCase())
                .toList();
    }

    @Override
    public String getUsername() {
        return this.email;
    }
}
