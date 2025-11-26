package co.zw.blexta.checkmate.device.assignments;

import co.zw.blexta.checkmate.common.exception.BadRequestException;
import co.zw.blexta.checkmate.common.exception.ResourceNotFoundException;
import co.zw.blexta.checkmate.device.Device;
import co.zw.blexta.checkmate.device.DeviceRepository;
import co.zw.blexta.checkmate.staff_users.User;
import co.zw.blexta.checkmate.staff_users.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeviceAssignmentServiceImpl implements DeviceAssignmentService {

    private final DeviceAssignmentRepository assignmentRepository;
    private final DeviceRepository deviceRepository;
    private final UserRepository userRepository;

    @Override
    public DeviceAssignment assignDevice(Long deviceId, Long assignedToUserId, Long assignedByUserId) {

        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found"));

        User assignedTo = userRepository.findById(assignedToUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Assigned-to user not found"));

        User assignedBy = userRepository.findById(assignedByUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Assigned-by user not found"));

        if (!device.getStatus().equalsIgnoreCase("available"))
            throw new BadRequestException("Device is not available");

        device.setStatus("assigned");
        deviceRepository.save(device);

        DeviceAssignment assignment = DeviceAssignment.builder()
                .device(device)
                .assignedTo(assignedTo)
                .assignedBy(assignedBy)
                .build();

        return assignmentRepository.save(assignment);
    }

    @Override
    public DeviceAssignment checkInDevice(Long deviceId, Long performedByUserId) {

        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found"));

        User checker = userRepository.findById(performedByUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!device.getStatus().equalsIgnoreCase("assigned"))
            throw new BadRequestException("Device is not currently assigned");

        device.setStatus("available");
        deviceRepository.save(device);

        DeviceAssignment assignment = DeviceAssignment.builder()
                .device(device)
                .assignedTo(null)
                .assignedBy(checker)
                .build();

        return assignmentRepository.save(assignment);
    }
}
