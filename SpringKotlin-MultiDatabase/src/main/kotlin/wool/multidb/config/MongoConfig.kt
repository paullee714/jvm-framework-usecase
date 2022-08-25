package wool.multidb.config

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.MongoCredential
import com.mongodb.ServerAddress
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories
import java.util.Collections


@Configuration
@EnableMongoRepositories(basePackages = ["wool.multidb.iotlog.repository"])
class MongoConfig : AbstractMongoClientConfiguration() {

    override fun getDatabaseName(): String {
        return "iotlog"
    }

    override fun mongoClient(): MongoClient {
        val connectionString = ConnectionString("mongodb://paul:qwerqwer123@localhost:27017/paul?authSource=admin")

        val mongoClientSettings = MongoClientSettings
            .builder()
            .applyConnectionString(connectionString)
            .build()
        return MongoClients.create(mongoClientSettings)
    }

}
