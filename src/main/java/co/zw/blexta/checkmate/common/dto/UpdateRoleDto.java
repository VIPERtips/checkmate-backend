package co.zw.blexta.checkmate.common.dto;

import java.util.List;
import java.util.Map;

public record UpdateRoleDto(
        Long id,
        String name,
        String description,
        Map<String, List<String>> permissions
) {}
