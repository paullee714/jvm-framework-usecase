package wool.multidb.config

import com.zaxxer.hikari.HikariDataSource
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
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
    entityManagerFactoryRef = "entityManagerFactory",
    basePackages = ["wool.multidb.users.repository"]
)
class MariaConfig {
    @Bean(name = ["dataSourceProperties"])
    @ConfigurationProperties("users-data.datasource")
    fun usersDataSourceProperties(): DataSourceProperties {
        return DataSourceProperties()
    }

    @Bean(name = ["dataSource"])
    @ConfigurationProperties("users-data.datasource.configuration")
    fun dataSource(@Qualifier("dataSourceProperties") usersDataSourceProperties: DataSourceProperties): DataSource {
        return usersDataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource::class.java)
            .build()
    }

    @Bean(name = ["entityManagerFactory"])
    fun entityManagerFactory(
        builder: EntityManagerFactoryBuilder, @Qualifier("dataSource") dataSource: DataSource
    ): LocalContainerEntityManagerFactoryBean {
        return builder
            .dataSource(dataSource)
            .packages("wool.multidb.users.domain")
            .persistenceUnit("users")
            .build()
    }

    @Bean(name = ["transactionManager"])
    fun transactionManager(
        @Qualifier("entityManagerFactory") usersEntityManagerFactory: EntityManagerFactory
    ): PlatformTransactionManager {
        return JpaTransactionManager(usersEntityManagerFactory)
    }
}
