package co.zw.blexta.checkmate.auth.role;


import co.zw.blexta.checkmate.auth.role_permissions.AuthRolePermission;
import co.zw.blexta.checkmate.auth.user_roles.AuthUserRole;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name="auth_roles")
public class AuthRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    private String description;

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<AuthUserRole> userRoles;

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<AuthRolePermission> permissions;
}
