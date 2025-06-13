package caffeine.nest_dev.domain.career.entity;

import caffeine.nest_dev.common.entity.BaseEntity;
import caffeine.nest_dev.domain.career.dto.request.UpdateCareerRequestDto;
import caffeine.nest_dev.domain.career.enums.CareerStatus;
import caffeine.nest_dev.domain.certificate.entity.Certificate;
import caffeine.nest_dev.domain.profile.entity.Profile;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "careers")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Career extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    private Profile profile;

    @Column(nullable = false)
    private String company;

    @Column(nullable = false)
    private LocalDateTime startAt;

    private LocalDateTime endAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CareerStatus careerStatus;

    @OneToMany(mappedBy = "career", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Certificate> certificates = new ArrayList<>();

    @Builder
    public Career(Profile profile, String company, LocalDateTime startAt, LocalDateTime endAt) {
        this.profile = profile;
        this.company = company;
        this.startAt = startAt;
        this.endAt = endAt;
        this.careerStatus = CareerStatus.UNAUTHORIZED;
    }

    public void updateCareerStatus(CareerStatus newStatus) {
        this.careerStatus = newStatus;
    }

    public void updateCareer(UpdateCareerRequestDto dto) {
        if (dto.getCompany() != null) {
            this.company = dto.getCompany();
        }

        if (dto.getStartAt() != null) {
            this.startAt = dto.getStartAt();
        }

        this.endAt = dto.getEndAt();
    }

    public void addCertificate(Certificate certificate) {
        this.certificates.add(certificate);
        certificate.updateCareer(this);
    }
}
