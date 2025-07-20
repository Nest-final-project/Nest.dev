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
}
