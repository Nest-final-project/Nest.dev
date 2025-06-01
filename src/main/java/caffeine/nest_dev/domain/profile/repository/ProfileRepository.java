package caffeine.nest_dev.domain.profile.repository;

import caffeine.nest_dev.domain.profile.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
}
