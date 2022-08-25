package wool.multidb.iotlog.repository

import org.springframework.data.mongodb.repository.MongoRepository
import wool.multidb.iotlog.domain.Light

interface IoTRepository : MongoRepository<Light, String>{}
