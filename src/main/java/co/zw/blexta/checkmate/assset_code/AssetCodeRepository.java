package co.zw.blexta.checkmate.assset_code;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AssetCodeRepository extends JpaRepository<AssetCode,Long> {
    boolean existsByCode(String code);
}
