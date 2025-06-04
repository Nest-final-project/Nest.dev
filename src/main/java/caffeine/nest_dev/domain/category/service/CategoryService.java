package caffeine.nest_dev.domain.category.service;

import caffeine.nest_dev.common.dto.PagingResponse;
import caffeine.nest_dev.common.enums.ErrorCode;
import caffeine.nest_dev.common.exception.BaseException;
import caffeine.nest_dev.domain.admin.dto.response.AdminMentorCareerResponseDto;
import caffeine.nest_dev.domain.career.entity.Career;
import caffeine.nest_dev.domain.category.dto.request.CategoryRequestDto;
import caffeine.nest_dev.domain.category.dto.response.CategoryResponseDto;
import caffeine.nest_dev.domain.category.entity.Category;
import caffeine.nest_dev.domain.category.repository.CategoryRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    @Transactional
    public CategoryResponseDto creatCategory(CategoryRequestDto categoryRequestDto) {

        categoryRepository.findByName(categoryRequestDto.getName())
                .ifPresent(category -> {
                    throw new BaseException(ErrorCode.CATEGORY_ALREADY_EXISTS);
                });

        Category category = Category.builder()
                        .name(categoryRequestDto.getName())
                                .build();

        Category save = categoryRepository.save(category);

        return CategoryResponseDto.of(save);

    }

    public PagingResponse<CategoryResponseDto> getCategories(Pageable pageable) {

        Page<Category> categories = categoryRepository.findAll(pageable);

        Page<CategoryResponseDto> map = categories.map(CategoryResponseDto::of);

        return PagingResponse.from(map);
    }
}
