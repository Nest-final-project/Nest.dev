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

    /**
     * 예약 성공 시  메일 전송
     */
    public void sendReservationSuccessEmail(String userEmail, String mentorName, String startAt) {
        try {
            String subject = "상담 예약이 완료되었습니다!";
            String bodyHtml = String.format(
                    "<h1>상담 예약 완료 🎉</h1>" + "<p>%s 멘토님과의 상담이 %s에 예약되었습니다.</p>" + "<p>상담 시간 전에 미리 준비해주세요.</p>",
                    mentorName, startAt
            );

            SendEmailRequest request = new SendEmailRequest()
                    .withDestination(new Destination().withToAddresses(userEmail))
                    .withMessage(new Message()
                            .withSubject(new Content().withCharset("UTF-8").withData(subject))
                            .withBody(new Body().withHtml(new Content()
                                    .withCharset("UTF-8")
                                    .withData(bodyHtml))))
                    .withSource(fromEmail);

            sesClient.sendEmail(request);
            log.info("예약 성공 이메일 발송 완료: {}", userEmail);

        } catch (Exception e) {
            log.error("예약 메일 전송 실패: {}", e.getMessage(), e);
        }
    }


}
