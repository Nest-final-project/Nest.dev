package caffeine.nest_dev.domain.admin.service;

import caffeine.nest_dev.common.enums.ErrorCode;
import caffeine.nest_dev.common.exception.BaseException;
import caffeine.nest_dev.domain.admin.dto.request.AdminRequestDto;
import caffeine.nest_dev.domain.admin.dto.response.AdminMentorCareerResponseDto;
import caffeine.nest_dev.domain.career.entity.Career;
import caffeine.nest_dev.domain.career.enums.CareerStatus;
import caffeine.nest_dev.domain.career.repository.CareerRepository;
import caffeine.nest_dev.domain.user.entity.User;
import caffeine.nest_dev.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final CareerRepository careerRepository;

    /**
     * 멘토 경력 확인 요청 목록 조회
     */
    public Page<AdminMentorCareerResponseDto> getMentorCareers(Pageable pageable) {
        return careerRepository.findByCareerStatus(CareerStatus.UNAUTHORIZED, pageable)
                .map(AdminMentorCareerResponseDto::of);
    }

    /**
     * 멘토 경력 확인 요청 단건 조회
     */
    public AdminMentorCareerResponseDto getMentorCareerById(Long careerId) {
        Career career = careerRepository.findById(careerId)
                .orElseThrow(()-> new BaseException(ErrorCode.ADMIN_MENTOR_CAREER_NOT_FOUND));
        return AdminMentorCareerResponseDto.of(career);
    }

    @Transactional
    public void updateCareerStatus(Long careerId, AdminRequestDto adminRequestDto) {
        Career career = careerRepository.findById(careerId)
                .orElseThrow(() -> new BaseException(ErrorCode.ADMIN_MENTOR_CAREER_NOT_FOUND));

        CareerStatus newStatus = adminRequestDto.getStatus();

        CareerStatus careerStatus = career.getCareerStatus();

        // 같은 상태로 변경 요청시 에러
        if (careerStatus == newStatus) {
            throw new BaseException(ErrorCode.ALREADY_SAME_STATUS);
        }

        career.updateCareerStatus(newStatus);

    }
}
