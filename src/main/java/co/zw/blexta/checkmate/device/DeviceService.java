package co.zw.blexta.checkmate.device;

import co.zw.blexta.checkmate.common.dto.DeviceCreateDto;
import co.zw.blexta.checkmate.common.dto.DeviceDto;
import co.zw.blexta.checkmate.common.dto.DeviceUpdateDto;

import java.util.List;

public interface DeviceService {
    DeviceDto registerDevice(DeviceCreateDto dto,Long creatorId);
    DeviceDto getDevice(Long id);
    DeviceDto updateDevice(DeviceUpdateDto dto, Long id);
    List<DeviceDto> getAllDevices();
    void deleteDevice(Long id);
    DeviceDto getDeviceByAssetCode(String code);
    List<DeviceDto> getAllDevicesForUser(Long userId);

}
