package co.zw.blexta.checkmate.common.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class DeviceAssignmentDto {
    private Long id;
    private Long deviceId;
    private String deviceName;
    private Long assignedToUserId;
    private String assignedToFullName;
    private Long assignedByUserId;
    private String assignedByFullName;
    private String status;
    private LocalDateTime assignedAt;
}
