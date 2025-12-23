package co.zw.blexta.checkmate.device;

import co.zw.blexta.checkmate.assset_code.AssetCode;
import co.zw.blexta.checkmate.assset_code.AssetCodeRepository;
import co.zw.blexta.checkmate.assset_code.AssetCodeService;
import co.zw.blexta.checkmate.common.dto.DeviceCreateDto;
import co.zw.blexta.checkmate.common.dto.DeviceDto;
import co.zw.blexta.checkmate.common.dto.DeviceUpdateDto;
import co.zw.blexta.checkmate.common.exception.BadRequestException;
import co.zw.blexta.checkmate.common.exception.ResourceNotFoundException;
import co.zw.blexta.checkmate.device.category.Category;
import co.zw.blexta.checkmate.device.category.CategoryRepository;
import co.zw.blexta.checkmate.staff_users.User;
import co.zw.blexta.checkmate.staff_users.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeviceServiceImpl implements DeviceService {

    private final DeviceRepository deviceRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final AssetCodeService assetCodeService;

    @Override
    @Transactional
    public DeviceDto registerDevice(DeviceCreateDto dto, Long creatorId) {
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new ResourceNotFoundException("Creator not found"));

        AssetCode assetCode = assetCodeService.createAssetCode(creatorId);

        Category category = categoryRepository.findById(dto.categoryId())
                .orElseThrow(() -> new BadRequestException("Invalid category ID"));

        if (creator.getCompany() == null && category.getScope() != co.zw.blexta.checkmate.device.category.CategoryScope.PLATFORM) {
            throw new BadRequestException("Superadmin can only assign PLATFORM category");
        }

        Device device = Device.builder()
                .name(dto.name())
                .serialNumber(dto.serialNumber())
                .status(dto.status())
                .assetCode(assetCode)
                .category(category)
                .company(creator.getCompany()) 
                .build();

        return new DeviceDto(deviceRepository.save(device));
    }

    @Override
    public DeviceDto getDevice(Long id) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found"));
        return new DeviceDto(device);
    }

    @Override
    public DeviceDto updateDevice(DeviceUpdateDto dto, Long id) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found"));

        device.setName(dto.name());
        device.setSerialNumber(dto.serialNumber());
        device.setStatus(dto.status());

        if (dto.categoryId() != null) {
            Category category = categoryRepository.findById(dto.categoryId())
                    .orElseThrow(() -> new BadRequestException("Invalid category ID"));
            device.setCategory(category);
        }

        return new DeviceDto(deviceRepository.save(device));
    }

    @Override
    public List<DeviceDto> getAllDevicesForUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        boolean isSuperAdmin = user.getAuthUser().getRoles()
                .stream().anyMatch(r -> r.getName().equals("SUPERADMIN"));

        List<Device> devices;

        if (isSuperAdmin) {
            devices = deviceRepository.findAll();
        } else {
            if (user.getCompany() == null) {
                throw new BadRequestException("User has no company assigned");
            }
            devices = deviceRepository.findAllByCompanyId(user.getCompany().getId());
        }

        return devices.stream()
                .map(DeviceDto::new)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteDevice(Long id) {
        if (!deviceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Device not found");
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
