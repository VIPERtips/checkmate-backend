package co.zw.blexta.checkmate.assset_code;

import co.zw.blexta.checkmate.common.dto.AssetCodeResponse;
import co.zw.blexta.checkmate.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/asset-codes")
@RequiredArgsConstructor
public class AssetCodeController {

    private final AssetCodeService assetCodeService;
    private final RedisTemplate<String, Object> redis;

    @PostMapping("/generate")
    public ApiResponse<AssetCodeResponse> generateAssetCode(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        Object sessionObj = redis.opsForValue().get("BLX:SESS:" + token);

        if (!(sessionObj instanceof Map<?, ?> session)) {
            return ApiResponse.<AssetCodeResponse>builder()
                    .success(false)
                    .message("Session expired or invalid")
                    .build();
        }

        Object userIdObj = session.get("userId");
        if (userIdObj == null) {
            return ApiResponse.<AssetCodeResponse>builder()
                    .success(false)
                    .message("User ID not found in session")
                    .build();
        }

        Long userId;
        try {
            userId = Long.parseLong(userIdObj.toString());
        } catch (NumberFormatException e) {
            return ApiResponse.<AssetCodeResponse>builder()
                    .success(false)
                    .message("Invalid user ID in session")
                    .build();
        }

        var assetCode = assetCodeService.createAssetCode(userId);

        return ApiResponse.<AssetCodeResponse>builder()
                .success(true)
                .message("AssetCode generated successfully")
                .data(new AssetCodeResponse(assetCode.getId(), assetCode.getCode()))
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<AssetCode> getAssetCode(@PathVariable Long id) {
        AssetCode assetCode = assetCodeService.getAssetCode(id);
        return ApiResponse.<AssetCode>builder()
                .success(true)
                .message("AssetCode retrieved successfully")
                .data(assetCode)
                .build();
    }

    @GetMapping
    public ApiResponse<List<AssetCode>> getAllAssetCodes() {
        List<AssetCode> assetCodes = assetCodeService.getAllAssetCodes();
        return ApiResponse.<List<AssetCode>>builder()
                .success(true)
                .message("All AssetCodes retrieved successfully")
                .data(assetCodes)
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<AssetCode> updateAssetCode(@PathVariable Long id, @RequestParam String newCode) {
        AssetCode updated = assetCodeService.updateAssetCode(id, newCode);
        return ApiResponse.<AssetCode>builder()
                .success(true)
                .message("AssetCode updated successfully")
                .data(updated)
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteAssetCode(@PathVariable Long id) {
        assetCodeService.deleteAssetCode(id);
        return ApiResponse.<Void>builder()
                .success(true)
                .message("AssetCode deleted successfully")
                .build();
    }
}
