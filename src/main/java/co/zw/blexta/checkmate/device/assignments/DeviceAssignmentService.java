package co.zw.blexta.checkmate.device.assignments;

public interface DeviceAssignmentService {

    DeviceAssignment assignDevice(Long deviceId, Long assignedToUserId, Long assignedByUserId);

    DeviceAssignment checkInDevice(Long deviceId, Long performedByUserId);
}
