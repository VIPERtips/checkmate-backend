package co.zw.blexta.checkmate.common.dto;

public record CategoryDto(
        Long id,
        String name,
        String code,
        Long deviceCount
) {
}

