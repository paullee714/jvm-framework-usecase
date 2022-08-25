package wool.multidb.device.domain

import lombok.Data
import javax.persistence.*

@Entity
@Data
@Table(name = "device")
data class DeviceEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null,
    var deviceName: String? = null,
    var deviceType: String? = null
)
