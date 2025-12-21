package co.zw.blexta.checkmate.common.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CompanyResponseDto {
    private Long id;
    private String name;
    private String status;
    private String plan;
    private String email;
    private String address;
    private String phoneNumber;
}