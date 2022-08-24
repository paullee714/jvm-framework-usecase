package wool.multidb

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SpringKotlinMultiDatabaseApplication

fun main(args: Array<String>) {
    runApplication<SpringKotlinMultiDatabaseApplication>(*args)
}
