package caffeine.nest_dev.domain.profile.repository;

import caffeine.nest_dev.domain.career.entity.QCareer;
import caffeine.nest_dev.domain.career.enums.CareerStatus;
import caffeine.nest_dev.domain.category.entity.QCategory;
import caffeine.nest_dev.domain.keyword.dto.response.KeywordResponseDto;
import caffeine.nest_dev.domain.keyword.entity.Keyword;
import caffeine.nest_dev.domain.keyword.entity.QKeyword;
import caffeine.nest_dev.domain.keyword.entity.QProfileKeyword;
import caffeine.nest_dev.domain.profile.dto.response.RecommendedProfileResponseDto;
import caffeine.nest_dev.domain.profile.entity.Profile;
import caffeine.nest_dev.domain.profile.entity.QProfile;
import caffeine.nest_dev.domain.user.entity.QUser;
import caffeine.nest_dev.domain.user.enums.UserRole;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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
                .join(profile.user, user).fetchJoin()
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

    @Override
    public List<RecommendedProfileResponseDto> searchRecommendedMentorProfiles(Long categoryId) {
        QProfile profile = QProfile.profile;
        QUser user = QUser.user;
        QCategory category = QCategory.category;
        QCareer career = QCareer.career;
        QProfileKeyword profileKeyword = QProfileKeyword.profileKeyword;
        QKeyword keywordEntity = QKeyword.keyword;

        // 서로 다른 유저의 프로필 선택
        List<Tuple> userProfiles = queryFactory
                .select(
                        profile.id.max(),
                        profile.createdAt.max()

                ) // 한 유저의 여러 프로필 중 최신
                .from(career)
                .join(career.profile, profile)
                .join(profile.user, user)
                .where(
                        career.careerStatus.eq(CareerStatus.AUTHORIZED), // careerStatus 가 승인된 것만
                        profile.category.id.eq(categoryId) // 카테고리에 해당하는 profile 조회 조건
                )
                .groupBy(user.id) // 한 유저의 프로필만 나오지 않도록
                .orderBy(profile.createdAt.max().desc()) // 가장 최신 생성일 기준
                .limit(5)
                .fetch();

        // 조회된 프로필이 없으면 빈 리스트 즉시 반환
        if (userProfiles.isEmpty()) {
            return Collections.emptyList();
        }

        // 프로필 id 만 추출
        List<Long> profileIds = userProfiles.stream()
                .map(tuple -> tuple.get(0, Long.class))
                .toList();

        // 프로필 ID 에 해당하는 데이터 가져오기
        List<Tuple> mainProfiles = queryFactory
                .selectDistinct(
                        profile.id,
                        user.id,
                        user.name,
                        user.imgUrl,
                        profile.title,
                        category.name,
                        profile.createdAt
                )
                .from(profile)
                .join(profile.user, user)
                .join(profile.category, category)
                .where(
                        profile.id.in(profileIds)  // 위에서 지정한 프로필 3개만 가져오기
                )
                .orderBy(profile.createdAt.desc())
                .fetch();

        // 프로필에 관련된 keyword 조회
        List<Tuple> profileKeywordsRaw = queryFactory
                .select(
                        profileKeyword.profile.id,
                        keywordEntity // Keyword 엔티티 자체를 가져옵니다.
                )
                .from(profileKeyword)
                .join(profileKeyword.keyword, keywordEntity)
                .where(profileKeyword.profile.id.in(profileIds))
                .fetch();

        // 2. 가져온 결과를 Map<Long, List<Keyword>> 형태로 수동 그룹화합니다.
        Map<Long, List<Keyword>> keywordMap = profileKeywordsRaw.stream()
                .collect(Collectors.groupingBy(
                        tuple -> tuple.get(profileKeyword.profile.id), // Map의 키: profileId
                        Collectors.mapping(
                                tuple -> tuple.get(keywordEntity), // Map의 값: Keyword 엔티티
                                Collectors.toCollection(ArrayList::new) // 값을 List로 수집
                        )
                ));

        // 다양한 정보를 dto에 담기
        return mainProfiles.stream()
                .map(tuple -> {
                    Long profileId = tuple.get(profile.id);
                    Long userId = tuple.get(user.id);
                    String userName = tuple.get(user.name);
                    String userImgUrl = tuple.get(user.imgUrl);
                    String profileTitle = tuple.get(profile.title);
                    String categoryName = tuple.get(category.name);
                    List<Keyword> keywords = keywordMap.getOrDefault(profileId, Collections.emptyList());

                    // Keyword -> KeywordResponseDto 변환
                    List<KeywordResponseDto> keywordDtos = keywords.stream()
                            .map(k -> new KeywordResponseDto(k.getId(), k.getName()))
                            .collect(Collectors.toCollection(ArrayList::new));

                    return new RecommendedProfileResponseDto(
                            profileId,
                            userId,
                            userName,
                            profileTitle,
                            categoryName,
                            keywordDtos,  // 수정된 부분
                            userImgUrl
                    );
                })
                .collect(Collectors.toCollection(ArrayList::new));

    }


}
