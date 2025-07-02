package caffeine.nest_dev.domain.user.service;

import caffeine.nest_dev.common.config.JwtUtil;
import caffeine.nest_dev.common.config.PasswordEncoder;
import caffeine.nest_dev.common.enums.ErrorCode;
import caffeine.nest_dev.common.exception.BaseException;
import caffeine.nest_dev.domain.auth.dto.request.DeleteRequestDto;
import caffeine.nest_dev.domain.auth.dto.response.LoginResponseDto;
import caffeine.nest_dev.domain.auth.repository.TokenRepository;
import caffeine.nest_dev.domain.profile.dto.response.ProfileImageResponseDto;
import caffeine.nest_dev.domain.s3.S3Service;
import caffeine.nest_dev.domain.user.dto.request.ExtraInfoRequestDto;
import caffeine.nest_dev.domain.user.dto.request.UpdatePasswordRequestDto;
import caffeine.nest_dev.domain.user.dto.request.UserRequestDto;
import caffeine.nest_dev.domain.user.dto.response.ProfileImageUploadResponseDto;
import caffeine.nest_dev.domain.user.dto.response.UserInfoResponseDto;
import caffeine.nest_dev.domain.user.dto.response.UserResponseDto;
import caffeine.nest_dev.domain.user.entity.User;
import caffeine.nest_dev.domain.user.enums.SocialType;
import caffeine.nest_dev.domain.user.enums.UserGrade;
import caffeine.nest_dev.domain.user.enums.UserRole;
import caffeine.nest_dev.domain.user.repository.UserRepository;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final TokenRepository tokenRepository;

    private final RedisTemplate<String, String> imgTemplate;
    private final S3Service s3Service;

    private static final String CACHE_KEY_PREFIX = "profile_image:";
    private static final String NO_IMAGE_MARKER = "NO_IMAGE";

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    @Transactional(readOnly = true)
    public UserResponseDto findUser(Long userId) {

        // 유저 조회
        User user = findByIdAndIsDeletedFalseOrElseThrow(userId);

        return UserResponseDto.of(user);
    }

    @Transactional(readOnly = true)
    public UserInfoResponseDto getUserById(Long userId) {
        User user = findByIdAndIsDeletedFalseOrElseThrow(userId);

        return UserInfoResponseDto.of(user.getId(), user.getName(), user.getUserRole());
    }

    @Transactional
    public void updateUser(Long userId, UserRequestDto dto) {

        // dto 가 null 일 때
        if (dto == null) {
            throw new BaseException(ErrorCode.EMPTY_UPDATE_REQUEST);
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

        // 소셜 로그인 회원은 예외 발생
        if (!SocialType.LOCAL.equals(user.getSocialType())) {
            throw new BaseException(ErrorCode.NOT_LOCAL_USER);
        }

        // 비밀 번호 검증
        if (!passwordEncoder.matches(dto.getRawPassword(), user.getPassword())) {
            throw new BaseException(ErrorCode.INVALID_PASSWORD);
        }

        // 새로운 비밀번호가 현재 비밀번호와 같은 경우
        if (dto.getNewPassword().equals(dto.getRawPassword())) {
            throw new BaseException(ErrorCode.NEW_PASSWORD_SAME_AS_CURRENT);
        }

        // 새 비밀번호 인코딩
        String encodedPassword = passwordEncoder.encode(dto.getNewPassword());

        // 비밀번호 변경
        user.updatePassword(encodedPassword);
    }

    @Transactional
    public LoginResponseDto updateExtraInfo(ExtraInfoRequestDto dto) {

        // 유저 조회
        User user = findByIdAndIsDeletedFalseOrElseThrow(dto.getId());

        if (dto.getUserRole() == null || dto.getPhoneNumber() == null || dto.getName() == null) {
            throw new BaseException(ErrorCode.EXTRA_INFO_REQUIRED);
        }

        // MENTEE 일때
        if (dto.getUserRole().equals(UserRole.MENTEE)) {
            user.updateUserGrade(UserGrade.SEED);
            user.updateExtraInfo(dto);
            user.updateTotalPrice(0);
        }

        // MENTOR 일때
        if (dto.getUserRole().equals(UserRole.MENTOR)) {
            user.updateExtraInfo(dto);
        }

        // accessToken 발급
        String accessToken = jwtUtil.createAccessToken(user);
        // refreshToken 발급
        String refreshToken = jwtUtil.createRefreshToken(user);

        // redis 에 refreshToken 저장
        tokenRepository.save(user.getId(), refreshToken, refreshTokenExpiration);

        return LoginResponseDto.of(user, accessToken, refreshToken);
    }

    @Transactional
    public void deleteUser(Long userId, String accessToken, DeleteRequestDto dto) {

        // 토큰 무효화
        if (dto.getRefreshToken() == null) {
            throw new BaseException(ErrorCode.TOKEN_MISSING);
        }

        // refresh 토큰 유효성 검사
        log.info("토큰 유효성 검사 시작");
        if (!jwtUtil.validateToken(dto.getRefreshToken())) {
            throw new BaseException(ErrorCode.INVALID_TOKEN);
        }

        String resolvedAccessToken = jwtUtil.resolveToken(accessToken);

        // access 토큰에서 가져온 userId 와 refresh 토큰에서 가져온 userId 가 일치하는지 검증
        Long userIdFromAccessToken = jwtUtil.getUserIdFromToken(resolvedAccessToken);
        Long userIdFromRefreshToken = jwtUtil.getUserIdFromToken(dto.getRefreshToken());
        if (!userIdFromAccessToken.equals(userIdFromRefreshToken)) {
            throw new BaseException(ErrorCode.TOKEN_USER_MISMATCH);
        }

        // refreshToken 일치 여부 검증
        String refreshTokenByUserId = tokenRepository.findByUserId(userIdFromRefreshToken);
        if (!dto.getRefreshToken().equals(refreshTokenByUserId)) {
            throw new BaseException(ErrorCode.INVALID_TOKEN);
        }

        // 유저 조회
        User user = findByIdAndIsDeletedFalseOrElseThrow(userId);

        // 비밀번호 일치 검증
        if (SocialType.LOCAL.equals(user.getSocialType())) {
            if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
                throw new BaseException(ErrorCode.INVALID_PASSWORD);
            }
        }

        // access 토큰 redis에 블랙리스트 추가
        jwtUtil.addToBlacklistAccessToken(resolvedAccessToken);
        log.info("access 토큰을 블랙리스트에 추가");

        // refresh 토큰 redis에 블랙리스트 추가
        jwtUtil.addToBlacklistRefreshToken(dto.getRefreshToken());
        log.info("refresh 토큰을 블랙리스트에 추가");

        // 유저 상태 변경
        user.deleteUser(true);
    }

    // user 가 없으면 예외 던지기
    public User findByIdAndIsDeletedFalseOrElseThrow(Long userId) {
        return userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));
    }

    // 총 결제 금액으로 등급 산정
    @Scheduled(cron = "59 59 23 L * ?")
    @Transactional
    public void runOnLastDayOfMonth() {
        List<User> users = userRepository.findAll();

        for (User user : users) {

            Integer totalPrice = user.getTotalPrice();

            if (totalPrice < 20000) {
                // 20,000원 미만은 SEED
                user.updateUserGrade(UserGrade.SEED);
            } else if (totalPrice <= 39999) {
                // 20,000원 ~ 39,999원 -> SPROUT
                user.updateUserGrade(UserGrade.SPROUT);
            } else if (totalPrice <= 59999) {
                // 40,000원 ~ 59,999원 -> BRANCH
                user.updateUserGrade(UserGrade.BRANCH);
            } else if (totalPrice <= 79999) {
                // 60,000원 ~ 79,999원 -> BLOOM
                user.updateUserGrade(UserGrade.BLOOM);
            } else {
                // 80,000원 이상 -> NEST
                user.updateUserGrade(UserGrade.NEST);
            }
        }
    }

    // 이미지 조회
    @Transactional(readOnly = true)
    public ProfileImageResponseDto getUserProfileImage(Long id) {
        User user = findByIdAndIsDeletedFalseOrElseThrow(id);

        Long userId = user.getId();
        String key = CACHE_KEY_PREFIX + user.getId();
        String cached = imgTemplate.opsForValue().get(key);

        // 캐시 조회 - 존재하면 바로 반환
        if (cached != null) {
            String cachedUrl = NO_IMAGE_MARKER.equals(cached) ? null : cached;
            return ProfileImageResponseDto.of(userId, cachedUrl);
        }

        String imgUrl = user.getImgUrl();

        // 캐시 저장
        String valueToCache = imgUrl != null ? imgUrl : NO_IMAGE_MARKER;
        imgTemplate.opsForValue().set(key, valueToCache, Duration.ofHours(1));

        String responseUrl = NO_IMAGE_MARKER.equals(valueToCache) ? null : valueToCache;
        return ProfileImageResponseDto.of(userId, responseUrl);
    }

    // 이미지 저장
    @Transactional
    public ProfileImageUploadResponseDto saveImg(Long id, MultipartFile file) {
        User user = findByIdAndIsDeletedFalseOrElseThrow(id);

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("프로필 사진이 없습니다.");
        }
        if (user.getImgUrl() != null) {
            throw new IllegalArgumentException("이미 등록되었습니다.");
        }
        try {
            String folder = "profile/";
            String fileUrl = s3Service.uploadFile(file, folder);
            user.saveImg(fileUrl);
            // 캐시 무효화
            String key = CACHE_KEY_PREFIX + user.getId();
            imgTemplate.delete(key);
            return new ProfileImageUploadResponseDto(fileUrl);
        } catch (IOException e) {
            throw new BaseException(ErrorCode.S3_UPLOAD_FAILED);
        }
    }

    @Transactional
    public void deleteImage(Long userId) {
        User user = findByIdAndIsDeletedFalseOrElseThrow(userId);
        String imgUrl = user.getImgUrl();

        // s3 삭제
        if (imgUrl != null && !imgUrl.isEmpty()) {
            s3Service.deleteFile(imgUrl);
        }
        user.removeImg();
        // 캐시 삭제
        String key = CACHE_KEY_PREFIX + user.getId();
        imgTemplate.delete(key);
    }

    @Transactional
    public ProfileImageUploadResponseDto updateImage(Long userId, MultipartFile file) {
        User user = findByIdAndIsDeletedFalseOrElseThrow(userId);
        String imgUrl = user.getImgUrl();

        // s3 삭제
        if (imgUrl != null && !imgUrl.isEmpty()) {
            s3Service.deleteFile(imgUrl);
        }

        try {
            String folder = "profile/";
            String fileUrl = s3Service.uploadFile(file, folder);

            user.saveImg(fileUrl);

            // 캐시 무효화
            String key = CACHE_KEY_PREFIX + user.getId();
            imgTemplate.delete(key);

            return new ProfileImageUploadResponseDto(fileUrl);
        } catch (IOException e) {
            throw new BaseException(ErrorCode.S3_UPLOAD_FAILED);
        }
    }
}
