package co.zw.blexta.checkmate.device.assignments;

import co.zw.blexta.checkmate.device.Device;
import co.zw.blexta.checkmate.staff_users.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeviceAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "assigned_to_id")
    private User assignedTo;

    @ManyToOne
    @JoinColumn(name = "assigned_by_id")
    private User assignedBy;

    @ManyToOne
    @JoinColumn(name = "device_id")
    private Device device;

    @CreationTimestamp
    private LocalDateTime assignmentDate;
}
