package co.zw.blexta.checkmate.device.category;

import co.zw.blexta.checkmate.common.dto.CategoryDto;
import co.zw.blexta.checkmate.common.dto.CreateCategoryDto;
import co.zw.blexta.checkmate.common.exception.BadRequestException;
import co.zw.blexta.checkmate.common.exception.ResourceNotFoundException;
import co.zw.blexta.checkmate.staff_users.User;
import co.zw.blexta.checkmate.company.Company;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepo;

    @Override
    public CategoryDto createCategory(CreateCategoryDto dto, User creator) {
        Company company = creator.getCompany();

        if (categoryRepo.existsByNameAndCompany(dto.name(), company)) {
            throw new BadRequestException("Category name already exists");
        }
        if (categoryRepo.existsByCodeAndCompany(dto.code(), company)) {
            throw new BadRequestException("Category code already exists");
        }

        Category category = Category.builder()
                .name(dto.name())
                .code(dto.code())
                .company(company)
                .createdBy(creator)
                .scope(company == null ? CategoryScope.PLATFORM : CategoryScope.COMPANY)
                .build();

        categoryRepo.save(category);
        return toDto(category);
    }

    @Override
    public CategoryDto getCategory(Long id) {
        Category category = categoryRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        return toDto(category);
    }

    @Override
    public List<CategoryDto> getAllCategories(User user) {
        Company company = user.getCompany();
        List<Category> categories;

        if (company == null) { 
            categories = categoryRepo.findAll();
        } else {
            categories = categoryRepo.findAllByCompany(company);
        }

        return categories.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto updateCategory(Long id, CreateCategoryDto dto) {
        Category category = categoryRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        if (!category.getName().equals(dto.name()) && categoryRepo.existsByNameAndCompany(dto.name(), category.getCompany())) {
            throw new BadRequestException("Category name already exists");
        }
        if (!category.getCode().equals(dto.code()) && categoryRepo.existsByCodeAndCompany(dto.code(), category.getCompany())) {
            throw new BadRequestException("Category code already exists");
        }

        category.setName(dto.name());
        category.setCode(dto.code());

        categoryRepo.save(category);
        return toDto(category);
    }

    @Override
    public void deleteCategory(Long id) {
        Category category = categoryRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        if (categoryRepo.countDevices(category.getId()) > 0) {
            throw new BadRequestException("Cannot delete category with devices");
        }

        categoryRepo.delete(category);
    }

    private CategoryDto toDto(Category category) {
        Long deviceCount = categoryRepo.countDevices(category.getId());
        Long companyId = category.getCompany() != null ? category.getCompany().getId() : null;
        String companyName = category.getCompany() != null ? category.getCompany().getName() : "Platform";

        return new CategoryDto(
                category.getId(),
                category.getName(),
                category.getCode(),
                deviceCount,
                companyId,
                companyName
        );
    }

}
