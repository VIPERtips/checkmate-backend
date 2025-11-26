package co.zw.blexta.checkmate.device;

import co.zw.blexta.checkmate.common.dto.DeviceAssignmentDto;
import co.zw.blexta.checkmate.common.dto.DeviceCreateDto;
import co.zw.blexta.checkmate.common.dto.DeviceDto;
import co.zw.blexta.checkmate.common.dto.DeviceUpdateDto;
import co.zw.blexta.checkmate.common.response.ApiResponse;
import co.zw.blexta.checkmate.common.utils.SessionUtil;
import co.zw.blexta.checkmate.device.assignments.DeviceAssignment;
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
    public ApiResponse<DeviceDto> createDevice(@RequestBody DeviceCreateDto dto) {
        DeviceDto device = deviceService.registerDevice(dto);
        return ApiResponse.<DeviceDto>builder()
                .success(true)
                .message("Device registered successfully")
                .data(device)
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<DeviceDto> getDevice(@PathVariable Long id) {
        DeviceDto device = deviceService.getDevice(id);
        return ApiResponse.<DeviceDto>builder()
                .success(true)
                .message("Device retrieved successfully")
                .data(device)
                .build();
    }

    @GetMapping
    public ApiResponse<List<DeviceDto>> getAllDevices() {
        List<DeviceDto> devices = deviceService.getAllDevices();
        return ApiResponse.<List<DeviceDto>>builder()
                .success(true)
                .message("All devices retrieved successfully")
                .data(devices)
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<DeviceDto> updateDevice(@PathVariable Long id, @RequestBody DeviceUpdateDto dto) {
        DeviceDto updated = deviceService.updateDevice(dto, id);
        return ApiResponse.<DeviceDto>builder()
                .success(true)
                .message("Device updated successfully")
                .data(updated)
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteDevice(@PathVariable Long id) {
        deviceService.deleteDevice(id);
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
    public ApiResponse<DeviceDto> getDeviceByAssetCode(@PathVariable String code) {
        DeviceDto device = deviceService.getDeviceByAssetCode(code);

        return ApiResponse.<DeviceDto>builder()
                .success(true)
                .message("Device retrieved successfully")
                .data(device)
                .build();
    }
    @GetMapping("/recent")
    public ApiResponse<List<DeviceAssignmentDto>> getRecentAssignments() {
        List<DeviceAssignmentDto> recent = deviceAssignmentService.getRecentAssignments(5);
        return ApiResponse.<List<DeviceAssignmentDto>>builder()
                .success(true)
                .message("Recent assignments retrieved successfully")
                .data(recent)
                .build();
    }

}
