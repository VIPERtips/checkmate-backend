package co.zw.blexta.checkmate.dashboard;

import co.zw.blexta.checkmate.common.dto.DashboardStatsDto;
import co.zw.blexta.checkmate.device.DeviceRepository;
import co.zw.blexta.checkmate.device.assignments.DeviceAssignmentRepository;
import co.zw.blexta.checkmate.staff_users.UserRepository;
import co.zw.blexta.checkmate.staff_users.User;
import co.zw.blexta.checkmate.auth.users.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

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
}
