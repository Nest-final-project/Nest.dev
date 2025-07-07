package caffeine.nest_dev.domain.complaint.entity;

import caffeine.nest_dev.common.entity.BaseEntity;
import caffeine.nest_dev.domain.complaint.enums.ComplaintStatus;
import caffeine.nest_dev.domain.complaint.enums.ComplaintType;
import caffeine.nest_dev.domain.reservation.entity.Reservation;
import caffeine.nest_dev.domain.user.entity.User;
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
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Entity
@Getter
@Table(name = "complaints")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Complaint extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = true)
    private Reservation reservation;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String contents;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ComplaintStatus complaintStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ComplaintType complaintType;

    public void changeStatus(){
        this.complaintStatus = ComplaintStatus.RESOLVED;
    }

}