package caffeine.nest_dev.domain.career.entity;

import caffeine.nest_dev.common.entity.BaseEntity;
import caffeine.nest_dev.domain.career.enums.CareerStatus;
import caffeine.nest_dev.domain.profile.entity.Profile;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Entity
@Table(name = "careers")
@AllArgsConstructor
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

    @OneToMany(mappedBy = "career")
    private List<Certificate> certificates = new ArrayList<>();

    public void updateCareerStatus(CareerStatus newStatus) {
        this.careerStatus = newStatus;
    }
}
