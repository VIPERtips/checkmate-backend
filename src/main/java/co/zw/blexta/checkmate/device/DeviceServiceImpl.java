package co.zw.blexta.checkmate.device;

import co.zw.blexta.checkmate.assset_code.AssetCode;
import co.zw.blexta.checkmate.assset_code.AssetCodeRepository;
import co.zw.blexta.checkmate.common.dto.DeviceCreateDto;
import co.zw.blexta.checkmate.common.dto.DeviceDto;
import co.zw.blexta.checkmate.common.dto.DeviceUpdateDto;
import co.zw.blexta.checkmate.common.exception.BadRequestException;
import co.zw.blexta.checkmate.common.exception.ResourceNotFoundException;
import co.zw.blexta.checkmate.deviceCategories.Category;
import co.zw.blexta.checkmate.deviceCategories.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeviceServiceImpl  implements  DeviceService{
    private final DeviceRepository deviceRepository;
    private final AssetCodeRepository assetCodeRepository;
    private final CategoryRepository categoryRepository;


    @Override
    public DeviceDto registerDevice(DeviceCreateDto dto) {
        AssetCode assetCode = assetCodeRepository.findById(dto.assetCodeId())
                .orElseThrow(() -> new BadRequestException("Invalid asset code ID"));

        Category category = categoryRepository.findById(dto.categoryId())
                .orElseThrow(() -> new BadRequestException("Invalid category ID"));

        Device device = Device.builder()
                .name(dto.name())
                .serialNumber(dto.serialNumber())
                .status(dto.status())
                .assetCode(assetCode)
                .category(category)
                .build();

        Device saved = deviceRepository.save(device);
        return new DeviceDto(saved);
    }

    @Override
    public DeviceDto getDevice(Long id) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found with id: " + id));
        return new DeviceDto(device);
    }

    @Override
    public DeviceDto updateDevice(DeviceUpdateDto dto, Long id) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found with id: " + id));

        device.setName(dto.name());
        device.setSerialNumber(dto.serialNumber());
        device.setStatus(dto.status());

        if (dto.categoryId() != null) {
            Category category = categoryRepository.findById(dto.categoryId())
                    .orElseThrow(() -> new BadRequestException("Invalid category ID"));
            device.setCategory(category);
        }

        Device updated = deviceRepository.save(device);
        return new DeviceDto(updated);
    }

    @Override
    public List<DeviceDto> getAllDevices() {
        List<Device> devices = deviceRepository.findAll();
        return devices.stream().map(DeviceDto::new).collect(Collectors.toList());
    }

    @Override
    public void deleteDevice(Long id) {
        if (!deviceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Device not found with id: " + id);
        }
        deviceRepository.deleteById(id);
    }

    @Override
    public DeviceDto getDeviceByAssetCode(String code) {
        Device device = deviceRepository.findByAssetCode_Code(code)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found for asset code: " + code));

        return new DeviceDto(device);
    }

}
