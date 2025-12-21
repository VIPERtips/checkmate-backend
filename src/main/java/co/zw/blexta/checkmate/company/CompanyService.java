package co.zw.blexta.checkmate.company;



import java.util.List;

import co.zw.blexta.checkmate.common.dto.CompanyRequestDto;
import co.zw.blexta.checkmate.common.dto.CompanyResponseDto;

public interface CompanyService {
    CompanyResponseDto createCompany(CompanyRequestDto request,Long userId);
    CompanyResponseDto addCompanyBySuperAdmin(CompanyRequestDto request, Long userId);
    CompanyResponseDto updateCompany(Long companyId, CompanyRequestDto request);
    CompanyResponseDto getCompany(Long companyId);
    List<CompanyResponseDto> getAllCompanies();
    void deleteCompany(Long companyId);
}
