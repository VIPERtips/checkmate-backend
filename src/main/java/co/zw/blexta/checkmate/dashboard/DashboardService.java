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
        boolean isSuperAdmin = authUser.getRoles().stream()
                .anyMatch(r -> "SUPERADMIN".equalsIgnoreCase(r.getName()));

        Long companyId = null;
        if (!isSuperAdmin) {
            User user = userRepository.findByAuthUser(authUser)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            if (user.getCompany() == null) {
                throw new RuntimeException("User has no company assigned");
            }
            companyId = user.getCompany().getId();
        }

        long totalGadgets = isSuperAdmin
                ? deviceRepository.count()
                : deviceRepository.countByCompanyId(companyId);

        long activeUsers = isSuperAdmin
                ? userRepository.count()
                : userRepository.countByCompanyId(companyId);

        long assignedCount = isSuperAdmin
                ? deviceRepository.countByStatus("assigned")
                : deviceRepository.countByStatusAndCompanyId("assigned", companyId);

        double utilizationRate = totalGadgets > 0 ? (assignedCount * 100.0 / totalGadgets) : 0;

        long myGadgets = assignmentRepository.countByAssignedTo_AuthUser_Id(authUser.getId());

        return DashboardStatsDto.builder()
                .totalGadgets(totalGadgets)
                .activeUsers(activeUsers)
                .upcomingEvents(0L) // you can add company filtering if needed
                .utilizationRate(utilizationRate)
                .myGadgets(myGadgets)
                .build();
    }


    public List<MonthlyTrendDto> getGadgetTrends(AuthUser authUser) {
        boolean isSuperAdmin = authUser.getRoles().stream()
                .anyMatch(r -> "SUPERADMIN".equalsIgnoreCase(r.getName()));

        Long companyId = null;
        if (!isSuperAdmin) {
            User user = userRepository.findByAuthUser(authUser)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            companyId = user.getCompany().getId();
        }

        List<Object[]> checkouts = isSuperAdmin
                ? assignmentRepository.getMonthlyCheckouts()
                : assignmentRepository.getMonthlyCheckoutsByCompanyId(companyId);

        List<Object[]> checkins = isSuperAdmin
                ? assignmentRepository.getMonthlyCheckins()
                : assignmentRepository.getMonthlyCheckinsByCompanyId(companyId);

        Map<String, MonthlyTrendDto> trends = new LinkedHashMap<>();

        if (checkouts != null) {
            for (Object[] row : checkouts) {
                if (row == null || row.length < 2) continue;
                String month = formatMonthSafe(row[0].toString());
                long count = ((Number) row[1]).longValue();
                trends.put(month, new MonthlyTrendDto(month, count, 0));
            }
        }

        if (checkins != null) {
            for (Object[] row : checkins) {
                if (row == null || row.length < 2) continue;
                String month = formatMonthSafe(row[0].toString());
                long count = ((Number) row[1]).longValue();
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
