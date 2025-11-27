package co.zw.blexta.checkmate.deviceCategories;

import co.zw.blexta.checkmate.common.dto.CategoryDto;
import co.zw.blexta.checkmate.common.dto.CreateCategoryDto;
import co.zw.blexta.checkmate.common.exception.BadRequestException;
import co.zw.blexta.checkmate.common.exception.ResourceNotFoundException;
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
    public CategoryDto createCategory(CreateCategoryDto dto) {
        if (categoryRepo.existsByName(dto.name())) {
            throw new BadRequestException("Category name already exists");
        }
        if (categoryRepo.existsByCode(dto.code())) {
            throw new BadRequestException("Category code already exists");
        }

        Category category = Category.builder()
                .name(dto.name())
                .code(dto.code())
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
    public List<CategoryDto> getAllCategories() {
        return categoryRepo.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto updateCategory(Long id, CreateCategoryDto dto) {
        Category category = categoryRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        if (!category.getName().equals(dto.name()) && categoryRepo.existsByName(dto.name())) {
            throw new BadRequestException("Category name already exists");
        }
        if (!category.getCode().equals(dto.code()) && categoryRepo.existsByCode(dto.code())) {
            throw new BadRequestException("Category code already exists");
        }

        category.setName(dto.name());
        category.setCode(dto.code());

        categoryRepo.save(category);
        return toDto(category);
    }

    @Override
    public void deleteCategory(Long id) {
        if (!categoryRepo.existsById(id)) {
            throw new ResourceNotFoundException("Category not found");
        }
        categoryRepo.deleteById(id);
    }

    private CategoryDto toDto(Category category) {
        Long deviceCount = categoryRepo.countDevices(category.getId());
        return new CategoryDto(
                category.getId(),
                category.getName(),
                category.getCode(),
                deviceCount
        );
    }
}
