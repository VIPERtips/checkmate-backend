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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DeviceAssignmentServiceImpl implements DeviceAssignmentService {

    private final DeviceAssignmentRepository assignmentRepository;
    private final DeviceRepository deviceRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    private boolean isSuperAdmin(User user) {
        return user.getAuthUser().getRoles()
                .stream()
                .anyMatch(r -> r.getName().equals("SUPERADMIN"));
    }

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
        deviceRepository.saveAndFlush(device); 

        DeviceAssignment assignment = DeviceAssignment.builder()
                .device(device) 
                .assignedTo(assignedTo)
                .assignedBy(assignedBy)
                .company(device.getCompany())
                .build();

        return toDto(assignmentRepository.saveAndFlush(assignment));
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
        deviceRepository.saveAndFlush(device);

        DeviceAssignment assignment = DeviceAssignment.builder()
                .device(device)
                .assignedBy(checker)
                .company(device.getCompany())
                .build();

        return toDto(assignmentRepository.saveAndFlush(assignment));
    }


    @Override
    public List<DeviceAssignmentDto> getRecentAssignments(int limit, Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<DeviceAssignment> assignments;

        if (isSuperAdmin(user)) {
            assignments = assignmentRepository.findTopByOrderByIdDesc(limit);
        } else {
            if (user.getCompany() == null)
                throw new BadRequestException("User has no company");

            assignments = assignmentRepository
                    .findRecentByCompanyId(user.getCompany().getId())
                    .stream()
                    .limit(limit)
                    .toList();
        }

        return assignments.stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public List<DeviceAssignmentDto> getDeviceHistory(Long deviceId, Long userId) {

        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!isSuperAdmin(user)) {
            if (user.getCompany() == null ||
                !user.getCompany().getId().equals(device.getCompany().getId())) {
                throw new BadRequestException("Access denied");
            }
        }

        return assignmentRepository.findByDeviceOrderByAssignmentDateDesc(device)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public List<DeviceAssignmentDto> getAssignedDevices(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<DeviceAssignment> assignments;

        if (isSuperAdmin(user)) {
            assignments = assignmentRepository.findCurrentlyAssignedDevices();
        } else {
            if (user.getCompany() == null)
                throw new BadRequestException("User has no company");

            assignments = assignmentRepository
                    .findCurrentlyAssignedDevicesByCompanyId(user.getCompany().getId());
        }

        return assignments.stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public DeviceAssignmentDto requestReturnDevice(Long deviceId, Long requestedByUserId) {

        DeviceAssignment assignment = assignmentRepository.findLatestByDeviceId(deviceId)
                .orElseThrow(() -> new BadRequestException("No active assignment found"));

        User requester = userRepository.findById(requestedByUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!isSuperAdmin(requester)) {
            if (requester.getCompany() == null ||
                !requester.getCompany().getId().equals(assignment.getCompany().getId())) {
                throw new BadRequestException("Access denied");
            }
        }

        assignment.setReturnRequestedBy(requester);
        assignment.setReturnRequestedAt(LocalDateTime.now());
        assignmentRepository.save(assignment);

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
        boolean assigned = assignment.getAssignedTo() != null;

        return DeviceAssignmentDto.builder()
                .id(assignment.getId())
                .deviceId(assignment.getDevice().getId())
                .deviceName(assignment.getDevice().getName())
                .assignedToUserId(assigned ? assignment.getAssignedTo().getId() : null)
                .assignedToFullName(assigned ? assignment.getAssignedTo().getFullName() : null)
                .assignedByUserId(
                        assignment.getAssignedBy() != null
                                ? assignment.getAssignedBy().getId()
                                : null
                )
                .assignedByFullName(
                        assignment.getAssignedBy() != null
                                ? assignment.getAssignedBy().getFullName()
                                : null
                )
                .status(assignment.getDevice().getStatus())
                .assignedAt(assignment.getAssignmentDate())
                .action(assigned ? "checked out" : "checked in")
                .build();
    }
}
