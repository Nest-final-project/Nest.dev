package caffeine.nest_dev.domain.user.entity;

import caffeine.nest_dev.common.entity.BaseEntity;
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

    public void updateEmail(String email) {
        this.email = email;
    }

    // -------------- 수정 메서드 --------------

    public void updateNickName(String nickName) {
        this.nickName = nickName;
    }

    public void updatePhoneNumber (String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void updateBank(String bank) {
        this.bank = bank;
    }

    public void updateAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public void updatePassword(String password) {
        this.password = password;
    }
}
