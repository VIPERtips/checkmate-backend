package co.zw.blexta.checkmate.device;


import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DeviceRepository extends JpaRepository<Device,Long> {
    @EntityGraph(attributePaths = {"assetCode"})
    Optional<Device> findByAssetCode_Code(String code);
    long countByStatus(String status);
    List<Device> findAllByCompanyId(Long companyId);
	long countByCompanyId(Long companyId);
	long countByStatusAndCompanyId(String string, Long companyId);

}
