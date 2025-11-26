package co.zw.blexta.checkmate.deviceCategories;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByName(String name);
    boolean existsByCode(String code);
    Optional<Category> findByName(String name);
}
