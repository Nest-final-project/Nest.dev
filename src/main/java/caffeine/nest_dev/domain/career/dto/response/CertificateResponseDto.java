package caffeine.nest_dev.domain.career.dto.response;


import caffeine.nest_dev.domain.certificate.entity.Certificate;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CertificateResponseDto {

    private Long id;
    private String fileUrl;

    public static CertificateResponseDto from(Certificate certificate) {
        return CertificateResponseDto.builder()
                .id(certificate.getId())
                .fileUrl(certificate.getFileUrl())
                .build();
    }

    public static List<CertificateResponseDto> fromList(List<Certificate> certificateList) {
        return certificateList.stream().map(CertificateResponseDto::from).collect(
                Collectors.toList());
    }

}
