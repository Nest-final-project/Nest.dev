package caffeine.nest_dev.domain.category.repository;

import caffeine.nest_dev.domain.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
