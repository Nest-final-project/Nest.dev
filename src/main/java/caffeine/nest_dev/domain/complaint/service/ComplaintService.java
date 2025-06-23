package caffeine.nest_dev.domain.complaint.service;

import caffeine.nest_dev.common.dto.PagingResponse;
import caffeine.nest_dev.common.enums.ErrorCode;
import caffeine.nest_dev.common.exception.BaseException;
import caffeine.nest_dev.domain.complaint.dto.request.ComplaintRequestDto;
import caffeine.nest_dev.domain.complaint.dto.response.ComplaintResponseDto;
import caffeine.nest_dev.domain.complaint.entity.Complaint;
import caffeine.nest_dev.domain.complaint.enums.ComplaintType;
import caffeine.nest_dev.domain.complaint.repository.ComplaintRepository;
import caffeine.nest_dev.domain.reservation.entity.Reservation;
import caffeine.nest_dev.domain.reservation.repository.ReservationRepository;
import caffeine.nest_dev.domain.user.entity.User;
import caffeine.nest_dev.domain.user.repository.UserRepository;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ComplaintService {

    private final ReservationRepository reservationRepository;
    private final ComplaintRepository complaintRepository;
    private final UserRepository userRepository;


    @Transactional
    public ComplaintResponseDto save(Long userId, ComplaintRequestDto complaintRequestDto) {

        Reservation reservation = null;

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));

        // type이 null인지 체크
        if (complaintRequestDto.getType() == null) {
            throw new BaseException(ErrorCode.INVALID_INPUT_VALUE);
        }

        // 1. Complaint인지, Inquiry인지 체크하기.
        // 1) COMPLAINT인 경우
        if (ComplaintType.COMPLAINT.equals(complaintRequestDto.getType())) {

            // 예약번호가 없을 경우
            if (complaintRequestDto.getReservationId() == null) {
                throw new BaseException(ErrorCode.COMPLAINT_NEED_RESERVATION_ID);
            }

            // 이미 작성된 민원이 있는 지 확인.
            boolean exists = complaintRepository.existsByReservationId(
                    complaintRequestDto.getReservationId());

            if (exists) {
                throw new BaseException(ErrorCode.DUPLICATED_COMPLAINT);
            }

            // 예약이 없을 경우
            reservation = reservationRepository.findById(
                            complaintRequestDto.getReservationId())
                    .orElseThrow(() -> new BaseException(ErrorCode.RESERVATION_NOT_FOUND));

            // 본인이 예약한 건이 아닐 경우
            if (!reservation.getMentee().getId().equals(userId)) {
                throw new BaseException(ErrorCode.ACCESS_DENIED);
            }
        }
        Complaint complaint = complaintRepository.save(
                complaintRequestDto.toEntity(user, reservation));

        return ComplaintResponseDto.of(complaint);
    }


    @Transactional(readOnly = true)
    public PagingResponse<ComplaintResponseDto> getInquiries(Pageable pageable) {

        List<ComplaintType> complaintTypes = Arrays.asList(
                ComplaintType.INQUIRY_ACCOUNT, // 계정 관련 문의
                ComplaintType.INQUIRY_CHAT, // 채팅 관련 문의
                ComplaintType.INQUIRY_PAY, // 결제 관련 문의
                ComplaintType.INQUIRY_RESERVATION, // 예약 관련 문의
                ComplaintType.INQUIRY_TICKET, // 이용권 관련 문의
                ComplaintType.INQUIRY_PROFILE // 프로필 관련 문의
        );

        Page<Complaint> complaintPage = complaintRepository.findALLByComplaintTypeIn(complaintTypes,
                pageable);
        Page<ComplaintResponseDto> complaintResponseDtos = complaintPage.map(
                ComplaintResponseDto::of);

        return PagingResponse.from(complaintResponseDtos);
    }

    @Transactional(readOnly = true)
    public ComplaintResponseDto getComplaint(Long complaintId) {

        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new BaseException(ErrorCode.COMPLAINT_NOT_FOUND));

        return ComplaintResponseDto.of(complaint);
    }

    public PagingResponse<ComplaintResponseDto> getMyComplaints(Long userId, Pageable pageable) {

        Page<Complaint> complaintList = complaintRepository.findAllByUserId(userId,
                pageable);
        Page<ComplaintResponseDto> complaintResponseDtos = complaintList.map(
                ComplaintResponseDto::of);

        return PagingResponse.from(complaintResponseDtos);
    }

    public void deleteComplant(Long id, Long complaintId) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new BaseException(ErrorCode.COMPLAINT_NOT_FOUND));
        if (!complaint.getUser().getId().equals(id)) {
            throw new BaseException(ErrorCode.ACCESS_DENIED);
        }

        complaintRepository.delete(complaint);
    }
}
