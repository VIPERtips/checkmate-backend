package co.zw.blexta.checkmate.company;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import co.zw.blexta.checkmate.common.dto.CompanyRequestDto;
import co.zw.blexta.checkmate.common.dto.CompanyResponseDto;
import co.zw.blexta.checkmate.common.exception.ConflictException;
import co.zw.blexta.checkmate.common.exception.ResourceNotFoundException;
import co.zw.blexta.checkmate.staff_users.User;
import co.zw.blexta.checkmate.staff_users.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    @Override
public CompanyResponseDto addCompanyBySuperAdmin(CompanyRequestDto request, Long userId) {

    if (companyRepository.existsByName(request.name())) {
        throw new ConflictException("Company already exists by name: " + request.name());
    }

    User companyAdmin = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Staff user not found"));

    boolean isAdmin = companyAdmin.getAuthUser()
            .getRoles()
            .stream()
            .anyMatch(r -> r.getName().equals("ADMIN"));

    if (!isAdmin) {
        throw new ConflictException("User is not an admin and cannot be assigned a company");
    }

    Company company = Company.builder()
            .name(request.name())
            .email(request.email())
            .phoneNumber(request.phoneNumber())
            .address(request.address())
            .status(CompanyStatus.TRIAL)
            .plan(PlanType.FREE)
            .build();

    companyRepository.save(company);

    companyAdmin.setCompany(company);
    userRepository.save(companyAdmin);

    return mapToDto(company);
}


    @Override
    public CompanyResponseDto createCompany(CompanyRequestDto request, Long userId) {
        boolean exists = companyRepository.existsByName(request.name());
        if (exists) {
            throw new ConflictException("Company already exists by name: " + request.name());
        }
        User  creator = userRepository.findById(userId)
        		.orElseThrow(
        				()-> new ResourceNotFoundException("Staff user not found"));
        
        
        Company company = Company.builder()
                .name(request.name())
                .email(request.email())
                .phoneNumber(request.phoneNumber())
                .address(request.address())
                .status(CompanyStatus.PENDING_SETUP)
                .plan(PlanType.FREE) 
                .build();
        
        if (!creator.getAuthUser().getRoles().stream().noneMatch(r -> r.getName().equals("SUPERADMIN"))) {
            creator.setCompany(company);
            userRepository.save(creator);
        }

        companyRepository.save(company);
        return mapToDto(company);
    }

    @Override
    public CompanyResponseDto updateCompany(Long companyId, CompanyRequestDto request) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));

        company.setName(request.name());
        company.setEmail(request.email());
        company.setPhoneNumber(request.phoneNumber());
        company.setAddress(request.address());

        companyRepository.save(company);
        return mapToDto(company);
    }

    @Override
    public CompanyResponseDto getCompany(Long companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));
        return mapToDto(company);
    }

    @Override
    public List<CompanyResponseDto> getAllCompanies() {
        return companyRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteCompany(Long companyId) {
        companyRepository.deleteById(companyId);
    }

    private CompanyResponseDto mapToDto(Company company) {
        return CompanyResponseDto.builder()
                .id(company.getId())
                .name(company.getName())
                .email(company.getEmail())
                .phoneNumber(company.getPhoneNumber())
                .address(company.getAddress())
                .status(company.getStatus().name())
                .plan(company.getPlan() != null ? company.getPlan().name() : null)
                .build();
    }
}
