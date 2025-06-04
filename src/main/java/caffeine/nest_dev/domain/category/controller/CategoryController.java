package caffeine.nest_dev.domain.category.controller;

import caffeine.nest_dev.common.dto.CommonResponse;
import caffeine.nest_dev.common.dto.PagingResponse;
import caffeine.nest_dev.common.enums.SuccessCode;
import caffeine.nest_dev.domain.admin.dto.response.AdminMentorCareerResponseDto;
import caffeine.nest_dev.domain.category.dto.request.CategoryRequestDto;
import caffeine.nest_dev.domain.category.dto.response.CategoryResponseDto;
import caffeine.nest_dev.domain.category.service.CategoryService;
import caffeine.nest_dev.domain.user.entity.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("/admin/categories")
    public ResponseEntity<CommonResponse<CategoryResponseDto>> createCategory(
            @RequestBody @Valid CategoryRequestDto categoryRequestDto) {
        CategoryResponseDto responseDto = categoryService.creatCategory(categoryRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.of(SuccessCode.SUCCESS_CATEGORY_CREATED, responseDto));
    }

    @GetMapping("/categories")
    public ResponseEntity<CommonResponse<PagingResponse<CategoryResponseDto>>> getCategories(
            Pageable pageable) {
        PagingResponse<CategoryResponseDto> categories = categoryService.getCategories(pageable);
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_CATEGORY_READ, categories));
    }

    @PatchMapping("/admin/categories/{categoryId}")
    public ResponseEntity<CommonResponse<CategoryResponseDto>> updateCategory(
            @PathVariable Long categoryId,
            @RequestBody @Valid CategoryRequestDto categoryRequestDto
    ) {
        CategoryResponseDto responseDto = categoryService.updateCategory(categoryId,
                categoryRequestDto);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_CATEGORY_UPDATED, responseDto));
    }

    @DeleteMapping("/admin/categories/{categoryId}")
    public ResponseEntity<CommonResponse<Void>> deleteCategory(@PathVariable Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_CATEGORY_DELETED));
    }


}
