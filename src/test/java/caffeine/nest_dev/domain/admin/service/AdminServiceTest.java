package caffeine.nest_dev.domain.admin.service;

import caffeine.nest_dev.common.dto.PagingResponse;
import caffeine.nest_dev.common.enums.ErrorCode;
import caffeine.nest_dev.common.exception.BaseException;
import caffeine.nest_dev.domain.admin.dto.request.AdminRequestDto;
import caffeine.nest_dev.domain.admin.dto.response.AdminMentorCareerResponseDto;
import caffeine.nest_dev.domain.career.entity.Career;
import caffeine.nest_dev.domain.career.enums.CareerStatus;
import caffeine.nest_dev.domain.career.repository.CareerRepository;
import caffeine.nest_dev.domain.profile.entity.Profile;
import caffeine.nest_dev.domain.user.entity.User;
import caffeine.nest_dev.domain.user.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminServiceTest {

    @Mock
    private CareerRepository careerRepository;

    @InjectMocks
    private AdminService adminService;

    private Career dummyCareer;

    @BeforeEach
    void setUp() {
        // User 생성
        User user = User.builder()
                .name("홍길동")
                .email("test@example.com")
                .password("password123")
                .userRole(UserRole.MENTOR)
                .build();

        // Profile 생성 (nickname은 User.getName() 으로 추정)
        Profile profile = Profile.builder()
                .user(user)
                .build();

        // Career 생성
        dummyCareer = Career.builder()
                .profile(profile)
                .company("삼성전자")
                .startAt(LocalDateTime.of(2022, 1, 1, 0, 0))
                .endAt(LocalDateTime.of(2023, 12, 31, 0, 0))
                .build();

        // 상태 설정
        dummyCareer.updateCareerStatus(CareerStatus.UNAUTHORIZED);
    }

    @Test
    void getMentorCareers_shouldReturnPagingResponse() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Career> page = new PageImpl<>(Collections.singletonList(dummyCareer));

        when(careerRepository.findByCareerStatus(pageable)).thenReturn(page);

        PagingResponse<AdminMentorCareerResponseDto> result = adminService.getMentorCareers(pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getStatus()).isEqualTo(CareerStatus.UNAUTHORIZED.name());

        verify(careerRepository, times(1)).findByCareerStatus(pageable);
    }

    @Test
    void getMentorCareerById_shouldReturnDto_whenCareerExists() {
        when(careerRepository.findById(1L)).thenReturn(Optional.of(dummyCareer));

        AdminMentorCareerResponseDto dto = adminService.getMentorCareerById(1L);

        assertThat(dto).isNotNull();
        assertThat(dto.getStatus()).isEqualTo(CareerStatus.UNAUTHORIZED.name());
    }

    @Test
    void getMentorCareerById_shouldThrowException_whenCareerNotFound() {
        when(careerRepository.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminService.getMentorCareerById(100L))
                .isInstanceOf(BaseException.class)
                .hasMessageContaining(ErrorCode.ADMIN_MENTOR_CAREER_NOT_FOUND.getMessage());
    }

    @Test
    void updateCareerStatus_shouldUpdate_whenValidRequest() {
        AdminRequestDto dto = new AdminRequestDto(CareerStatus.AUTHORIZED);

        when(careerRepository.findById(1L)).thenReturn(Optional.of(dummyCareer));

        adminService.updateCareerStatus(1L, dto);

        verify(careerRepository, times(1)).findById(1L);
        assertThat(dummyCareer.getCareerStatus()).isEqualTo(CareerStatus.AUTHORIZED);
    }

    @Test
    void updateCareerStatus_shouldThrow_whenSameStatus() {
        AdminRequestDto dto = new AdminRequestDto(CareerStatus.UNAUTHORIZED);
        when(careerRepository.findById(1L)).thenReturn(Optional.of(dummyCareer));

        assertThatThrownBy(() -> adminService.updateCareerStatus(1L, dto))
                .isInstanceOf(BaseException.class)
                .hasMessageContaining(ErrorCode.ALREADY_SAME_STATUS.getMessage());
    }

    @Test
    void updateCareerStatus_shouldThrow_whenCareerNotFound() {
        when(careerRepository.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminService.updateCareerStatus(999L, new AdminRequestDto(CareerStatus.AUTHORIZED)))
                .isInstanceOf(BaseException.class)
                .hasMessageContaining(ErrorCode.ADMIN_MENTOR_CAREER_NOT_FOUND.getMessage());
    }

    // 리플렉션으로 private 필드 세팅
    private void setField(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException("필드 설정 실패: " + fieldName, e);
        }
    }
    @Test
    void getMentorCareers_shouldReturnEmpty_whenNoCareersFound() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Career> emptyPage = new PageImpl<>(Collections.emptyList());

        when(careerRepository.findByCareerStatus(pageable)).thenReturn(emptyPage);

        PagingResponse<AdminMentorCareerResponseDto> result = adminService.getMentorCareers(pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);

        verify(careerRepository, times(1)).findByCareerStatus(pageable);
    }

    @Test
    void updateCareerStatus_shouldNotCallSave_whenOnlyStatusChanged() {
        //  [Given] 관리자가 특정 Career 상태를 변경하려는 요청이 주어졌을 때
        // CareerStatus.AUTHORIZED로 상태 변경을 요청하는 DTO 생성
        AdminRequestDto dto = new AdminRequestDto(CareerStatus.AUTHORIZED);

        // CareerRepository에서 ID 1L인 Career를 조회했을 때 dummyCareer를 반환하도록 mock 설정
        when(careerRepository.findById(1L)).thenReturn(Optional.of(dummyCareer));

        // [When] 상태 변경 메서드 실행
        // adminService에서 상태 변경 메서드 호출 (내부적으로 dummyCareer의 상태가 변경됨)
        adminService.updateCareerStatus(1L, dto);

        // [Then] 검증 단계
        // careerRepository.findById()는 정확히 1번 호출되었는지 확인
        verify(careerRepository, times(1)).findById(1L);

        // careerRepository.save()는 한 번도 호출되지 않았는지 확인
        // 상태 변경이 영속성 컨텍스트 내에서만 일어나고 별도 save 호출이 없음을 검증
        verify(careerRepository, never()).save(any());

        // 실제 dummyCareer 객체의 상태가 요청한 값으로 변경되었는지 확인
        assertThat(dummyCareer.getCareerStatus()).isEqualTo(CareerStatus.AUTHORIZED);
    }


    @Test
    void updateCareerStatus_shouldCallRepositoryOnlyOnce_whenValidRequest() {
        // given
        AdminRequestDto dto = new AdminRequestDto(CareerStatus.AUTHORIZED);
        when(careerRepository.findById(1L)).thenReturn(Optional.of(dummyCareer));

        // when
        adminService.updateCareerStatus(1L, dto);

        // then
        // findById는 1번 호출
        verify(careerRepository, times(1)).findById(1L);

        // save()는 호출되지 않음 (영속성 컨텍스트에서만 변경)
        verify(careerRepository, never()).save(any());

        // 상태 값이 요청 값으로 정확히 변경되었는지 확인
        assertThat(dummyCareer.getCareerStatus()).isEqualTo(CareerStatus.AUTHORIZED);
    }


}
