package co.zw.blexta.checkmate.dashboard;

import co.zw.blexta.checkmate.common.dto.DashboardStatsDto;
import co.zw.blexta.checkmate.common.dto.MonthlyTrendDto;
import co.zw.blexta.checkmate.auth.users.AuthUser;
import co.zw.blexta.checkmate.auth.users.AuthUserService;
import co.zw.blexta.checkmate.common.response.ApiResponse;
import co.zw.blexta.checkmate.common.utils.SessionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    private final AuthUserService authUserService;
    private final SessionUtil sessionUtil;

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<DashboardStatsDto>> getStats(
            @RequestHeader("Authorization") String authHeader
    ) {
        Long userId = sessionUtil.extractUserId(authHeader);
        AuthUser user = authUserService.getUserById(userId);

        DashboardStatsDto stats = dashboardService.getStats(user);

        return ResponseEntity.ok(
                new ApiResponse<>("Stats fetched successfully", true, stats)
        );
    }

    @GetMapping("/gadget-trends")
    public ResponseEntity<ApiResponse<List<MonthlyTrendDto>>> getGadgetTrends(
            @RequestHeader("Authorization") String authHeader
    ) {
        Long userId = sessionUtil.extractUserId(authHeader);
        AuthUser user = authUserService.getUserById(userId);

        List<MonthlyTrendDto> trends = dashboardService.getGadgetTrends(user);

        return ResponseEntity.ok(
                new ApiResponse<>("Monthly trends fetched", true, trends)
        );
    }
}
