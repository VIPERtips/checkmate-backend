package co.zw.blexta.checkmate.company;

import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "companies")
public class Company {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;
	private String phoneNumber;
	private String address;
	private String email;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private CompanyStatus status;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private PlanType plan;
}

