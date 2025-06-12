package caffeine.nest_dev.domain.consultation.service;

import caffeine.nest_dev.common.enums.ErrorCode;
import caffeine.nest_dev.common.exception.BaseException;
import caffeine.nest_dev.domain.consultation.dto.request.ConsultationRequestDto;
import caffeine.nest_dev.domain.consultation.dto.response.ConsultationResponseDto;
import caffeine.nest_dev.domain.consultation.entity.Consultation;
import caffeine.nest_dev.domain.consultation.repository.ConsultationRepository;
import caffeine.nest_dev.domain.user.entity.User;
import caffeine.nest_dev.domain.user.service.UserService;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConsultationService {

    private final ConsultationRepository consultationRepository;
    private final UserService userService;

    @Transactional
    public ConsultationResponseDto createConsultation(Long userId,
            ConsultationRequestDto requestDto) {

        if (consultationRepository.existsConsultation(userId, requestDto.getStartAt(), requestDto.getEndAt())) {
            throw new BaseException(ErrorCode.DUPLICATE_CONSULTATION_TIME);
        }

        User user = userService.findByIdAndIsDeletedFalseOrElseThrow(userId);

        Consultation consultation = consultationRepository.save(
                requestDto.toEntity(user));

        return ConsultationResponseDto.of(consultation);

    }

    public List<ConsultationResponseDto> getMyConsultations(Long userId) {
        List<Consultation> list = consultationRepository.findByMentorId(userId);
        return list.stream().map(ConsultationResponseDto::of).collect(Collectors.toList());
    }


    @Transactional
    public ConsultationResponseDto updateConsultation(Long userId, Long consultationId, ConsultationRequestDto requestDto) {

        if (consultationRepository.existsConsultation(userId, requestDto.getStartAt(), requestDto.getEndAt())) {
            throw new BaseException(ErrorCode.DUPLICATE_CONSULTATION_TIME);
        }


        Consultation consultation = consultationRepository.findByIdAndMentorId(consultationId, userId)
                .orElseThrow(() -> new BaseException(ErrorCode.CONSULTATION_NOT_FOUND));

        consultation.update(requestDto.getStartAt(), requestDto.getEndAt());

        return ConsultationResponseDto.of(consultation);
    }

    @Transactional
    public void deleteConsultation(Long userId, Long consultationId) {

        Consultation consultation = consultationRepository.findByIdAndMentorId(consultationId, userId)
                .orElseThrow(() -> new BaseException(ErrorCode.CONSULTATION_NOT_FOUND));

        consultationRepository.delete(consultation);
    }
}
