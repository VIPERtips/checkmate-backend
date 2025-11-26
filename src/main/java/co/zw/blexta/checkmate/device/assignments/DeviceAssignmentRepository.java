package co.zw.blexta.checkmate.device.assignments;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DeviceAssignmentRepository extends JpaRepository<DeviceAssignment, Long> {
    @Query(value = "SELECT * FROM device_assignment ORDER BY id DESC LIMIT ?1", nativeQuery = true)
    List<DeviceAssignment> findTopByOrderByIdDesc(int limit);
}
