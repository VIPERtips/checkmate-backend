package co.zw.blexta.checkmate.payment.intent;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentIntentRepository extends JpaRepository<PaymentIntent, Long> {

	Optional<PaymentIntent> findByClientReference(String clientReference);

}
