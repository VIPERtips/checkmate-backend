package co.zw.blexta.checkmate.device.assignments;

import co.zw.blexta.checkmate.common.dto.DeviceAssignmentDto;
import co.zw.blexta.checkmate.common.exception.BadRequestException;
import co.zw.blexta.checkmate.common.exception.ResourceNotFoundException;
import co.zw.blexta.checkmate.device.Device;
import co.zw.blexta.checkmate.device.DeviceRepository;
import co.zw.blexta.checkmate.notification.EmailService;
import co.zw.blexta.checkmate.staff_users.User;
import co.zw.blexta.checkmate.staff_users.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeviceAssignmentServiceImpl implements DeviceAssignmentService {

    private final DeviceAssignmentRepository assignmentRepository;
    private final DeviceRepository deviceRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    @Override
    public DeviceAssignmentDto assignDevice(Long deviceId, Long assignedToUserId, Long assignedByUserId) {

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

        assignment = assignmentRepository.save(assignment);
        return toDto(assignment);
    }

    @Override
    public DeviceAssignmentDto checkInDevice(Long deviceId, Long performedByUserId) {

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

        assignment = assignmentRepository.save(assignment);
        return toDto(assignment);
    }

    @Override
    public List<DeviceAssignmentDto> getRecentAssignments(int limit) {
        return assignmentRepository.findTopByOrderByIdDesc(limit)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public List<DeviceAssignmentDto> getDeviceHistory(Long deviceId) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found"));

        return assignmentRepository.findByDeviceOrderByAssignmentDateDesc(device)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public List<DeviceAssignmentDto> getAssignedDevices() {
        return assignmentRepository.findCurrentlyAssignedDevices()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public DeviceAssignmentDto requestReturnDevice(Long deviceId, Long requestedByUserId) {

        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found"));

        User requester = userRepository.findById(requestedByUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        DeviceAssignment assignment = assignmentRepository.findLatestByDeviceId(deviceId)
                .orElseThrow(() -> new BadRequestException("No active assignment found for this device"));

        assignment.setReturnRequestedBy(requester);
        assignment.setReturnRequestedAt(java.time.LocalDateTime.now());
        assignmentRepository.save(assignment);

        // Send email to assigned user
        if (assignment.getAssignedTo() != null) {
            emailService.sendReminder(
                    assignment.getAssignedTo().getEmail(),
                    assignment.getAssignedTo().getFullName(),
                    assignment.getDevice().getName(),
                    "ASAP"
            );
        }

        return toDto(assignment);
    }


    private DeviceAssignmentDto toDto(DeviceAssignment assignment) {
        boolean isAssigned = assignment.getAssignedTo() != null;

        return DeviceAssignmentDto.builder()
                .id(assignment.getId())
                .deviceId(assignment.getDevice().getId())
                .deviceName(assignment.getDevice().getName())
                .assignedToUserId(isAssigned ? assignment.getAssignedTo().getId() : null)
                .assignedToFullName(isAssigned ? assignment.getAssignedTo().getFullName() : null)
                .assignedByUserId(assignment.getAssignedBy() != null ? assignment.getAssignedBy().getId() : null)
                .assignedByFullName(assignment.getAssignedBy() != null ? assignment.getAssignedBy().getFullName() : null)
                .status(assignment.getDevice().getStatus())
                .assignedAt(assignment.getAssignmentDate())
                .action(isAssigned ? "checked out" : "checked in")
                .build();
    }
}
