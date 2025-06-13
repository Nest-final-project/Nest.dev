package caffeine.nest_dev.domain.chatroom.repository;

import caffeine.nest_dev.domain.chatroom.dto.response.MessageDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface ChatRoomRepositoryQuery {

    Slice<MessageDto> findAllMessagesByChatRoomId(Long chatRoomId, Long messageId, Pageable pageable);

}
