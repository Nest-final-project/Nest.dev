package caffeine.nest_dev.domain.admin.service;

import caffeine.nest_dev.common.dto.PagingResponse;
import caffeine.nest_dev.common.enums.ErrorCode;
import caffeine.nest_dev.common.exception.BaseException;
import caffeine.nest_dev.domain.admin.dto.request.AdminRequestDto;
import caffeine.nest_dev.domain.admin.dto.response.AdminMentorCareerResponseDto;
import caffeine.nest_dev.domain.career.entity.Career;
import caffeine.nest_dev.domain.career.enums.CareerStatus;
import caffeine.nest_dev.domain.career.repository.CareerRepository;
import caffeine.nest_dev.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final CareerRepository careerRepository;

    /**
     * 멘토 경력 확인 요청 목록 조회
     */
    @Transactional(readOnly = true)
    public PagingResponse<AdminMentorCareerResponseDto> getMentorCareers(Pageable pageable) {

        Page<Career> career = careerRepository.findByCareerStatus(pageable);

        Page<AdminMentorCareerResponseDto> responseDtos = career.map(AdminMentorCareerResponseDto::of);

        return PagingResponse.from(responseDtos);
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
