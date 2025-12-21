package co.zw.blexta.checkmate.payment.intent;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import co.zw.blexta.checkmate.company.Company;
import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "payment_intents")
public class PaymentIntent {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String clientReference;

	@Column(nullable = false)
	private Long amount;

	@Column(nullable = false)
	private String currency;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private PaymentIntentStatus status;

	@ManyToOne(optional = false)
	@JoinColumn(name = "company_id")
	private Company createdBy;

	private String payMeUrl;

	@CreationTimestamp
	private LocalDateTime createdAt;
}
