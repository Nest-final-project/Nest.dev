package caffeine.nest_dev.domain.keyword.repository;

import caffeine.nest_dev.domain.keyword.entity.Keyword;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface KeywordRepository extends JpaRepository<Keyword, Long> {

    @Query("select k from Keyword k where k.name = :name and k.isDeleted = false ")
    Optional<Keyword> findByName(@Param("name") String name);

    @Query("select k from Keyword k where k.isDeleted = false ")
    Page<Keyword> findAll(Pageable pageable);

    @Query("SELECT k FROM Keyword k WHERE k.isDeleted = false AND k.name LIKE %?1%")
    Page<Keyword> findByName(String name, Pageable pageable);
}
