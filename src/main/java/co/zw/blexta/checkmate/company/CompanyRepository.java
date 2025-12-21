package co.zw.blexta.checkmate.company;


import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, Long> {

	boolean existsByName(String name);

}
