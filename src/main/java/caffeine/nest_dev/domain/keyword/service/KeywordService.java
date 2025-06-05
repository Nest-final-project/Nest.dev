package caffeine.nest_dev.domain.keyword.service;

import caffeine.nest_dev.common.dto.PagingResponse;
import caffeine.nest_dev.common.enums.ErrorCode;
import caffeine.nest_dev.common.exception.BaseException;
import caffeine.nest_dev.domain.category.dto.response.CategoryResponseDto;
import caffeine.nest_dev.domain.category.entity.Category;
import caffeine.nest_dev.domain.keyword.dto.request.KeywordRequestDto;
import caffeine.nest_dev.domain.keyword.dto.response.KeywordResponseDto;
import caffeine.nest_dev.domain.keyword.entity.Keyword;
import caffeine.nest_dev.domain.keyword.repository.KeywordRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KeywordService {

    private final KeywordRepository keywordRepository;

    /**
     * 키워드 생성
     * */
    @Transactional
    public KeywordResponseDto createKeyword(KeywordRequestDto keywordRequestDto) {
        // 생성되어있는 키워드 이름 오류처리
        keywordRepository.findByName(keywordRequestDto.getName())
                .ifPresent(keyword -> {
                    throw new BaseException(ErrorCode.KEYWORD_ALREADY_EXISTS);
                });

        Keyword keyword = keywordRequestDto.toEntity();
        Keyword save = keywordRepository.save(keyword);
        return KeywordResponseDto.of(save);
    }


    /**
     * 키워드 목록조회
     * */
    public PagingResponse<KeywordResponseDto> getKeywords(Pageable pageable) {

        Page<Keyword> keywords = keywordRepository.findAll(pageable);

        Page<KeywordResponseDto> map = keywords.map(KeywordResponseDto::of);

        return PagingResponse.from(map);
    }

    @Transactional
    public KeywordResponseDto updateKeyword(Long keywordId, KeywordRequestDto keywordRequestDto) {
        Keyword keyword = keywordRepository.findById(keywordId)
                .orElseThrow(() -> new BaseException(ErrorCode.KEYWORD_NOT_FOUND));

        String dtoName = keywordRequestDto.getName();
        String keywordName = keyword.getName();

        if (dtoName.equals(keywordName)) {
            throw new BaseException(ErrorCode.ALREADY_SAME_KEYWORD_NAME);
        }
        keyword.update(dtoName);
        return KeywordResponseDto.of(keyword);
    }

    @Transactional
    public void deleteKeyword(Long keywordId) {
        Keyword keyword = keywordRepository.findById(keywordId)
                .orElseThrow(() -> new BaseException(ErrorCode.KEYWORD_NOT_FOUND));

        keyword.softDelete();
    }

    public PagingResponse<KeywordResponseDto> searchKeywords(String keyword, Pageable pageable) {

        Page<Keyword> keywords = keywordRepository.findByName(keyword, pageable);

        Page<KeywordResponseDto> map = keywords.map(KeywordResponseDto::of);

        return PagingResponse.from(map);
    }

}
