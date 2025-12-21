package co.zw.blexta.checkmate.payment.intent;

import co.zw.blexta.checkmate.common.dto.PaymentIntentRequestDto;
import co.zw.blexta.checkmate.common.dto.PaymentIntentResponseDto;

public interface PaymentIntentService {
    PaymentIntentResponseDto createPaymentIntent(PaymentIntentRequestDto request) throws Exception;
    PaymentIntentStatus checkPaymentStatus(String clientReference) throws Exception;
}
