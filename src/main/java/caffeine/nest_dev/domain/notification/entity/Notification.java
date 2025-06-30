package caffeine.nest_dev.domain.notification.entity;

import caffeine.nest_dev.domain.chatroom.scheduler.enums.ChatRoomType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

/*
 * 사용자에게 전송되는 알림 정보 저장
 */
@Entity
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long receiverId;

    private Long chatRoomId;

    private Long reservationId;

    private String content;

    @Enumerated(EnumType.STRING)
    private ChatRoomType chatRoomType;

    @CreatedDate
    private LocalDateTime createdAt;

}
