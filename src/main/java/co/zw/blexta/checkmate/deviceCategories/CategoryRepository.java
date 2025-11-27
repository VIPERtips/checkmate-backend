package co.zw.blexta.checkmate.deviceCategories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByName(String name);
    boolean existsByCode(String code);
    Optional<Category> findByName(String name);
    @Query("SELECT COUNT(d) FROM Device d WHERE d.category.id = :categoryId")
    Long countDevices(Long categoryId);
}
