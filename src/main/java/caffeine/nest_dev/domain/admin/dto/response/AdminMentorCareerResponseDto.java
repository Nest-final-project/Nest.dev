package caffeine.nest_dev.domain.admin.dto.response;

import caffeine.nest_dev.domain.career.entity.Career;
import caffeine.nest_dev.domain.user.entity.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class AdminMentorCareerResponseDto {

    private Long careerId;
    private String mentorName;
    private String mentorEmail;
    private  String company;
    private  LocalDateTime startAt;
    private  LocalDateTime endAt;
    private  String status;
    private  List<String> files;


    public static AdminMentorCareerResponseDto of(Career career) {
        User user = career.getProfile().getUser();
        return AdminMentorCareerResponseDto.builder()
                .careerId(career.getId())
                .mentorName(user.getName())
                .mentorEmail(user.getEmail())
                .company(career.getCompany())
                .startAt(career.getStartAt())
                .endAt(career.getEndAt())
                .status(career.getCareerStatus().name())
                .files(career.getCertificates().stream()
                        .map(certificate -> certificate.getFileUrl())
                        .collect(Collectors.toList()))
                .build();
    }
}
