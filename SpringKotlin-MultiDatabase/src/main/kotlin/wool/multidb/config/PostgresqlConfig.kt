package wool.multidb.config

import com.zaxxer.hikari.HikariDataSource
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement
import javax.persistence.EntityManagerFactory
import javax.sql.DataSource

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    entityManagerFactoryRef = "deviceEntityManagerFactory",
    basePackages = ["wool.multidb.device.repository"]
)
class PostgresqlConfig {

    @Primary
    @Bean(name = ["deviceDataSourceProperties"])
    @ConfigurationProperties("device-data.datasource")
    fun deviceDataSourceProperties(): DataSourceProperties {
        return DataSourceProperties()
    }


    @Primary
    @Bean(name = ["deviceDataSource"])
    @ConfigurationProperties("device-data.datasource.configuration")
    fun dataSource(@Qualifier("deviceDataSourceProperties") deviceDataSourceProperties: DataSourceProperties): DataSource {
        return deviceDataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource::class.java).build()
    }

    @Primary
    @Bean(name = ["deviceEntityManagerFactory"])
    fun entityManagerFactory(
        builder: EntityManagerFactoryBuilder,
        @Qualifier("deviceDataSource") deviceDataSource: DataSource
    ): LocalContainerEntityManagerFactoryBean {
        return builder
            .dataSource(deviceDataSource)
            .packages("wool.multidb.device.domain")
            .persistenceUnit("device")
            .build()
    }

    @Primary
    @Bean(name = ["deviceTransactionManager"])
    fun transactionManager(
        @Qualifier("deviceEntityManagerFactory") deviceEntityManagerFactory: EntityManagerFactory
    ): PlatformTransactionManager{
        return JpaTransactionManager(deviceEntityManagerFactory)
    }


}
