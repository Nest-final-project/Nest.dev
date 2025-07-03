package caffeine.nest_dev.domain.keyword.controller;

import caffeine.nest_dev.common.dto.CommonResponse;
import caffeine.nest_dev.common.dto.PagingResponse;
import caffeine.nest_dev.common.enums.SuccessCode;
import caffeine.nest_dev.domain.category.dto.response.CategoryResponseDto;
import caffeine.nest_dev.domain.keyword.dto.request.KeywordRequestDto;
import caffeine.nest_dev.domain.keyword.dto.response.KeywordResponseDto;
import caffeine.nest_dev.domain.keyword.service.KeywordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Keyword", description = "키워드 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class KeywordController {

    private final KeywordService keywordService;

    @Operation(summary = "키워드 생성", description = "관리자가 새로운 키워드를 생성합니다")
    @ApiResponse(responseCode = "201", description = "키워드 생성 성공")
    @PostMapping("/admin/keywords")
    public ResponseEntity<CommonResponse<KeywordResponseDto>> createKeyword(
            @Parameter(description = "키워드 생성 요청 정보") @RequestBody KeywordRequestDto keywordRequestDto) {
        KeywordResponseDto keyword = keywordService.createKeyword(keywordRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.of(SuccessCode.SUCCESS_KEYWORD_CREATED, keyword));
    }

    @Operation(summary = "키워드 목록 조회", description = "등록된 키워드 목록을 조회합니다")
    @ApiResponse(responseCode = "200", description = "키워드 목록 조회 성공")
    @GetMapping("/keywords")
    public ResponseEntity<CommonResponse<PagingResponse<KeywordResponseDto>>> getKeywords(
            @Parameter(description = "페이지 정보") Pageable pageable) {
        PagingResponse<KeywordResponseDto> keywords = keywordService.getKeywords(pageable);
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_KEYWORD_READ, keywords));
    }

    @Operation(summary = "키워드 수정", description = "관리자가 기존 키워드를 수정합니다")
    @ApiResponse(responseCode = "200", description = "키워드 수정 성공")
    @PatchMapping("/admin/keywords/{keywordId}")
    public ResponseEntity<CommonResponse<KeywordResponseDto>> updateKeyword(
            @Parameter(description = "수정할 키워드 ID") @PathVariable Long keywordId,
            @Parameter(description = "키워드 수정 요청 정보") @RequestBody @Valid KeywordRequestDto keywordRequestDto
    ) {
        KeywordResponseDto keywordResponseDto = keywordService.updateKeyword(keywordId,
                keywordRequestDto);
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_KEYWORD_UPDATED, keywordResponseDto));
    }

    @Operation(summary = "키워드 삭제", description = "관리자가 키워드를 삭제합니다")
    @ApiResponse(responseCode = "200", description = "키워드 삭제 성공")
    @DeleteMapping("/admin/keywords/{keywordId}")
    public ResponseEntity<CommonResponse<Void>> deleteKeyword(
            @Parameter(description = "삭제할 키워드 ID") @PathVariable Long keywordId) {
        keywordService.deleteKeyword(keywordId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_KEYWORD_DELETED));
    }

    /**
     * 키워드 검색
     */
    @Operation(summary = "키워드 검색", description = "키워드 이름으로 검색합니다")
    @ApiResponse(responseCode = "200", description = "키워드 검색 성공")
    @GetMapping("/keywords/search")
    public ResponseEntity<CommonResponse<PagingResponse<KeywordResponseDto>>> getKeywords(
            @Parameter(description = "검색할 키워드 이름") @RequestParam(required = false) String name,
            @Parameter(description = "페이지 정보") Pageable pageable
    ) {
        PagingResponse<KeywordResponseDto> byName = keywordService.searchKeywords(
                name, pageable);
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.of(SuccessCode.SUCCESS_KEYWORD_READ, byName));
    }

}
