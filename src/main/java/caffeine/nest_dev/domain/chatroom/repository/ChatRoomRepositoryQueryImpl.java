package caffeine.nest_dev.domain.chatroom.repository;

import static caffeine.nest_dev.domain.chatroom.entity.QChatRoom.chatRoom;
import static caffeine.nest_dev.domain.message.entity.QMessage.message;

import caffeine.nest_dev.domain.chatroom.dto.response.MessageDto;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

@RequiredArgsConstructor
public class ChatRoomRepositoryQueryImpl implements ChatRoomRepositoryQuery {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Slice<MessageDto> findAllMessagesByChatRoomId(Long chatRoomId, Long messageId, Pageable pageable) {
        List<MessageDto> results = jpaQueryFactory.select(Projections.fields(
                        MessageDto.class,
                        message.id.as("messageId"),
                        chatRoom.id.as("chatRoomId"),
                        message.mentor.id.as("mentorId"),
                        message.mentee.id.as("menteeId"),
                        message.sender.id.as("senderId"),
                        message.content.as("content"),
                        message.createdAt.as("sentAt")

                ))
                .from(message)
                .where(
                        message.chatRoom.id.eq(chatRoomId),
                        lastMessageId(messageId)
                )
                .leftJoin(message.chatRoom, chatRoom)
                .orderBy(message.createdAt.desc(), message.id.desc())   // 시간은 중복될 수 있음
                .limit(pageable.getPageSize() + 1)
                .fetch();

        return checkLastPage(pageable, results);
    }

    private BooleanExpression lastMessageId(Long messageId) {
        if (messageId == null || messageId == 0) {
            return null; // 조건 없이 최신부터 조회
        }
        return message.id.lt(messageId);
    }

    private Slice<MessageDto> checkLastPage(Pageable pageable, List<MessageDto> results) {
        boolean hasNext = false;

        if (results.size() > pageable.getPageSize()) {
            hasNext = true;
            results.remove(pageable.getPageSize());
        }
        return new SliceImpl<>(results, pageable, hasNext);
    }
}


