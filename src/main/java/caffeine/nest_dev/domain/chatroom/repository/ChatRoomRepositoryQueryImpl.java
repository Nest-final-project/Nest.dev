package caffeine.nest_dev.domain.chatroom.repository;

import static caffeine.nest_dev.domain.chatroom.entity.QChatRoom.chatRoom;
import static caffeine.nest_dev.domain.message.entity.QMessage.message;

import caffeine.nest_dev.domain.chatroom.dto.response.ChatRoomReadDto;
import caffeine.nest_dev.domain.chatroom.dto.response.MessageDto;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

@RequiredArgsConstructor
public class ChatRoomRepositoryQueryImpl implements ChatRoomRepositoryQuery {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Slice<MessageDto> findAllMessagesByChatRoomId(Long chatRoomId, Long messageId, Long currentUserId,
            Pageable pageable) {
        List<MessageDto> results = jpaQueryFactory.select(Projections.fields(
                        MessageDto.class,
                        message.id.as("messageId"),
                        chatRoom.id.as("chatRoomId"),
                        message.mentor.id.as("mentorId"),
                        message.mentee.id.as("menteeId"),
                        message.sender.id.as("senderId"),
                        message.content.as("content"),
                        message.createdAt.as("sentAt"),
                        ExpressionUtils.as(
                                message.sender.id.eq(currentUserId), "isMine"
                        )
                ))
                .from(message)
                .where(
                        message.chatRoom.id.eq(chatRoomId),
                        lastMessageId(messageId)
                )
                .orderBy(message.createdAt.desc(), message.id.desc())   // 시간은 중복될 수 있음
                .limit(pageable.getPageSize() + 1)
                .fetch();

        return checkLastPage(pageable, results);
    }

    @Override
    public Slice<ChatRoomReadDto> findAllByMentorIdOrMenteeId(Long userId, Long messageId, LocalDateTime cursorTime,
            Pageable pageable) {

        // 서브쿼리로 각 채팅방의 마지막 메시지 정보 조회
        List<ChatRoomReadDto> results = jpaQueryFactory.select(Projections.fields(
                        ChatRoomReadDto.class,
                        chatRoom.id.as("roomId"),
                        chatRoom.mentor.id.as("mentorId"),
                        chatRoom.mentor.name.as("mentorName"),
                        chatRoom.mentee.id.as("menteeId"),
                        chatRoom.mentee.name.as("menteeName"),
                        chatRoom.reservation.id.as("reservationId"),
                        // 마지막 메시지 정보 추가
                        message.content.as("lastMessageContent"),
                        message.createdAt.as("lastMessageTime"),
                        message.sender.id.as("lastMessageSenderId")
                ))
                .from(chatRoom)
                .leftJoin(message).on(
                        message.chatRoom.id.eq(chatRoom.id)
                                .and(message.id.eq(
                                        // 각 채팅방의 가장 최신 메시지 ID를 찾는 서브쿼리
                                        jpaQueryFactory.select(message.id.max())
                                                .from(message)
                                                .where(message.chatRoom.id.eq(chatRoom.id))
                                ))
                )
                .where(
                        chatRoom.mentor.id.eq(userId).or(chatRoom.mentee.id.eq(userId)),
                        gtUpdateAt(cursorTime, messageId)
                )
                .orderBy(chatRoom.updatedAt.desc(),
                        chatRoom.id.desc())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        return checkLastPage(pageable, results);
    }

    private BooleanExpression lastMessageId(Long messageId) {
        if (messageId == null || messageId == 0L) {
            return null; // 조건 없이 최신부터 조회
        }
        return message.id.lt(messageId);
    }

    private BooleanExpression gtUpdateAt(LocalDateTime cursorTime, Long messageId) {
        if (cursorTime == null || messageId == null || messageId == 0L) {
            return null;
        }
        return chatRoom.updatedAt.lt(cursorTime)
                .or(chatRoom.updatedAt.eq(cursorTime).and(chatRoom.id.lt(messageId)));
    }

    private <T> Slice<T> checkLastPage(Pageable pageable, List<T> results) {
        boolean hasNext = false;

        if (results.size() > pageable.getPageSize()) {
            hasNext = true;
            results.remove(pageable.getPageSize());
        }
        return new SliceImpl<>(results, pageable, hasNext);
    }
}


