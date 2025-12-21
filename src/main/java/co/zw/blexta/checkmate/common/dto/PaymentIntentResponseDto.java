package co.zw.blexta.checkmate.common.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentIntentResponseDto {
    private String clientReference;
    private String currency;
    private String paymeURL;
}
