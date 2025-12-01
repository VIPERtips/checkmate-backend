package co.zw.blexta.checkmate;

import co.zw.blexta.checkmate.common.response.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/health")
    public ApiResponse<String> health() {
        return ApiResponse.<String>builder()
                .success(true)
                .message("Service is running")
                .data("OK")
                .build();
    }

}
