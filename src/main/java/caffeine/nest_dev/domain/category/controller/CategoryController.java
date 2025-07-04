package caffeine.nest_dev.domain.category.controller;

import caffeine.nest_dev.common.dto.CommonResponse;
import caffeine.nest_dev.common.dto.PagingResponse;
import caffeine.nest_dev.common.enums.SuccessCode;
import caffeine.nest_dev.domain.admin.dto.response.AdminMentorCareerResponseDto;
import caffeine.nest_dev.domain.category.dto.request.CategoryRequestDto;
import caffeine.nest_dev.domain.category.dto.response.CategoryResponseDto;
import caffeine.nest_dev.domain.category.service.CategoryService;
import caffeine.nest_dev.domain.user.entity.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Category", description = "카테고리 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "카테고리 생성", description = "관리자가 새로운 카테고리를 생성합니다")
    @ApiResponse(responseCode = "201", description = "카테고리 생성 성공")
    @PostMapping("/admin/categories")
    public ResponseEntity<CommonResponse<CategoryResponseDto>> createCategory(
            @Parameter(description = "카테고리 생성 요청 정보") @RequestBody @Valid CategoryRequestDto categoryRequestDto) {
        CategoryResponseDto responseDto = categoryService.creatCategory(categoryRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.of(SuccessCode.SUCCESS_CATEGORY_CREATED, responseDto));
    }

    @Operation(summary = "카테고리 목록 조회", description = "페이징된 카테고리 목록을 조회합니다")
    @ApiResponse(responseCode = "200", description = "카테고리 목록 조회 성공")
    @GetMapping("/categories")
    public ResponseEntity<CommonResponse<PagingResponse<CategoryResponseDto>>> getCategories(
            @Parameter(description = "페이지 정보") Pageable pageable) {
        PagingResponse<CategoryResponseDto> categories = categoryService.getCategories(pageable);
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_CATEGORY_READ, categories));
    }

    @Operation(summary = "카테고리 수정", description = "관리자가 기존 카테고리를 수정합니다")
    @ApiResponse(responseCode = "200", description = "카테고리 수정 성공")
    @PatchMapping("/admin/categories/{categoryId}")
    public ResponseEntity<CommonResponse<CategoryResponseDto>> updateCategory(
            @Parameter(description = "수정할 카테고리 ID") @PathVariable Long categoryId,
            @Parameter(description = "카테고리 수정 요청 정보") @RequestBody @Valid CategoryRequestDto categoryRequestDto
    ) {
        CategoryResponseDto responseDto = categoryService.updateCategory(categoryId,
                categoryRequestDto);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_CATEGORY_UPDATED, responseDto));
    }

    @Operation(summary = "카테고리 삭제", description = "관리자가 카테고리를 삭제합니다")
    @ApiResponse(responseCode = "200", description = "카테고리 삭제 성공")
    @DeleteMapping("/admin/categories/{categoryId}")
    public ResponseEntity<CommonResponse<Void>> deleteCategory(
            @Parameter(description = "삭제할 카테고리 ID") @PathVariable Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_CATEGORY_DELETED));
    }


}
