package caffeine.nest_dev.domain.auth.service;

import caffeine.nest_dev.domain.auth.dto.request.AuthRequestDto;
import caffeine.nest_dev.domain.auth.dto.response.AuthResponseDto;
import caffeine.nest_dev.domain.user.entity.User;
import caffeine.nest_dev.domain.user.enums.UserGrade;
import caffeine.nest_dev.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    public AuthResponseDto signup(AuthRequestDto dto) {

        User user = User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .nickName(dto.getNickname())
                .password(dto.getPassword())
                .phoneNumber(dto.getPhoneNumber())
                .userGrade(UserGrade.SEED)
                .userRole(dto.getUserRole())
                .build();

        User saved = userRepository.save(user);

        return AuthResponseDto.builder()
                .id(saved.getId())
                .name(saved.getName())
                .email(saved.getEmail())
                .nickName(saved.getNickName())
                .phoneNumber(saved.getPhoneNumber())
                .userGrade(saved.getUserGrade())
                .userRole(saved.getUserRole())
                .build();
    }
}
