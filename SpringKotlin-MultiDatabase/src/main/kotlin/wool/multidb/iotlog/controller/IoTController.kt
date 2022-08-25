package wool.multidb.iotlog.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import wool.multidb.iotlog.domain.Light
import wool.multidb.iotlog.service.IoTService


@RestController
class IoTController(
    private val ioTService: IoTService
) {

    @GetMapping("/light")
    fun getAllLight(): List<Light> {
        return ioTService.getAllLight()
    }

    @PostMapping("/light")
    fun addLightLog(@RequestBody light: Light) {
        return ioTService.putLightLog(light)
    }
}
