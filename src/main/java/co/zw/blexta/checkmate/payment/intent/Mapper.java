package co.zw.blexta.checkmate.payment.intent;

import co.zw.blexta.checkmate.common.dto.CompanyResponseDto;
import co.zw.blexta.checkmate.company.Company;

public class Mapper {

    public static CompanyResponseDto toCompanyResponseDto(Company company) {
        return CompanyResponseDto.builder()
                .id(company.getId())
                .name(company.getName())
                .status(company.getStatus().name())
                .plan(company.getPlan() != null ? company.getPlan().name() : null)
                .build();
    }
}
