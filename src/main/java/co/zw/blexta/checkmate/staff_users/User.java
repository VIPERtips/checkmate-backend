package co.zw.blexta.checkmate.staff_users;


import co.zw.blexta.checkmate.assset_code.AssetCode;
import co.zw.blexta.checkmate.auth.users.AuthUser;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "staff_users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fullName;
    @Column(unique = true)
    private String email;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @OneToOne
    @JoinColumn(name = "auth_user_id")
    private AuthUser authUser;

    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AssetCode> assetCodes;
    private boolean active = true;
    
}
