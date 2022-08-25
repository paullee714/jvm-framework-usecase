package wool.multidb.device.repository

import org.springframework.data.jpa.repository.JpaRepository
import wool.multidb.device.domain.DeviceEntity

interface DeviceEntityRepository : JpaRepository<DeviceEntity, Int> {
}
