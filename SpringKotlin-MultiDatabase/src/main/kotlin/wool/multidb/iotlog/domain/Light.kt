package wool.multidb.iotlog.domain

import org.springframework.data.mongodb.core.mapping.Document
import javax.persistence.Entity
import javax.persistence.Id

//import com.querydsl.core.annotations.QueryEntity

@Document(collection = "iotLog")
data class Light(
    @Id
    var id: String? = null,
    var status: Boolean = false
)
