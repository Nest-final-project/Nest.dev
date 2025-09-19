package caffeine.nest_dev.domain.profile.service;

import caffeine.nest_dev.common.enums.ErrorCode;
import caffeine.nest_dev.common.exception.BaseException;
import caffeine.nest_dev.domain.category.entity.Category;
import caffeine.nest_dev.domain.category.repository.CategoryRepository;
import caffeine.nest_dev.domain.keyword.repository.KeywordRepository;
import caffeine.nest_dev.domain.profile.dto.request.ProfileRequestDto;
import caffeine.nest_dev.domain.profile.entity.Profile;
import caffeine.nest_dev.domain.profile.repository.ProfileRepository;
import caffeine.nest_dev.domain.user.entity.User;
import caffeine.nest_dev.domain.user.enums.UserRole;
import caffeine.nest_dev.domain.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * ProfileService 단위 테스트
 * - 이미 존재하는 프로필 중복 생성 시 BaseException(PROFILE_ALREADY_EXISTS) 발생 검증
 */
@ExtendWith(MockitoExtension.class) // JUnit5에 Mockito 확장 연결(@Mock, @InjectMocks 자동 초기화)
class ProfileServiceTest {

    // ======= 협력 객체들을 Mock으로 대체 (실제 DB/외부 의존 없이 빠르고 안정적인 테스트) =======
    @org.mockito.Mock
    ProfileRepository profileRepository;

    @org.mockito.Mock
    UserService userService;

    @org.mockito.Mock
    CategoryRepository categoryRepository;

    @org.mockito.Mock
    KeywordRepository keywordRepository;

    // ======= 테스트 대상 주입: 위 Mock들이 생성자를 통해 ProfileService에 주입됨 =======
    @org.mockito.InjectMocks
    ProfileService profileService;

    @Test
    @DisplayName("createProfile: 동일 사용자·카테고리 프로필이 이미 존재하면 PROFILE_ALREADY_EXISTS 예외를 던진다")
    void createProfile_shouldThrow_whenProfileAlreadyExists() {
        // -------------------- Given (준비) --------------------
        Long userId = 1L;       // 테스트 입력: 사용자 ID
        Long categoryId = 10L;  // 테스트 입력: 카테고리 ID

        // 요청 DTO는 내부에서 getCategoryId()만 사용하므로 Mock으로 충분
        ProfileRequestDto req = Mockito.mock(ProfileRequestDto.class);
        when(req.getCategoryId()).thenReturn(categoryId); // 서비스가 DTO에서 카테고리 ID를 읽을 때 반환값 설정

        // User, Category 엔티티는 기본 생성자가 protected일 수 있으므로 mock 사용
        User user = Mockito.mock(User.class);
        Category category = Mockito.mock(Category.class);
        // 서비스 내부에서 category.getId()를 읽어 중복 여부를 검사하므로 반드시 스텁
        when(category.getId()).thenReturn(categoryId);

        // 유저 조회 스텁: 호출 시 정상 유저 반환
        when(userService.findByIdAndIsDeletedFalseOrElseThrow(userId)).thenReturn(user);

        // 카테고리 조회 스텁: 호출 시 Optional<Category> 반환
        when(categoryRepository.findById(categoryId)).thenReturn(java.util.Optional.of(category));

        // 핵심 스텁: "이미 존재하냐" 조회 시 true 반환 → 서비스가 중복 예외 경로로 진입
        when(profileRepository.existsByUserIdAndCategoryIdAndIsDeletedFalse(userId, categoryId))
                .thenReturn(true);

        // -------------------- When (실행) --------------------
        // createProfile 호출 시 BaseException이 반드시 발생해야 함
        BaseException ex = assertThrows(
                BaseException.class,
                () -> profileService.createProfile(userId, req)
        );

        // -------------------- Then (검증) --------------------
        // 1) 예외의 ErrorCode가 정확히 PROFILE_ALREADY_EXISTS인지 확인 (단순 예외 발생만으로는 부족)
        assertEquals(ErrorCode.PROFILE_ALREADY_EXISTS, ex.getErrorCode());

        // 2) 중복 판단 후에는 저장(save)이 절대 실행되면 안 됨 → 부수효과 방지 검증
        verify(profileRepository, never()).save(any(Profile.class));

        // (선택) 추가 상호작용 검증: 유저/카테고리 조회와 중복 체크는 정확히 1회씩 호출되었는지
        verify(userService, times(1)).findByIdAndIsDeletedFalseOrElseThrow(userId);
        verify(categoryRepository, times(1)).findById(categoryId);
        verify(profileRepository, times(1))
                .existsByUserIdAndCategoryIdAndIsDeletedFalse(userId, categoryId);

        // (선택) 불필요한 상호작용이 없는지 체크하고 싶다면 아래 라인으로 마무리할 수도 있음
        // verifyNoMoreInteractions(userService, categoryRepository, profileRepository, keywordRepository);
    }
    @Test
    @DisplayName("createProfile: 성공 시 save가 호출된다")
    void createProfile_success() {
        Long userId = 1L;
        Long categoryId = 10L;

        ProfileRequestDto req = mock(ProfileRequestDto.class);
        when(req.getCategoryId()).thenReturn(categoryId);

        // User Mock
        User user = mock(User.class);
        when(user.getUserRole()).thenReturn(UserRole.MENTOR); // UserRole 세팅

        Category category = mock(Category.class);
        when(category.getId()).thenReturn(categoryId);

        Profile profile = mock(Profile.class);
        when(profile.getCategory()).thenReturn(category);
        when(req.toEntity(user, category)).thenReturn(profile);
        when(req.toProfileKeywords(eq(profile), any())).thenReturn(java.util.List.of());

        when(userService.findByIdAndIsDeletedFalseOrElseThrow(userId)).thenReturn(user);
        when(categoryRepository.findById(categoryId)).thenReturn(java.util.Optional.of(category));
        when(profileRepository.existsByUserIdAndCategoryIdAndIsDeletedFalse(userId, categoryId)).thenReturn(false);
        when(keywordRepository.findAllById(any())).thenReturn(java.util.List.of());
        when(profileRepository.save(any(Profile.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // 실행
        var response = profileService.createProfile(userId, req);

        // 검증
        assertNotNull(response); // 응답이 null 아님
        verify(profileRepository, times(1)).save(any(Profile.class));
    }

}
