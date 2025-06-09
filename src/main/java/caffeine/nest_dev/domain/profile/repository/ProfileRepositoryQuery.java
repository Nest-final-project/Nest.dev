package caffeine.nest_dev.domain.profile.repository;

import caffeine.nest_dev.domain.profile.entity.Profile;
import java.util.List;

public interface ProfileRepositoryQuery {
    List<Profile> searchMentorProfilesByKeyword(String keyword);

}
