# Kotlin과 Spring을 사용한 Server에 여러개의 DataSource를 사용하자
- 하나의 Server에서 하나의 DB만 사용하면 너무 편하겠지만 예외적인 상황들이 있을 수 있다
- 중앙에서 관리하는 Server(Route역할)가 앞에서 filter를 거쳐, 뒤에 이어진 다양한 Server들을 이어주는 경우가 아니라면 여러개의 DataSource를 설정하는 고민을 해 보았을 것 이다

## 만들어 볼 프로젝트
- Home IoT를 관리하는 서버를 만들자
- 도메인은 IoTLog, Device, Users
- 서로 매핑되면 좋은 프로젝트가 되겠지만, 엔티티의 매핑보다 DataSource를 다양하게 설정하는데에 주목하자

### 개발언어 및 사용스택
- Kotlin
- SpringBoot
- MariaDB
- PostgreSQL
- Mongodb

## 프로젝트 초기 설정
- spring-boot-starter-web
- spring-boot-starter-data-jpa
- spring-boot-starter-data-mongodb
- postgresql
- mariadb
- lombok

### build.gradle.kts
```kts
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.7.3"
    id("io.spring.dependency-management") version "1.0.13.RELEASE"
    kotlin("jvm") version "1.6.21"
    kotlin("plugin.spring") version "1.6.21"
    kotlin("plugin.jpa") version "1.6.21"
}

group = "wool"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    compileOnly("org.projectlombok:lombok")
    runtimeOnly("org.mariadb.jdbc:mariadb-java-client")
    runtimeOnly("org.postgresql:postgresql")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

```
- dependency를 설정한 gradle파일이다

## Configuration 조작하기 - Bean으로 직접 설정하기
- application.properties는 설정을 자동으로 해 주기 때문에 기본적인 설정을 입력하면 자동으로 세팅을 완료 해준다
- 우리가 만들려고 하는 서버에서는 기본적인 설정 외에 추가적인 설정을 필요로 하기때문에 Bean으로 직접 생성 해 주려고 한다

### application.properties
```application.properties
# Maria DB - "db1"
users-data.datasource.url=jdbc:mariadb://localhost:3306/paul?characterEncoding=UTF-8&serverTimezone=UTC
users-data.datasource.username=paul
users-data.datasource.password=qwerqwer123
users-data.datasource.driver-class-name=org.mariadb.jdbc.Driver
users-data.datasource.hikari.connection-test-query=SELECT 1


# PostgreSQL DB - "db2"
device-data.datasource.url=jdbc:postgresql://localhost:5432/paul
device-data.datasource.username=paul
device-data.datasource.password=qwerqwer123
device-data.datasource.driver-class-name=org.postgresql.Driver
device-data.datasource.hikari.connection-test-query=SELECT 1

# MongoDB
spring.data.mongodb.host=localhost
spring.data.mongodb.port=27017
spring.data.mongodb.username=paul
spring.data.mongodb.password=qwerqwer123
spring.data.mongodb.database=paul
spring.data.mongodb.authentication-database=admin

# The SQL dialect makes Hibernate generate better SQL for the chosen database
spring.jpa.properties.hibernate.dialect = org.hibernate.dialect.PostgreSQLDialect
spring.jpa.properties.hibernate.jdbc.lob.non_contextual_creation=true

# logging
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss} %-5level %logger{36} - %msg%n
logging.level.org.hibernate.SQL=debug

server.port=8000

```
- Maria DB, PostgreSQL DB, Mongo DB 를 따로 설정 해 주기 위해 따로 설정값들을 나열했다
- Postgresql은 사용자 데이터(Users), Maria DB은 Device 관리 테이블(Device), Mongo DB는 Device가 사용된 기록을 raw로 남긴다
- 사실 우리가 만들 코드에서는 MongoDB 설정값이 크게 필요 없다.
    - 이부분은 차후에 properties 파일에서 설정값을 읽어오는 방향으로 코드를 수정하면 된다

### Configuration Bean 생성하기 1 - Postgresql DB 세팅
- config 패키기를 생성하고, 하위에 `PostgresqlConfing` 파일을 생성한다
```kotlin
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

```

### Configuration Bean 생성하기 2 - Maria DB 세팅
- config 패키기를 생성하고, 하위에 `MariaConfig` 파일을 생성한다
```kotlin
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

```

### Configuration Bean 생성하기 3 - Mongo DB 세팅
- config 패키기를 생성하고, 하위에 `MongoConfig` 파일을 생성한다
```kotlin
package wool.multidb.config

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories


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

```
- 여기에서 사용한 ConnectionString에서는 user,password, admin DB 정보가 모두 들어가있다.
    - 차후에 고도화과정에서 해당 내용들을 properties로 빼거나, kube의 secret를 넣으면 된다


## Users 패키지 개발 - Controller, Service, Repository, Domain

### Domain - UsersEntity
```kotlin
package wool.multidb.users.domain

import lombok.Data
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table


@Entity
@Data
@Table(name = "users")
data class UsersEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null,
    val userName: String,
    val email: String,
)

```

### repository - UsersEntityRepository
```kotlin
package wool.multidb.users.repository

import org.springframework.data.jpa.repository.JpaRepository
import wool.multidb.users.domain.UsersEntity

interface UsersEntityRepository: JpaRepository<UsersEntity, Int> {
}

```

### service - UsersService
```kotlin
package wool.multidb.users.service

import org.springframework.stereotype.Service
import wool.multidb.users.domain.UsersEntity
import wool.multidb.users.repository.UsersEntityRepository

@Service
class UsersService(
        private val usersEntityRepository: UsersEntityRepository
) {
    fun getUsers(): List<UsersEntity> = usersEntityRepository.findAll()
}

```

### controller - UsersController
```kotlin
package wool.multidb.users.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import wool.multidb.users.domain.UsersEntity
import wool.multidb.users.service.UsersService


@RestController
class UsersController(
    private val usersService: UsersService
) {
    @GetMapping("/users")
    fun getAllUsers(): List<UsersEntity> {
        return usersService.getUsers()
    }
}

```

## Device 패키지 개발 - Controller, Service, Repository, Domain
### Domain - DeviceEntity
```kotlin
package wool.multidb.device.domain

import lombok.Data
import javax.persistence.*

@Entity
@Data
@Table(name = "device")
data class DeviceEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null,
    var deviceName: String? = null,
    var deviceType: String? = null
)

```

### repository - DeviceEntityRepository
```kotlin
package wool.multidb.device.repository

import org.springframework.data.jpa.repository.JpaRepository
import wool.multidb.device.domain.DeviceEntity

interface DeviceEntityRepository : JpaRepository<DeviceEntity, Int> {
}

```

### service - DeviceService
```kotlin
package wool.multidb.device.service

import org.springframework.stereotype.Service
import wool.multidb.device.domain.DeviceEntity
import wool.multidb.device.repository.DeviceEntityRepository


@Service
class DeviceService(
    private val deviceEntityRepository: DeviceEntityRepository
) {
    fun getAllDevices(): MutableList<DeviceEntity> {
        return deviceEntityRepository.findAll()
    }
}

```

### controller - DeviceController
```kotlin
package wool.multidb.device.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import wool.multidb.device.domain.DeviceEntity
import wool.multidb.device.service.DeviceService


@RestController
class DeviceController(
        val deviceService: DeviceService
) {
    @GetMapping("/devices")
    fun getAllDevices(): MutableList<DeviceEntity> {
        return deviceService.getAllDevices()
    }
}

```

## IoTLog 패키지 개발 - Controller, Service, Repository, Domain
- MongoDB 세팅이기떄문에 기존의 Repository의 JPARepository를 상속받기보다 MongoTemplate을 상속받을 것
- IoT Device 중, 전구(Light) 데이터를 저장 해 보려고 한다

### Domain - Light
```kotlin
package wool.multidb.iotlog.domain

import org.springframework.data.mongodb.core.mapping.Document
import javax.persistence.Entity
import javax.persistence.Id

@Document(collection = "iotLog")
data class Light(
    @Id
    var id: String? = null,
    var status: Boolean = false
)

```

### repository - IoTRepository
```kotlin
package wool.multidb.iotlog.repository

import org.springframework.data.mongodb.repository.MongoRepository
import wool.multidb.iotlog.domain.Light

interface IoTRepository : MongoRepository<Light, String>{}

```

### service - IoTService
```kotlin
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

```

### controller - IoTController
```kotlin
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

```

## 확인하기
- 스프링부트 서버를 실행시키고, 잘 동작하는지 확인한다
- application properties에 작성했었던 Configuration 값들을 config 패키지를 만들어 내부로 이동시켰다
- controller에서 정했던 url로 각각 호출 해 보면 잘 되는 것을 볼 수 있다
