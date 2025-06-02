package caffeine.nest_dev.domain.user.service;

import caffeine.nest_dev.common.config.PasswordEncoder;
import caffeine.nest_dev.domain.user.dto.request.UpdatePasswordRequestDto;
import caffeine.nest_dev.domain.user.dto.request.UserRequestDto;
import caffeine.nest_dev.domain.user.dto.response.UserResponseDto;
import caffeine.nest_dev.domain.user.entity.User;
import caffeine.nest_dev.domain.user.enums.UserRole;
import caffeine.nest_dev.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public UserResponseDto findById(Long userId) {

        // 유저 조회
        User user = userRepository.findByIdOrElseThrow(userId);

        return UserResponseDto.of(user);
    }

    @Transactional
    public void updateUser(User user, UserRequestDto dto) {

        // dto 가 null 일 때
        if (dto == null) {
            throw new IllegalArgumentException("수정하려는 항목 중 하나는 필수 입력값입니다.");
        }

        if (dto.getEmail() != null) {
            // 이메일 중복 검증
            if (userRepository.existsByEmail(dto.getEmail())) {
                throw new IllegalArgumentException("중복된 이메일 입니다.");
            }

            user.updateEmail(dto.getEmail());
        }

        if (dto.getNickName() != null) {
            user.updateNickName(dto.getNickName());
        }

        if (dto.getPhoneNumber() != null) {
            user.updatePhoneNumber(dto.getPhoneNumber());
        }

        // 멘토일 경우 추가 수정
        if (user.getUserRole() == UserRole.MENTOR) {
            if (dto.getBank() != null) {
                user.updateBank(dto.getBank());
            }

            if (dto.getAccountNumber() != null) {
                user.updateAccountNumber(dto.getAccountNumber());
            }
        }

        // 바꾼 유저 정보 바탕으로 저장
        userRepository.save(user);
    }

    @Transactional
    public void updatePassword(User user, UpdatePasswordRequestDto dto) {

        // 비밀 번호 검증
        if (!passwordEncoder.matches(dto.getRawPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 새로운 비밀번호가 현재 비밀번호와 같은 경우
        if (dto.getNewPassword().equals(dto.getRawPassword())) {
            throw new IllegalArgumentException("같은 비밀번호로 변경할 수 없습니다.");
        }

        // 새 비밀번호 인코딩
        String encodedPassword = passwordEncoder.encode(dto.getNewPassword());

        // 바꾼 비밀번호 저장
        user.updatePassword(encodedPassword);
        userRepository.save(user);
    }
}
