package caffeine.nest_dev.domain.profile.repository;

import caffeine.nest_dev.domain.keyword.entity.QKeyword;
import caffeine.nest_dev.domain.keyword.entity.QProfileKeyword;
import caffeine.nest_dev.domain.profile.entity.Profile;
import caffeine.nest_dev.domain.profile.entity.QProfile;
import caffeine.nest_dev.domain.user.entity.QUser;
import caffeine.nest_dev.domain.user.enums.UserRole;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ProfileRepositoryQueryImpl implements ProfileRepositoryQuery {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<Profile> searchMentorProfilesByKeyword(String keyword) {
        QProfile profile = QProfile.profile;
        QUser user = QUser.user;
        QProfileKeyword profileKeyword = QProfileKeyword.profileKeyword;
        QKeyword keywordEntity = QKeyword.keyword;

        return queryFactory
                .selectDistinct(profile)
                .from(profile)
                .join(profile.user, user)
                .leftJoin(profile.profileKeywords, profileKeyword)
                .leftJoin(profileKeyword.keyword, keywordEntity)
                .where(
                        user.userRole.eq(UserRole.MENTOR),
                        keyword != null && !keyword.isEmpty()
                                ? keywordEntity.name.containsIgnoreCase(keyword)
                                : null
                )
                .orderBy(profile.createdAt.desc())
                .fetch();
    }



}
