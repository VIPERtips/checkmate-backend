package co.zw.blexta.checkmate.auth.role_permissions;

import co.zw.blexta.checkmate.auth.permissions.AuthPermission;
import co.zw.blexta.checkmate.auth.role.AuthRole;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "auth_role_permissions")
public class AuthRolePermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private AuthRole role;

    @ManyToOne
    @JoinColumn(name = "permission_id")
    @JsonBackReference
    private AuthPermission permission;

}