package co.zw.blexta.checkmate.company;

import co.zw.blexta.checkmate.common.dto.CompanyRequestDto;
import co.zw.blexta.checkmate.common.dto.CompanyResponseDto;
import co.zw.blexta.checkmate.common.response.ApiResponse;
import co.zw.blexta.checkmate.common.utils.SessionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;
    private final SessionUtil sessionUtil;

    @PostMapping("/superadmin/user")
    public ApiResponse<CompanyResponseDto> createCompanyBySuperAdmin(@RequestBody CompanyRequestDto request,@RequestParam Long userId) {
        CompanyResponseDto response = companyService.addCompanyBySuperAdmin(request, userId);
        return ApiResponse.<CompanyResponseDto>builder()
                .success(true)
                .message("Company created successfully by SuperAdmin")
                .data(response)
                .build();
    }

    @PostMapping("/register")
    public ApiResponse<CompanyResponseDto> createCompany(@RequestBody CompanyRequestDto request,@RequestHeader("Authorization") String authHeader) {
    Long userId = sessionUtil.extractUserId(authHeader);
        CompanyResponseDto response = companyService.createCompany(request, userId);
        return ApiResponse.<CompanyResponseDto>builder()
                .success(true)
                .message("Company registration successful. Complete setup to activate")
                .data(response)
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<CompanyResponseDto> getCompany(@PathVariable Long id) {
        CompanyResponseDto response = companyService.getCompany(id);
        return ApiResponse.<CompanyResponseDto>builder()
                .success(true)
                .message("Company retrieved successfully")
                .data(response)
                .build();
    }

    @GetMapping("/all")
    public ApiResponse<List<CompanyResponseDto>> getAllCompanies() {
        List<CompanyResponseDto> response = companyService.getAllCompanies();
        return ApiResponse.<List<CompanyResponseDto>>builder()
                .success(true)
                .message("Companies retrieved successfully")
                .data(response)
                .build();
    }
    
    @GetMapping("/mine")
    public ApiResponse<CompanyResponseDto> getMyCompany(
            @RequestHeader("Authorization") String authHeader) {

        Long userId = sessionUtil.extractUserId(authHeader);

        CompanyResponseDto response = companyService.getMine(userId);

        return ApiResponse.<CompanyResponseDto>builder()
                .success(true)
                .message("Company retrieved successfully")
                .data(response)
                .build();
    }


    @PutMapping("/{id}")
    public ApiResponse<CompanyResponseDto> updateCompany(@PathVariable Long id, @RequestBody CompanyRequestDto request) {
        CompanyResponseDto response = companyService.updateCompany(id, request);
        return ApiResponse.<CompanyResponseDto>builder()
                .success(true)
                .message("Company updated successfully")
                .data(response)
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteCompany(@PathVariable Long id) {
        companyService.deleteCompany(id);
        return ApiResponse.<Void>builder()
                .success(true)
                .message("Company deleted successfully")
                .build();
    }
}
