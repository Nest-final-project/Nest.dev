package caffeine.nest_dev.domain.notification.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NotificationEventType {
    CHAT_OPEN("chat-open"),
    CHAT_TERMINATION("chat-termination");

    private final String eventName;

}
