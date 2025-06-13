package caffeine.nest_dev.domain.keyword.controller;

import caffeine.nest_dev.common.dto.CommonResponse;
import caffeine.nest_dev.common.dto.PagingResponse;
import caffeine.nest_dev.common.enums.SuccessCode;
import caffeine.nest_dev.domain.category.dto.response.CategoryResponseDto;
import caffeine.nest_dev.domain.keyword.dto.request.KeywordRequestDto;
import caffeine.nest_dev.domain.keyword.dto.response.KeywordResponseDto;
import caffeine.nest_dev.domain.keyword.service.KeywordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class KeywordController {

    private final KeywordService keywordService;

    @PostMapping("/admin/keywords")
    public ResponseEntity<CommonResponse<KeywordResponseDto>> createKeyword(
            @RequestBody KeywordRequestDto keywordRequestDto) {
        KeywordResponseDto keyword = keywordService.createKeyword(keywordRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.of(SuccessCode.SUCCESS_KEYWORD_CREATED, keyword));
    }

    @GetMapping("/keywords")
    public ResponseEntity<CommonResponse<PagingResponse<KeywordResponseDto>>> getKeywords(
            Pageable pageable) {
        PagingResponse<KeywordResponseDto> keywords = keywordService.getKeywords(pageable);
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_KEYWORD_READ, keywords));
    }

    @PatchMapping("/admin/keywords/{keywordId}")
    public ResponseEntity<CommonResponse<KeywordResponseDto>> updateKeyword(
            @PathVariable Long keywordId,
            @RequestBody @Valid KeywordRequestDto keywordRequestDto
    ) {
        KeywordResponseDto keywordResponseDto = keywordService.updateKeyword(keywordId,
                keywordRequestDto);
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_KEYWORD_UPDATED, keywordResponseDto));
    }

    @DeleteMapping("/admin/keywords/{keywordId}")
    public ResponseEntity<CommonResponse<Void>> deleteKeyword(
            @PathVariable Long keywordId) {
        keywordService.deleteKeyword(keywordId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_KEYWORD_DELETED));
    }

    /**
     * 키워드 검색
     */
    @GetMapping("/keywords/search")
    public ResponseEntity<CommonResponse<PagingResponse<KeywordResponseDto>>> getKeywords(
            @RequestParam(required = false) String name,
            Pageable pageable
    ) {
        PagingResponse<KeywordResponseDto> byName = keywordService.searchKeywords(
                name, pageable);
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_KEYWORD_READ, byName));
    }

}
