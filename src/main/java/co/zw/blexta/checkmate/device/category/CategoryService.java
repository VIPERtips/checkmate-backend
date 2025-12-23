package co.zw.blexta.checkmate.device.category;

import co.zw.blexta.checkmate.common.dto.CategoryDto;
import co.zw.blexta.checkmate.common.dto.CreateCategoryDto;
import co.zw.blexta.checkmate.staff_users.User;

import java.util.List;

public interface CategoryService {

    CategoryDto createCategory(CreateCategoryDto dto, User creator);

    CategoryDto getCategory(Long id);

    List<CategoryDto> getAllCategories(User user);

    CategoryDto updateCategory(Long id, CreateCategoryDto dto);

    void deleteCategory(Long id);
}
