package co.zw.blexta.checkmate.device.assignments;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DeviceAssignmentRepository extends JpaRepository<DeviceAssignment, Long> {
    @Query(value = "SELECT * FROM device_assignment ORDER BY id DESC LIMIT ?1", nativeQuery = true)
    List<DeviceAssignment> findTopByOrderByIdDesc(int limit);

    @Query(value = "SELECT * FROM device_assignment WHERE device_id = :deviceId ORDER BY assignment_date DESC LIMIT 1", nativeQuery = true)
    Optional<DeviceAssignment> findLatestByDeviceId(Long deviceId);

    long countByAssignedTo_Id(Long userId);


}
