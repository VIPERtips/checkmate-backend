package co.zw.blexta.checkmate.common.dto;

import co.zw.blexta.checkmate.device.Device;

public record DeviceDto(
        Long id,
        String name,
        String serialNumber,
        String status,
        String categoryName,
        String assetCodeValue
) {
    public DeviceDto(Device device) {
        this(
                device.getId(),
                device.getName(),
                device.getSerialNumber(),
                device.getStatus(),
                device.getCategory() != null ? device.getCategory().getName() : null,
                device.getAssetCode() != null ? device.getAssetCode().getCode() : null
        );
    }
}
