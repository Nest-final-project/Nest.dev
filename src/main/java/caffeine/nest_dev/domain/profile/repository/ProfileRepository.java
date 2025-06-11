package caffeine.nest_dev.domain.profile.repository;

import caffeine.nest_dev.domain.profile.entity.Profile;
import java.util.List;
import java.util.Optional;
import org.antlr.v4.runtime.atn.SemanticContext.OR;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

public interface ProfileRepository extends JpaRepository<Profile, Long>, ProfileRepositoryQuery {

    Page<Profile> findByUserId(Long userId, Pageable pageable);
}
