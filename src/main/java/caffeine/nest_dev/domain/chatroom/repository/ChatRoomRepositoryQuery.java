package caffeine.nest_dev.domain.chatroom.repository;

import caffeine.nest_dev.domain.chatroom.dto.response.ChatRoomReadDto;
import caffeine.nest_dev.domain.chatroom.dto.response.MessageDto;
import java.time.LocalDateTime;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface ChatRoomRepositoryQuery {

    Slice<MessageDto> findAllMessagesByChatRoomId(Long chatRoomId, Long messageId, Pageable pageable);

    Slice<ChatRoomReadDto> findAllByMentorIdOrMenteeId(Long userId, Long messageId, LocalDateTime cursorTime,
            Pageable pageable);
}
