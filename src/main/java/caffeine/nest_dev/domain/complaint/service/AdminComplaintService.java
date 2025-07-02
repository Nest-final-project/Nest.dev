package caffeine.nest_dev.domain.complaint.service;

import caffeine.nest_dev.common.dto.PagingResponse;
import caffeine.nest_dev.common.enums.ErrorCode;
import caffeine.nest_dev.common.exception.BaseException;
import caffeine.nest_dev.domain.complaint.dto.request.AnswerRequestDto;
import caffeine.nest_dev.domain.complaint.dto.request.AnswerUpdateRequestDto;
import caffeine.nest_dev.domain.complaint.dto.response.AnswerResponseDto;
import caffeine.nest_dev.domain.complaint.dto.response.ComplaintResponseDto;
import caffeine.nest_dev.domain.complaint.entity.Answer;
import caffeine.nest_dev.domain.complaint.entity.Complaint;
import caffeine.nest_dev.domain.complaint.repository.AnswerRepository;
import caffeine.nest_dev.domain.complaint.repository.ComplaintRepository;
import caffeine.nest_dev.domain.user.entity.User;
import caffeine.nest_dev.domain.user.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminComplaintService {

    private final UserRepository userRepository;
    private final ComplaintRepository complaintRepository;
    private final AnswerRepository answerRepository;

    @Transactional
    public AnswerResponseDto save(Long userId, Long complaintId,
            AnswerRequestDto answerRequestDto) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));

        boolean exists = answerRepository.existsByComplaintId(complaintId);

        if (exists) {
            throw new BaseException(ErrorCode.ANSWER_ALREADY_EXISTS);
        }

        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new BaseException(ErrorCode.COMPLAINT_NOT_FOUND));

        Answer answer = answerRepository.save(answerRequestDto.toEntity(user, complaint));
        complaint.changeStatus();

        return AnswerResponseDto.of(answer);
    }

    @Transactional(readOnly = true)
    public PagingResponse<ComplaintResponseDto> getAllComplaints(Pageable pageable) {
        Page<Complaint> complaint = complaintRepository.findSortedByStatusAndCreatedAt(pageable);
        Page<ComplaintResponseDto> complaintResponseDtos = complaint.map(ComplaintResponseDto::of);

        return PagingResponse.from(complaintResponseDtos);
    }

    @Transactional(readOnly = true)
    public ComplaintResponseDto getComplaint(Long complaintId) {

        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new BaseException(ErrorCode.COMPLAINT_NOT_FOUND));

        return ComplaintResponseDto.of(complaint);
    }

    @Transactional(readOnly = true)
    public AnswerResponseDto getAnswer(Long complaintId){

       Optional<Answer> answer = answerRepository.findByComplaint_Id(complaintId);

       if(answer.isEmpty()){
           throw new BaseException(ErrorCode.ANSWER_NOT_FOUND);
       }

        return AnswerResponseDto.of(answer.get());
    }


    @Transactional
    public void update(Long userId, Long answerId, AnswerUpdateRequestDto answerUpdateRequestDto) {


        Answer answer = answerRepository.findById(answerId).orElseThrow(() -> new BaseException(ErrorCode.ANSWER_NOT_FOUND));

        if(!answer.getUser().getId().equals(userId)) {
            throw new BaseException(ErrorCode.NO_PERMISSION);
        }

        answer.update(answerUpdateRequestDto);
    }

}
