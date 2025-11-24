package co.zw.blexta.checkmate.common.dto;

import java.util.List;

public record PermissionDto(
        String resource,
        List<String> actions
) {}
