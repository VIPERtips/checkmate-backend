package co.zw.blexta.checkmate.deviceCategories;

import co.zw.blexta.checkmate.common.dto.CategoryDto;
import co.zw.blexta.checkmate.common.dto.CreateCategoryDto;
import co.zw.blexta.checkmate.common.exception.BadRequestException;
import co.zw.blexta.checkmate.common.exception.ResourceNotFoundException;

import java.util.List;

public interface CategoryService {
    CategoryDto createCategory(CreateCategoryDto dto);
    CategoryDto getCategory(Long id);
    List<CategoryDto> getAllCategories();
    CategoryDto updateCategory(Long id, CreateCategoryDto dto);
    void deleteCategory(Long id);
}
