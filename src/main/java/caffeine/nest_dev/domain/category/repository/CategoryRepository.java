package caffeine.nest_dev.domain.category.repository;

import caffeine.nest_dev.domain.category.entity.Category;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("select c from Category c where c.name = :name and c.isDeleted = false ")
    Optional<Category> findByName(@Param("name") String name);

    @Query("select c from Category c where c.isDeleted = false ")
    Page<Category> findAll(Pageable pageable);
}
