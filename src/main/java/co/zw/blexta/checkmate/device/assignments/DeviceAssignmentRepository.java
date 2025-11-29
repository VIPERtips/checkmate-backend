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
    @Query(value = """
    	    SELECT DATE_FORMAT(assignment_date, '%Y-%m') AS month,
    	           COUNT(*) AS count
    	    FROM device_assignment
    	    WHERE assigned_to_id IS NOT NULL
    	    GROUP BY DATE_FORMAT(assignment_date, '%Y-%m')
    	    ORDER BY month ASC
    	""", nativeQuery = true)
    	List<Object[]> getMonthlyCheckouts();


    	@Query(value = """
    	    SELECT DATE_FORMAT(da.assignment_date, '%Y-%m') AS month,
    	           COUNT(*) AS count
    	    FROM device_assignment da
    	    JOIN device d ON da.device_id = d.id
    	    WHERE d.status = 'available'
    	    GROUP BY DATE_FORMAT(da.assignment_date, '%Y-%m')
    	    ORDER BY month ASC
    	""", nativeQuery = true)
    	List<Object[]> getMonthlyCheckins();



}
