package caffeine.nest_dev.domain.profile.service;

import caffeine.nest_dev.common.dto.PagingResponse;
import caffeine.nest_dev.common.enums.ErrorCode;
import caffeine.nest_dev.common.exception.BaseException;
import caffeine.nest_dev.domain.category.entity.Category;
import caffeine.nest_dev.domain.category.repository.CategoryRepository;
import caffeine.nest_dev.domain.keyword.entity.Keyword;
import caffeine.nest_dev.domain.keyword.entity.ProfileKeyword;
import caffeine.nest_dev.domain.keyword.repository.KeywordRepository;
import caffeine.nest_dev.domain.profile.dto.request.ProfileRequestDto;
import caffeine.nest_dev.domain.profile.dto.response.ProfileResponseDto;
import caffeine.nest_dev.domain.profile.dto.response.RecommendedProfileResponseDto;
import caffeine.nest_dev.domain.profile.entity.Profile;
import caffeine.nest_dev.domain.profile.repository.ProfileRepository;
import caffeine.nest_dev.domain.user.entity.User;
import caffeine.nest_dev.domain.user.service.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final UserService userService;
    private final CategoryRepository categoryRepository;
    private final KeywordRepository keywordRepository;

    @Transactional
    public ProfileResponseDto createProfile(Long userId, ProfileRequestDto requestDto) {

        // 유저 조회
        User user = userService.findByIdAndIsDeletedFalseOrElseThrow(userId);

        // 카테고리 조회
        Category category = categoryRepository.findById(requestDto.getCategoryId())
                .orElseThrow(() -> new BaseException(ErrorCode.CATEGORY_NOT_FOUND));

        // 키워드 조회
        List<Keyword> keywords = keywordRepository.findAllById(requestDto.getKeywordId());

        Profile profile = requestDto.toEntity(user, category);

        List<ProfileKeyword> profileKeywords = requestDto.toProfileKeywords(profile, keywords);
        profile.getProfileKeywords().addAll(profileKeywords);

        Profile save = profileRepository.save(profile);

        return ProfileResponseDto.from(save, user);

    }

    @Transactional(readOnly = true)
    public ProfileResponseDto getProfile(Long userId, Long profileId) {

        User user = userService.findByIdAndIsDeletedFalseOrElseThrow(userId);

        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new BaseException(ErrorCode.PROFILE_NOT_FOUND));

        // 프로필이 해당 유저의 프로필인지 검증
        if (!profile.getUser().getId().equals(userId)) {
            throw new BaseException(ErrorCode.ACCESS_DENIED);
        }

        return ProfileResponseDto.from(profile, user);
    }

    @Transactional
    public void updateProfile(Long id, Long profileId, ProfileRequestDto profileRequestDto) {

        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new BaseException(ErrorCode.PROFILE_NOT_FOUND));

        if (!profile.getUser().getId().equals(id)) {
            throw new BaseException(ErrorCode.ACCESS_DENIED);
        }

        Category category = categoryRepository.findById(profileRequestDto.getCategoryId())
                .orElseThrow(() -> new BaseException(ErrorCode.CATEGORY_NOT_FOUND));

        List<Keyword> keywords = keywordRepository.findAllById(profileRequestDto.getKeywordId());

        // 프로필 정보 수정
        profile.updateProfile(profileRequestDto.getTitle(), profileRequestDto.getIntroduction(),
                profileRequestDto.getImageUrl(), category);

        profile.getProfileKeywords().clear();
        List<ProfileKeyword> profileKeywords = profileRequestDto.toProfileKeywords(profile, keywords);
        profile.getProfileKeywords().addAll(profileKeywords);
    }

    @Transactional(readOnly = true)
    public List<ProfileResponseDto> searchMentorProfilesByKeyword(String keyword) {
        List<Profile> profiles = profileRepository.searchMentorProfilesByKeyword(keyword);

        return profiles.stream()
                .map(profile -> ProfileResponseDto.from(profile, profile.getUser()))
                .toList();
    }

    @Transactional(readOnly = true)
    public PagingResponse<ProfileResponseDto> getMyProfiles(Long userId, Pageable pageable) {
        Page<Profile> profiles = profileRepository.findByUserId(userId, pageable);



        Page<ProfileResponseDto> map = profiles.map(
                profile -> ProfileResponseDto.from(profile, profile.getUser()));

        return PagingResponse.from(map);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "mentorsList", key = "#categoryId")
    public List<RecommendedProfileResponseDto> getRecommendedProfiles(Long categoryId) {
        return profileRepository.searchRecommendedMentorProfiles(categoryId);
    }
}
