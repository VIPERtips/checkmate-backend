package co.zw.blexta.checkmate.common.dto;

import java.util.List;
import java.util.Map;

public record CreateRoleDto(
        String name,
        String description,
        Map<String, List<String>> permissions
) {}
