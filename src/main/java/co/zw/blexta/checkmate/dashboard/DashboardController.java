package co.zw.blexta.checkmate.dashboard;

import co.zw.blexta.checkmate.common.dto.DashboardStatsDto;
import co.zw.blexta.checkmate.auth.users.AuthUser;
import co.zw.blexta.checkmate.auth.users.AuthUserService;
import co.zw.blexta.checkmate.common.response.ApiResponse;
import co.zw.blexta.checkmate.common.utils.SessionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    private final AuthUserService authUserService;
    private final SessionUtil sessionUtil;

    @GetMapping("/api/dashboard/stats")
    public ResponseEntity<ApiResponse<DashboardStatsDto>> getStats(
            @RequestHeader("Authorization") String authHeader
    ) {
        Long userId = sessionUtil.extractUserId(authHeader);
        AuthUser user = authUserService.getUserById(userId);
        return ResponseEntity.ok().body(
                new ApiResponse<>(
                        "Stats fetched successfully",
                        true,
                        dashboardService.getStats(user)
                )
        );
    }
}
