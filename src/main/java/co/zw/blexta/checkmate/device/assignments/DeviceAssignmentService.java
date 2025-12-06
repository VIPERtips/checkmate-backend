package co.zw.blexta.checkmate.device.assignments;

import co.zw.blexta.checkmate.common.dto.DeviceAssignmentDto;

import java.util.List;

public interface DeviceAssignmentService {

    DeviceAssignmentDto assignDevice(Long deviceId, Long assignedToUserId, Long assignedByUserId);

    DeviceAssignmentDto checkInDevice(Long deviceId, Long performedByUserId);
    List<DeviceAssignmentDto> getRecentAssignments(int limit);
    List<DeviceAssignmentDto> getDeviceHistory(Long deviceId);
    List<DeviceAssignmentDto> getAssignedDevices();
    DeviceAssignmentDto requestReturnDevice(Long deviceId, Long requestedByUserId);

}
