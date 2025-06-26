package caffeine.nest_dev.common.aws;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AwsSesMailService {

    private final AmazonSimpleEmailService sesClient;

    @Value("${aws.ses.from-email}")
    private String fromEmail;

    /**
     * 회원가입 환영 메일 전송
     */
    public void sendWelcomeEmail(String userEmail) {
        try {
            SendEmailRequest request = new SendEmailRequest()
                    .withDestination(new Destination().withToAddresses(userEmail))
                    .withMessage(new Message()
                            .withSubject(new Content().withCharset("UTF-8").withData("회원가입을 축하합니다!"))
                            .withBody(new Body().withHtml(new Content()
                                    .withCharset("UTF-8")
                                    .withData("<h1>회원가입이 완료되었습니다 🎉</h1><p>서비스 이용을 환영합니다.</p>"))))
                    .withSource(fromEmail);

            sesClient.sendEmail(request);
            log.info("회원가입 환영 이메일 발송 완료: {}", userEmail);

        } catch (Exception e) {
            log.error("회원가입 메일 전송 실패: {}", e.getMessage(), e);
        }
    }

}
