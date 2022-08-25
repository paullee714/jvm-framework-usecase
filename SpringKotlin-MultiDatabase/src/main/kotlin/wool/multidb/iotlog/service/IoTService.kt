package wool.multidb.iotlog.service

import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.stereotype.Service
import wool.multidb.iotlog.domain.Light
import wool.multidb.iotlog.repository.IoTRepository


@Service
class IoTService(
    val iotRepository: IoTRepository,
    val mongoTemplate: MongoTemplate
) {
    fun getAllLight(): List<Light> {
        return iotRepository.findAll()
    }

    fun putLightLog(light: Light) {
        mongoTemplate.save(light)
    }
}
