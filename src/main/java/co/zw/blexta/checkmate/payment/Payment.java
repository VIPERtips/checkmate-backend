package co.zw.blexta.checkmate.payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import co.zw.blexta.checkmate.company.Company;
import co.zw.blexta.checkmate.payment.intent.PaymentIntent;
import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "payments")
public class Payment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "company_id")
	private Company company;

	@OneToOne(optional = false)
	@JoinColumn(name = "payment_intent_id")
	private PaymentIntent paymentIntent;

	@Column(nullable = false)
	private Long amount;

	@Column(nullable = false)
	private String paymentMethod;

	private LocalDateTime paymentDate;

	@CreationTimestamp
	private LocalDateTime createdAt;
}
