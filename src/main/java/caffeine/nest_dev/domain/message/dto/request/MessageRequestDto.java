package caffeine.nest_dev.domain.message.dto.request;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MessageRequestDto {

    private Long roomId;
    private Long senderId;
    private Long receiverId;
    private String content;
    private LocalDateTime sentAt;

}
