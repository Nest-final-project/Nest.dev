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

        // 1. Complaint인지, Inquiry인지 체크하기.
        // 1) COMPLAINT인 경우
        if (complaintRequestDto.getType().equals(ComplaintType.COMPLAINT)) {

            // 예약번호가 없을 경우
            if (complaintRequestDto.getReservationId() == null) {
                throw new BaseException(ErrorCode.COMPLAINT_NEED_RESERVATION_ID);
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
    public PagingResponse<ComplaintResponseDto> getComplaints(Pageable pageable) {
        Page<Complaint> complaintPage = complaintRepository.findAll(pageable);
        Page<ComplaintResponseDto> complaintResponseDtos = complaintPage.map(ComplaintResponseDto::of);

        return PagingResponse.from(complaintResponseDtos);
    }

    @Transactional(readOnly = true)
    public ComplaintResponseDto getComplaint(Long complaintId) {

        Complaint complaint = complaintRepository.findById(complaintId).orElseThrow(()-> new BaseException(ErrorCode.COMPLAINT_NOT_FOUND));

        return ComplaintResponseDto.of(complaint);
    }
}
