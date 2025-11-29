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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
        long upcomingEvents = 0; // replace with your events logic

        // Utilization = % of assigned gadgets
        long assignedCount = deviceRepository.countByStatus("assigned");
        double utilizationRate = totalGadgets > 0 ? (assignedCount * 100.0 / totalGadgets) : 0;

        // For staff, show their own gadgets
        long myGadgets = 0;
        if (!isAdmin && !isManager) {
            myGadgets = assignmentRepository.countByAssignedTo_Id(authUser.getId());
        }

        // Only return relevant stats per role
        return DashboardStatsDto.builder()
                .totalGadgets(isAdmin || isManager ? totalGadgets : null)
                .activeUsers(isAdmin ? activeUsers : (isManager ? activeUsers : null))
                .upcomingEvents(isAdmin || isManager ? upcomingEvents : null)
                .utilizationRate(utilizationRate)
                .myGadgets(!isAdmin && !isManager ? myGadgets : null)
                .build();
    }
    
    public List<MonthlyTrendDto> getGadgetTrends() {

        List<Object[]> checkouts = assignmentRepository.getMonthlyCheckouts();
        List<Object[]> checkins = assignmentRepository.getMonthlyCheckins();

        Map<String, MonthlyTrendDto> trends = new LinkedHashMap<>();

        // Add checkouts data
        for (Object[] row : checkouts) {
        	String month = formatMonth((String) row[0]);

            long count = ((Number) row[1]).longValue();

            trends.put(month, new MonthlyTrendDto(month, count, 0));
        }

        // Add checkins data
        for (Object[] row : checkins) {
        	String month = formatMonth((String) row[0]);
            long count = ((Number) row[1]).longValue();

            trends.compute(month, (k, existing) ->
                    existing == null
                            ? new MonthlyTrendDto(month, 0, count)
                            : new MonthlyTrendDto(month, existing.getCheckouts(), count)
            );
        }

        return new ArrayList<>(trends.values());
    }
    
    private String formatMonth(String raw) {
        String[] parts = raw.split("-");
        int year = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);

        return LocalDate.of(year, month, 1)
                .getMonth()
                .name()
                .substring(0, 3)
                .toLowerCase()
                .replaceFirst(".", String.valueOf(Character.toUpperCase(
                        LocalDate.of(year, month, 1).getMonth().name().charAt(0)
                ))) + " " + year;
    }


}
