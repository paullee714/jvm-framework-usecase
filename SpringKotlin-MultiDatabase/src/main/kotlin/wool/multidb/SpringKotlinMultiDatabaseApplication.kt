package wool.multidb

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.core.env.Environment

@SpringBootApplication
class SpringKotlinMultiDatabaseApplication

fun main(args: Array<String>) {
    runApplication<SpringKotlinMultiDatabaseApplication>(*args)
}
