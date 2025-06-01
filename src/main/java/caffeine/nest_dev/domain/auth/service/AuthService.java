package caffeine.nest_dev.domain.auth.service;

import caffeine.nest_dev.domain.auth.dto.request.AuthRequestDto;
import caffeine.nest_dev.domain.auth.dto.request.LoginRequestDto;
import caffeine.nest_dev.domain.auth.dto.response.AuthResponseDto;
import caffeine.nest_dev.domain.auth.dto.response.LoginResponseDto;
import caffeine.nest_dev.domain.user.entity.User;
import caffeine.nest_dev.domain.user.enums.UserGrade;
import caffeine.nest_dev.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    @Transactional
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

    @Transactional
    public LoginResponseDto login(LoginRequestDto dto) {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));
        return LoginResponseDto.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .nickName(user.getNickName())
//                .accessToken()
                .build();
    }
}
