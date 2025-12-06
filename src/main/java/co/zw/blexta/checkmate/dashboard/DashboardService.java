package co.zw.blexta.checkmate.dashboard;

import co.zw.blexta.checkmate.common.dto.DashboardStatsDto;
import co.zw.blexta.checkmate.common.dto.MonthlyTrendDto;
import co.zw.blexta.checkmate.device.DeviceRepository;
import co.zw.blexta.checkmate.device.assignments.DeviceAssignmentRepository;
import co.zw.blexta.checkmate.staff_users.UserRepository;
import co.zw.blexta.checkmate.staff_users.User;
import co.zw.blexta.checkmate.auth.users.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final DeviceRepository deviceRepository;
    private final DeviceAssignmentRepository assignmentRepository;
    private final UserRepository userRepository;

    public DashboardStatsDto getStats(AuthUser authUser) {
        boolean isAdmin = authUser.getRoles().stream().anyMatch(r -> r.getName().equalsIgnoreCase("admin"));
        boolean isManager = authUser.getRoles().stream().anyMatch(r -> r.getName().equalsIgnoreCase("manager"));

        long totalGadgets = deviceRepository.count();
        long activeUsers = userRepository.count();
        long upcomingEvents = 0;
        long assignedCount = deviceRepository.countByStatus("assigned");
        double utilizationRate = totalGadgets > 0 ? (assignedCount * 100.0 / totalGadgets) : 0;

        Long myGadgets = null;
        if (!isAdmin && !isManager) {
            myGadgets = Optional.ofNullable(authUser.getId())
                    .map(id -> {
                        Long count = assignmentRepository.countByAssignedTo_Id(id);
                        return count != null ? count : 0L;
                    })
                    .orElse(0L);
        }



        return DashboardStatsDto.builder()
                .totalGadgets(isAdmin || isManager ? totalGadgets : 0L)
                .activeUsers(isAdmin ? activeUsers : (isManager ? activeUsers : 0L))
                .upcomingEvents(isAdmin || isManager ? upcomingEvents : 0L)
                .utilizationRate(utilizationRate)
                .myGadgets(!isAdmin && !isManager ? myGadgets : 0L)
                .build();

    }

    public List<MonthlyTrendDto> getGadgetTrends() {
        List<Object[]> checkouts = assignmentRepository.getMonthlyCheckouts();
        List<Object[]> checkins = assignmentRepository.getMonthlyCheckins();

        Map<String, MonthlyTrendDto> trends = new LinkedHashMap<>();

        if (checkouts != null) {
            for (Object[] row : checkouts) {
                if (row == null || row.length < 2) continue;

                String raw = row[0] == null ? null : row[0].toString();
                long count = row[1] == null ? 0L : ((Number) row[1]).longValue();
                String month = formatMonthSafe(raw);

                trends.put(month, new MonthlyTrendDto(month, count, 0));
            }
        }

        if (checkins != null) {
            for (Object[] row : checkins) {
                if (row == null || row.length < 2) continue;

                String raw = row[0] == null ? null : row[0].toString();
                long count = row[1] == null ? 0L : ((Number) row[1]).longValue();
                String month = formatMonthSafe(raw);

                trends.compute(month, (k, existing) ->
                        existing == null
                                ? new MonthlyTrendDto(month, 0, count)
                                : new MonthlyTrendDto(month, existing.getCheckouts(), count)
                );
            }
        }

        return new ArrayList<>(trends.values());
    }

    private String formatMonthSafe(String raw) {
        if (raw == null || raw.isBlank()) {
            var now = LocalDate.now();
            return now.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH) + " " + now.getYear();
        }

        String[] parts = raw.split("-");
        if (parts.length < 2) return raw;

        try {
            int year = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            return Month.of(month).getDisplayName(TextStyle.SHORT, Locale.ENGLISH) + " " + year;
        } catch (Exception e) {
            return raw;
        }
    }
}
