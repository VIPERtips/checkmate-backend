package co.zw.blexta.checkmate.payment.intent;

import co.zw.blexta.checkmate.common.dto.PaymentIntentRequestDto;
import co.zw.blexta.checkmate.common.dto.PaymentIntentResponseDto;
import co.zw.blexta.checkmate.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentIntentController {

    private final PaymentIntentService paymentService;

    @PostMapping("/intent")
    public ApiResponse<PaymentIntentResponseDto> createPaymentIntent(@RequestBody PaymentIntentRequestDto request) throws Exception {
        PaymentIntentResponseDto response = paymentService.createPaymentIntent(request);
        return ApiResponse.<PaymentIntentResponseDto>builder()
                .success(true)
                .message("Payment intent created successfully")
                .data(response)
                .build();
    }

    @GetMapping("/intent/{clientReference}/status")
    public ApiResponse<String> checkPaymentStatus(@PathVariable String clientReference) throws Exception {
        PaymentIntentStatus status = paymentService.checkPaymentStatus(clientReference);
        return ApiResponse.<String>builder()
                .success(true)
                .message("Payment status retrieved")
                .data(status.name())
                .build();
    }

}
