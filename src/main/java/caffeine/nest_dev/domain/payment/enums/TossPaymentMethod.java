package caffeine.nest_dev.domain.payment.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TossPaymentMethod {

    VIRTUAL_ACCOUNT("가상계좌"),
    EASY_PAYMENT("간편결제"),
    GAME_GIFT_CERTIFICATE("게임문화상품권"),
    ACCOUNT_TRANSFER("계좌이체"),
    BOOK_GIFT_CERTIFICATE("도서문화상품권"),
    CULTURE_GIFT_CERTIFICATE("문화상품권"),
    CARD("카드"),
    MOBILE("휴대폰");

    private final String description;

    // 직렬화 시 Enum 상수를 이 값으로 변환 (선택 사항)
    @JsonValue
    public String getDescription() {
        return description;
    }

    // 역직렬화 시 이 메소드를 사용하여 JSON 문자열을 Enum 상수로 변환
    @JsonCreator
    public static TossPaymentMethod from(String description) {
        return Arrays.stream(TossPaymentMethod.values())
                .filter(method -> method.getDescription().equals(description))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Unknown payment method: " + description));
    }
}
