package co.zw.blexta.checkmate.deviceCategories;

import co.zw.blexta.checkmate.common.dto.CategoryDto;
import co.zw.blexta.checkmate.common.dto.CreateCategoryDto;
import co.zw.blexta.checkmate.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ApiResponse<CategoryDto> createCategory(@RequestBody CreateCategoryDto dto) {
        CategoryDto category = categoryService.createCategory(dto);
        return ApiResponse.<CategoryDto>builder()
                .success(true)
                .message("Category created successfully")
                .data(category)
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<CategoryDto> getCategory(@PathVariable Long id) {
        CategoryDto category = categoryService.getCategory(id);
        return ApiResponse.<CategoryDto>builder()
                .success(true)
                .message("Category retrieved successfully")
                .data(category)
                .build();
    }

    @GetMapping
    public ApiResponse<List<CategoryDto>> getAllCategories() {
        List<CategoryDto> categories = categoryService.getAllCategories();
        return ApiResponse.<List<CategoryDto>>builder()
                .success(true)
                .message("Categories retrieved successfully")
                .data(categories)
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<CategoryDto> updateCategory(@PathVariable Long id, @RequestBody CreateCategoryDto dto) {
        CategoryDto updated = categoryService.updateCategory(id, dto);
        return ApiResponse.<CategoryDto>builder()
                .success(true)
                .message("Category updated successfully")
                .data(updated)
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ApiResponse.<Void>builder()
                .success(true)
                .message("Category deleted successfully")
                .build();
    }
}
