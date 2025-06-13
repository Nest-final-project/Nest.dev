package caffeine.nest_dev.domain.consultation.repository;

import caffeine.nest_dev.domain.consultation.entity.QConsultation;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ConsultationQueryRepositoryImpl implements ConsultationQueryRepository {

    private final JPAQueryFactory queryFactory;

    QConsultation qConsultation = QConsultation.consultation;

    @Override
    public boolean existsConsultation(Long userId, LocalDateTime startAt, LocalDateTime endAt) {
        Integer fetchOne = queryFactory
                .selectOne()
                .from(qConsultation)
                .where(
                        qConsultation.mentor.id.eq(userId),
                        qConsultation.startAt.lt(endAt),
                        qConsultation.endAt.gt(startAt)
                )
                .fetchFirst();

        return fetchOne != null;
    }
}
