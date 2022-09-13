package baul.sns.springkotlinsns

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.runApplication

@SpringBootApplication(exclude = [DataSourceAutoConfiguration::class])
class SpringKotlinSnsApplication

fun main(args: Array<String>) {
    runApplication<SpringKotlinSnsApplication>(*args)
}
