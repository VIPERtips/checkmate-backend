package co.zw.blexta.checkmate.device.category;

import co.zw.blexta.checkmate.common.dto.CategoryDto;
import co.zw.blexta.checkmate.common.dto.CreateCategoryDto;
import co.zw.blexta.checkmate.common.response.ApiResponse;
import co.zw.blexta.checkmate.common.utils.SessionUtil;
import co.zw.blexta.checkmate.staff_users.User;
import co.zw.blexta.checkmate.staff_users.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final SessionUtil sessionUtil;
    private final UserService userService;

    @PostMapping
    public ApiResponse<CategoryDto> createCategory(@RequestBody CreateCategoryDto dto,
                                                   @RequestHeader("Authorization") String authHeader) {
        Long creatorId = sessionUtil.extractUserId(authHeader);
        User creator = userService.findById(creatorId);

        CategoryDto category = categoryService.createCategory(dto, creator);
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
    public ApiResponse<List<CategoryDto>> getAllCategories(@RequestHeader("Authorization") String authHeader) {
        Long userId = sessionUtil.extractUserId(authHeader);
        User user = userService.findById(userId);

        List<CategoryDto> categories = categoryService.getAllCategories(user);
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
