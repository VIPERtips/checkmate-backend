package co.zw.blexta.checkmate.device.category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import co.zw.blexta.checkmate.company.Company;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByNameAndCompany(String name, Company company);
    boolean existsByCodeAndCompany(String code, Company company);
    Optional<Category> findByNameAndCompany(String name, Company company);

    @Query("SELECT COUNT(d) FROM Device d WHERE d.category.id = :categoryId")
    Long countDevices(Long categoryId);

    List<Category> findAllByScopeOrCompany(CategoryScope scope, Company company);
    List<Category> findAllByCompany(Company company);
}
