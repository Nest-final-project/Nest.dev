package caffeine.nest_dev.domain.user.dto.response;

import caffeine.nest_dev.domain.user.entity.User;
import caffeine.nest_dev.domain.user.enums.UserGrade;
import caffeine.nest_dev.domain.user.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserResponseDto {

    private Long id;
    private String name;
    private String email;
    private String nickName;
    private String phoneNumber;
    private UserRole userRole;
    private UserGrade userGrade;
    private String bank;
    private String accountNumber;

    public static UserResponseDto of(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .nickName(user.getNickName())
                .phoneNumber(user.getPhoneNumber())
                .userRole(user.getUserRole())
                .userGrade(user.getUserGrade())
                .bank(user.getBank())
                .accountNumber(user.getAccountNumber())
                .build();
    }
}
