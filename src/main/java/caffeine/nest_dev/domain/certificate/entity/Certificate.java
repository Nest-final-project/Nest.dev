package caffeine.nest_dev.domain.certificate.entity;

import caffeine.nest_dev.domain.career.entity.Career;
import caffeine.nest_dev.domain.certificate.dto.request.UpdateCertificateRequestDto;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "certificates")
public class Certificate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "career_id", nullable = false)
    private Career career;

    private String fileUrl;

    public void updateCertificate(UpdateCertificateRequestDto dto) {
        this.fileUrl = dto.getFileUrl();
    }

    public void updateCareer(Career career) {
        this.career = career;
    }
}
