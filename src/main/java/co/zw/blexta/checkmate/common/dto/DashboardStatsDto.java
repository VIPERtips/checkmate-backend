package co.zw.blexta.checkmate.common.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record DashboardStatsDto(
        Long totalGadgets,
        Long activeUsers,
        Long upcomingEvents,
        Double utilizationRate,
        Long myGadgets
) {}
