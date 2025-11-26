package co.zw.blexta.checkmate.device;

import co.zw.blexta.checkmate.common.dto.DeviceCreateDto;
import co.zw.blexta.checkmate.common.dto.DeviceDto;
import co.zw.blexta.checkmate.common.dto.DeviceUpdateDto;
import co.zw.blexta.checkmate.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/devices")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;

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
}
