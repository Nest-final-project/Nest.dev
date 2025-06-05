package caffeine.nest_dev.domain.user.service;

import caffeine.nest_dev.common.config.JwtUtil;
import caffeine.nest_dev.common.config.PasswordEncoder;
import caffeine.nest_dev.common.enums.ErrorCode;
import caffeine.nest_dev.common.exception.BaseException;
import caffeine.nest_dev.domain.user.dto.request.UpdatePasswordRequestDto;
import caffeine.nest_dev.domain.user.dto.request.UserRequestDto;
import caffeine.nest_dev.domain.user.dto.response.UserResponseDto;
import caffeine.nest_dev.domain.user.entity.User;
import caffeine.nest_dev.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional(readOnly = true)
    public UserResponseDto findUser(Long userId) {

        // 유저 조회
        User user = findByIdAndIsDeletedFalseOrElseThrow(userId);

        return UserResponseDto.of(user);
    }

    @Transactional
    public void updateUser(Long userId, UserRequestDto dto) {

        // dto 가 null 일 때
        if (dto == null) {
            throw new IllegalArgumentException("수정하려는 항목 중 하나는 필수 입력값입니다.");
        }

        // 이메일 중복 검증
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new BaseException(ErrorCode.ALREADY_EXIST_EMAIL);
        }

        // 유저 조회
        User user = findByIdAndIsDeletedFalseOrElseThrow(userId);

        // 수정 메서드 호출
        user.updateUser(dto, user);
    }

    @Transactional
    public void updatePassword(Long userId, UpdatePasswordRequestDto dto) {

        // 유저 조회
        User user = findByIdAndIsDeletedFalseOrElseThrow(userId);

        // 비밀 번호 검증
        if (!passwordEncoder.matches(dto.getRawPassword(), user.getPassword())) {
            throw new BaseException(ErrorCode.INVALID_PASSWORD);
        }

        // 새로운 비밀번호가 현재 비밀번호와 같은 경우
        if (dto.getNewPassword().equals(dto.getRawPassword())) {
            throw new IllegalArgumentException("같은 비밀번호로 변경할 수 없습니다.");
        }

        // 새 비밀번호 인코딩
        String encodedPassword = passwordEncoder.encode(dto.getNewPassword());

        // 비밀번호 변경
        user.updatePassword(encodedPassword);
    }

    @Transactional
    public void deleteUser(Long userId, String accessToken, String refreshToken) {

        // 토큰 무효화
        if (refreshToken == null) {
            throw new BaseException(ErrorCode.TOKEN_MISSING);
        }

        // refresh 토큰 유효성 검사
        log.info("토큰 유효성 검사 시작");
        if (jwtUtil.validateToken(refreshToken)) {
            throw new BaseException(ErrorCode.INVALID_TOKEN);
        }

        // access 토큰에서 가져온 userId 와 refresh 토큰에서 가져온 userId 가 일치하는지 검증
        Long userIdFromAccessToken = jwtUtil.getUserIdFromToken(accessToken);
        Long userIdFromRefreshToken = jwtUtil.getUserIdFromToken(refreshToken);
        if (!userIdFromAccessToken.equals(userIdFromRefreshToken)) {
            throw new BaseException(ErrorCode.TOKEN_USER_MISMATCH);
        }

        // 유저 조회
        User user = findByIdAndIsDeletedFalseOrElseThrow(userId);

        // access 토큰 redis에 블랙리스트 추가
        jwtUtil.addToBlacklistAccessToken(accessToken);
        log.info("access 토큰을 블랙리스트에 추가");

        // refresh 토큰 redis에 블랙리스트 추가
        jwtUtil.addToBlacklistRefreshToken(refreshToken);
        log.info("refresh 토큰을 블랙리스트에 추가");

        // 유저 상태 변경
        user.deleteUser(true);
    }

    // user 가 없으면 예외 던지기
    public User findByIdAndIsDeletedFalseOrElseThrow(Long userId) {
        User user = userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND_USER));
        return user;
    }
}
