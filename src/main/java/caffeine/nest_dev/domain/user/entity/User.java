package caffeine.nest_dev.domain.user.entity;

import caffeine.nest_dev.common.entity.BaseEntity;
import caffeine.nest_dev.domain.user.dto.request.UserRequestDto;
import caffeine.nest_dev.domain.user.enums.SocialType;
import caffeine.nest_dev.domain.user.enums.UserGrade;
import caffeine.nest_dev.domain.user.enums.UserRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Entity
@Getter
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String email;

    @Column(nullable = false)
    private String nickName;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private SocialType socialType;  // 소셜 타입

    private String socialId; // 소셜 로그인 식별자

    @Enumerated(EnumType.STRING)
    private UserGrade userGrade; // 회원 등급

    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    private String bank;

    private String accountNumber;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean isDeleted;

    // -------------- 수정 메서드 --------------

    public void updateUser(UserRequestDto dto, User user) {
        if (dto.getEmail() != null) {
            this.email = dto.getEmail();
        }

        if (dto.getNickName() != null) {
            this.nickName = dto.getNickName();
        }

        if (dto.getPhoneNumber() != null) {
            this.phoneNumber = dto.getPhoneNumber();
        }

        // 멘토일 경우 추가 수정
        if (user.getUserRole() == UserRole.MENTOR) {
            if (dto.getBank() != null) {
                this.bank = dto.getBank();
            }

            if (dto.getAccountNumber() != null) {
                this.accountNumber = dto.getAccountNumber();
            }
        }
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void deleteUser(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
}
