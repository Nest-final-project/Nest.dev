package caffeine.nest_dev.domain.email;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final StringRedisTemplate stringRedisTemplate;

    @Value("${auth.code.expiration-millis}")
    private long authCodeExpiration;

    public void sendAuthCodeEmail(String toEmail) {
        // 6자리 랜덤 코드 생성
        String authCode = generateRandomCode();

        // 인증 코드 redis에 저장
        stringRedisTemplate.opsForValue()
                .set(toEmail, authCode, authCodeExpiration, TimeUnit.MILLISECONDS);

        // 고정된 제목, 내용 생성 해서 메일 보내기
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("회원가입 인증 코드입니다.");
        String emailContent =
                "안녕하세요!\n\n"
                        + "귀하의 회원가입을 위한 인증 코드는 다음과 같습니다.:\n\n"
                        + "인증 코드: "
                        + authCode + "\n\n"
                        + "이 코드는 "
                        + (authCodeExpiration / 60000)
                        + "분 동안 유효합니다.\n"
                        + "인증 코드를 입력하여 회원가입을 완료해주세요.\n\n"
                        + "감사합니다.\n";
        message.setText(emailContent);
        javaMailSender.send(message);
    }

    // 인증 코드 검증
    public boolean checkAuthCode(String email, String authCode) {
        String redisAuthCode = stringRedisTemplate.opsForValue().get(email);

        if (redisAuthCode == null) {
            return false;
        }

        if (!redisAuthCode.equals(authCode)) {
            return false;
        }

        stringRedisTemplate.delete(email);
        return true;
    }

    /**
     * 6자리 숫자 문자열
     */
    private String generateRandomCode() {
        Random random = new Random();
        int min = 100000; // 6자리 숫자의 최솟값
        int max = 999999; // 6자리 숫자의 최대값
        int randomNumber = random.nextInt(max - min + 1) + min;
        return String.valueOf(randomNumber);
    }

}
