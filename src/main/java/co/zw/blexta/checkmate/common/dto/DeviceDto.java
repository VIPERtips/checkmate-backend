package co.zw.blexta.checkmate.common.dto;

import co.zw.blexta.checkmate.device.Device;
import co.zw.blexta.checkmate.device.assignments.DeviceAssignment;

public record DeviceDto(
        Long id,
        String name,
        String serialNumber,
        String status,
        String categoryName,
        String assetCodeValue,
        Long assignedToUserId,
        String assignedToFullName
) {
    public DeviceDto(Device device, DeviceAssignment currentAssignment) {
        this(
                device.getId(),
                device.getName(),
                device.getSerialNumber(),
                device.getStatus(),
                device.getCategory() != null ? device.getCategory().getName() : null,
                device.getAssetCode() != null ? device.getAssetCode().getCode() : null,
                currentAssignment != null && currentAssignment.getAssignedTo() != null
                        ? currentAssignment.getAssignedTo().getId() : null,
                currentAssignment != null && currentAssignment.getAssignedTo() != null
                        ? currentAssignment.getAssignedTo().getFullName() : null
        );
    }

    public DeviceDto(Device device) {
        this(device, null);
    }
}
