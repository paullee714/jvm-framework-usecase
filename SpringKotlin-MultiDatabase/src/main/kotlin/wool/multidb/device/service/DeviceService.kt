package wool.multidb.device.service

import org.springframework.stereotype.Service
import wool.multidb.device.domain.DeviceEntity
import wool.multidb.device.repository.DeviceEntityRepository


@Service
class DeviceService(
    private val deviceEntityRepository: DeviceEntityRepository
) {
    fun getAllDevices(): MutableList<DeviceEntity> {
        return deviceEntityRepository.findAll()
    }
}
