package wool.multidb.device.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import wool.multidb.device.domain.DeviceEntity
import wool.multidb.device.service.DeviceService


@RestController
class DeviceController(
        val deviceService: DeviceService
) {
    @GetMapping("/devices")
    fun getAllDevices(): MutableList<DeviceEntity> {
        return deviceService.getAllDevices()
    }
}
