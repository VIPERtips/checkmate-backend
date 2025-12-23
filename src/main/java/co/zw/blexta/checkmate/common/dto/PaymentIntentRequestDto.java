package co.zw.blexta.checkmate.common.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentIntentRequestDto {
    private Long companyId;
    private double amount;
    private String currency;
    private String planId;
}

