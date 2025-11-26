package co.zw.blexta.checkmate.common.dto;

public record DeviceUpdateDto(
        String name,
        String serialNumber,
        Long categoryId,
        String status
) {}
