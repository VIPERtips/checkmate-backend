package co.zw.blexta.checkmate.device;

import co.zw.blexta.checkmate.assset_code.AssetCode;
import co.zw.blexta.checkmate.company.Company;
import co.zw.blexta.checkmate.device.category.Category;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Device {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "asset_code_id", referencedColumnName = "id")
    private AssetCode assetCode;

    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    private Category category;

    private String name;
    private String serialNumber;
    private String status;

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = true)
    private Company company;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
