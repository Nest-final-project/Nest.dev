package caffeine.nest_dev.domain.keyword.repository;

import caffeine.nest_dev.domain.keyword.entity.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KeywordRepository extends JpaRepository<Keyword, Long> {
}
