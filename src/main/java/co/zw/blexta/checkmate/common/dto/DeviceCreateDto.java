package co.zw.blexta.checkmate.common.dto;

public record DeviceCreateDto(
        String name,
        String serialNumber,
        Long categoryId,
        //Long assetCodeId,
        String status
) {}
