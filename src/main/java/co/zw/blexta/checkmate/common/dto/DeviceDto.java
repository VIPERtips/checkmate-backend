package co.zw.blexta.checkmate.common.dto;

import co.zw.blexta.checkmate.device.Device;
import co.zw.blexta.checkmate.device.assignments.DeviceAssignment;

public record DeviceDto(
        Long id,
        String name,
        String serialNumber,
        String status,

        Long companyId,

        Long categoryId,
        String categoryName,
        String categoryCode,
        String categoryScope,

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

                device.getCompany() != null ? device.getCompany().getId() : null,

                device.getCategory() != null ? device.getCategory().getId() : null,
                device.getCategory() != null ? device.getCategory().getName() : null,
                device.getCategory() != null ? device.getCategory().getCode() : null,
                device.getCategory() != null ? device.getCategory().getScope().name() : null,

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
