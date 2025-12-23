package co.zw.blexta.checkmate.device;

import co.zw.blexta.checkmate.common.dto.DeviceAssignmentDto;
import co.zw.blexta.checkmate.common.dto.DeviceCreateDto;
import co.zw.blexta.checkmate.common.dto.DeviceDto;
import co.zw.blexta.checkmate.common.dto.DeviceUpdateDto;
import co.zw.blexta.checkmate.common.response.ApiResponse;
import co.zw.blexta.checkmate.common.utils.SessionUtil;
import co.zw.blexta.checkmate.device.assignments.DeviceAssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/devices")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;
    private final DeviceAssignmentService deviceAssignmentService;
    private final SessionUtil sessionUtil;

    @PostMapping
    public ApiResponse<DeviceDto> createDevice(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody DeviceCreateDto dto) {

        Long creatorId = sessionUtil.extractUserId(authHeader);
        DeviceDto device = deviceService.registerDevice(dto, creatorId);

        return ApiResponse.<DeviceDto>builder()
                .success(true)
                .message("Device registered successfully")
                .data(device)
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<DeviceDto> getDevice(@RequestHeader("Authorization") String authHeader,
                                            @PathVariable Long id) {
        Long userId = sessionUtil.extractUserId(authHeader);
        DeviceDto device = deviceService.getDevice(id); 
        return ApiResponse.<DeviceDto>builder()
                .success(true)
                .message("Device retrieved successfully")
                .data(device)
                .build();
    }

    @GetMapping
    public ApiResponse<List<DeviceDto>> getAllDevices(@RequestHeader("Authorization") String authHeader) {
        Long userId = sessionUtil.extractUserId(authHeader);
        List<DeviceDto> devices = deviceService.getAllDevicesForUser(userId);
        return ApiResponse.<List<DeviceDto>>builder()
                .success(true)
                .message("All devices retrieved successfully")
                .data(devices)
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<DeviceDto> updateDevice(@RequestHeader("Authorization") String authHeader,
                                               @PathVariable Long id,
                                               @RequestBody DeviceUpdateDto dto) {
        Long userId = sessionUtil.extractUserId(authHeader);
        DeviceDto updated = deviceService.updateDevice(dto, id); // service should enforce role/company rules
        return ApiResponse.<DeviceDto>builder()
                .success(true)
                .message("Device updated successfully")
                .data(updated)
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteDevice(@RequestHeader("Authorization") String authHeader,
                                          @PathVariable Long id) {
        Long userId = sessionUtil.extractUserId(authHeader);
        deviceService.deleteDevice(id); // service should enforce role/company
        return ApiResponse.<Void>builder()
                .success(true)
                .message("Device deleted successfully")
                .build();
    }

    @PostMapping("/{id}/checkout")
    public ApiResponse<DeviceAssignmentDto> checkoutDevice(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id,
            @RequestBody Map<String, Long> body) {

        Long performedBy = sessionUtil.extractUserId(authHeader);
        Long assignedTo = body.get("assignedTo");

        DeviceAssignmentDto assignment = deviceAssignmentService.assignDevice(
                id,
                assignedTo,
                performedBy
        );

        return ApiResponse.<DeviceAssignmentDto>builder()
                .success(true)
                .message("Device checked out successfully")
                .data(assignment)
                .build();
    }

    @PostMapping("/{id}/checkin")
    public ApiResponse<DeviceAssignmentDto> checkInDevice(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id) {

        Long performedBy = sessionUtil.extractUserId(authHeader);

        DeviceAssignmentDto assignment = deviceAssignmentService.checkInDevice(
                id,
                performedBy
        );

        return ApiResponse.<DeviceAssignmentDto>builder()
                .success(true)
                .message("Device checked in successfully")
                .data(assignment)
                .build();
    }

    @GetMapping("/assetCode/{code}")
    public ApiResponse<DeviceDto> getDeviceByAssetCode(@RequestHeader("Authorization") String authHeader,
                                                       @PathVariable String code) {
        Long userId = sessionUtil.extractUserId(authHeader);
        DeviceDto device = deviceService.getDeviceByAssetCode(code); 
        return ApiResponse.<DeviceDto>builder()
                .success(true)
                .message("Device retrieved successfully")
                .data(device)
                .build();
    }

    @GetMapping("/recent")
    public ApiResponse<List<DeviceAssignmentDto>> getRecentAssignments(@RequestHeader("Authorization") String authHeader) {
        Long userId = sessionUtil.extractUserId(authHeader);
        List<DeviceAssignmentDto> recent = deviceAssignmentService.getRecentAssignments(5, userId);
        return ApiResponse.<List<DeviceAssignmentDto>>builder()
                .success(true)
                .message("Recent assignments retrieved successfully")
                .data(recent)
                .build();
    }

    @GetMapping("/{id}/history")
    public ApiResponse<List<DeviceAssignmentDto>> getDeviceHistory(@RequestHeader("Authorization") String authHeader,
                                                                    @PathVariable Long id) {
        Long userId = sessionUtil.extractUserId(authHeader);
        List<DeviceAssignmentDto> history = deviceAssignmentService.getDeviceHistory(id, userId);
        return ApiResponse.<List<DeviceAssignmentDto>>builder()
                .success(true)
                .message("Device assignment history retrieved successfully")
                .data(history)
                .build();
    }

    @GetMapping("/assigned")
    public ApiResponse<List<DeviceAssignmentDto>> getAssignedDevices(@RequestHeader("Authorization") String authHeader) {
        Long userId = sessionUtil.extractUserId(authHeader);
        List<DeviceAssignmentDto> assigned = deviceAssignmentService.getAssignedDevices(userId);
        return ApiResponse.<List<DeviceAssignmentDto>>builder()
                .success(true)
                .message("Currently assigned devices retrieved successfully")
                .data(assigned)
                .build();
    }

    @PostMapping("/{id}/request-return")
    public ApiResponse<DeviceAssignmentDto> requestReturn(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id) {

        Long userId = sessionUtil.extractUserId(authHeader);
        DeviceAssignmentDto dto = deviceAssignmentService.requestReturnDevice(id, userId);

        return ApiResponse.<DeviceAssignmentDto>builder()
                .success(true)
                .message("Return request sent successfully")
                .data(dto)
                .build();
    }
}
