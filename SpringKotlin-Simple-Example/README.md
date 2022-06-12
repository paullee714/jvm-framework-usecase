# SpringBoot Kotlin으로 작성하기

- Springboot 를 kotlin을 사용하여 작성해보기
- h2 데이터베이스와 JPA를 사용해서 주문모델을 구현 해 보도록 하자

## 개발환경

- Kotlin
- Springboot
- Gradle
- MacOS

## 프로젝트 구조

```shell
.
├── HELP.md
├── README.md
├── SpringKotlin-Simple-Example.iml
├── build
├── build.gradle.kts
├── gradle
│   └── wrapper
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
├── gradlew
├── gradlew.bat
├── settings.gradle.kts
└── src
    ├── main
    │   ├── kotlin
    │   │   └── com
    │   │       └── example
    │   │           └── springkotlinsimpleexample
    │   │               ├── SpringKotlinSimpleExampleApplication.kt
    │   │               ├── controller
    │   │               │   ├── OrderController.kt
    │   │               │   └── SampleController.kt
    │   │               ├── domain
    │   │               │   ├── OrderModel.kt
    │   │               │   └── dto
    │   │               │       ├── CreateOrderModelDTO.kt
    │   │               │       └── ReadOrderModelDTO.kt
    │   │               ├── repository
    │   │               │   └── OrderRepository.kt
    │   │               └── service
    │   │                   └── OrderService.kt
    │   └── resources
    │       ├── application.yml
    │       ├── static
    │       └── templates

```

## Dependency 설정 - gradle

```
implementation("org.springframework.boot:spring-boot-starter-data-jpa")
implementation("org.springframework.boot:spring-boot-starter-web")
implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
implementation("org.jetbrains.kotlin:kotlin-reflect")
implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
compileOnly("org.projectlombok:lombok")
runtimeOnly("com.h2database:h2")
annotationProcessor("org.projectlombok:lombok")
testImplementation("org.springframework.boot:spring-boot-starter-test")
```

## 프로젝트 내부 설정 생성 - application.yml

```yaml
spring:
  jpa:
    open-in-view: false
    generate-ddl: true
    show-sql: true
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        show_sql: true
        format_sql: true

  h2:
    console:
      enabled: true
```

## 프로젝트 코드 작성하기

1. 도메인 설계
2. DTO 설계
3. Repository 작성
4. Service 작성
5. Controller 작성

순서로 작업 해 보도록 한다

### Domain Model

- domain 패키지 내부에 OrderModel 코틀린 데이터클래스를 생성한다
    ```kotlin
    @Entity
    data class OrderModel(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Int? = null,
        val orderCode: String,
        val orderUserName: String,
        val orderUserPhone: String,
        val orderUserAddress: String,
        val orderUserEmail: String,
        val orderUserComment: String,
        val orderCreatedDate: OffsetDateTime = OffsetDateTime.now(),
    )
    ```
  - 작성한 Entity 를 사용 할 때, DTO로 감싸서 전달 하려고 한다.
  - DTO를 작성하여 OrderModel에 들어 갈 데이터 혹은 OrderModel에서 들어온 데이터를 원하는 부분만 추출 할 수 있도록 작성하자
  - dto 패키지를 작성하고, 하위에 `CreateOrderModelDTO`, `ReadOrderModelDTO` 를 작성 해 주자
      ```kotlin
      // CreateOrderModelDTO.kt
      data class CreateOrderModelDTO(
          val id: Int? = null,
          val orderCode: String,
          val orderUserName: String,
          val orderUserPhone: String,
          val orderUserAddress: String,
          val orderUserEmail: String,
          val orderUserComment: String,
      ) {
          fun toEntity(): OrderModel {
              return OrderModel(
                  orderCode = orderCode,
                  orderUserName = orderUserName,
                  orderUserPhone = orderUserPhone,
                  orderUserAddress = orderUserAddress,
                  orderUserEmail = orderUserEmail,
                  orderUserComment = orderUserComment
              )
          }
      }
      ```
      ```kotlin
      // ReadOrderModelDTO.kt
      data class ReadOrderModelDTO(
          val id: Int? = null,
          val orderCode: String,
          val orderUserName: String,
          val orderUserPhone: String,
          val orderUserAddress: String,
          val orderUserEmail: String,
          val orderUserComment: String,
          val orderCreatedDate: OffsetDateTime,
      )
      ```

- 작성 한 `CreateOrderModelDTO`와 `ReadOrderModelDTO`를 기반으로 OrderModel 데이터클래스를 완성하자
    ```kotlin
    @Entity
    data class OrderModel(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Int? = null,
        val orderCode: String,
        val orderUserName: String,
        val orderUserPhone: String,
        val orderUserAddress: String,
        val orderUserEmail: String,
        val orderUserComment: String,
        val orderCreatedDate: OffsetDateTime = OffsetDateTime.now(),
    ) {
        fun getReadOrderDTO(): ReadOrderModelDTO {
            return ReadOrderModelDTO(
                id = id,
                orderCode = orderCode,
                orderUserName = orderUserName,
                orderUserPhone = orderUserPhone,
                orderUserAddress = orderUserAddress,
                orderUserEmail = orderUserEmail,
                orderUserComment = orderUserComment,
                orderCreatedDate = orderCreatedDate
            )
        }

        fun createOrderModelDTO(): CreateOrderModelDTO {
            return CreateOrderModelDTO(
                orderCode = orderCode,
                orderUserName = orderUserName,
                orderUserPhone = orderUserPhone,
                orderUserAddress = orderUserAddress,
                orderUserEmail = orderUserEmail,
                orderUserComment = orderUserComment
            )
        }


        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
            other as OrderModel

            return id != null && id == other.id
        }

        override fun hashCode(): Int = javaClass.hashCode()

        @Override
        override fun toString(): String {
            return this::class.simpleName + "(id = $id , orderCode = $orderCode , orderUserName = $orderUserName , orderUserPhone = $orderUserPhone , orderUserAddress = $orderUserAddress , orderUserEmail = $orderUserEmail , orderUserComment = $orderUserComment , orderCreatedDate = $orderCreatedDate )"
        }

    }
    ```

### Repository 작성
- Repository는 JPARepository를 상속하여 작성한다
    ```kotlin
    interface OrderRepository : CrudRepository<OrderModel, String> {

        fun findAllByOrderUserName(orderCode: String): List<OrderModel>

        fun findByOrderCode(orderCode: String): OrderModel?
    }
    ```
    - 작성한 Repository는 `OrderRepository`를 상속하여 작성한다
    - `OrderRepository`는 `OrderModel`에 대한 조회, 생성, 수정, 삭제를 수행하는 기능을 제공한다
    - 추가적인 custom 쿼리 및 기능을 작성 하여 사용한다
    - interface 작업으로 내부 로직 구현을 유연하게 가져간다

### Service 작성
- 서비스로직을 작성한다. 아직 기초예제라서 데이터를 저장하는것에 그쳤다
    ```kotlin
    @Component
    class OrderService {

        @Autowired
        lateinit var orderRepository: OrderRepository

        fun getAllOrders(): List<ReadOrderModelDTO> {
            val orders = orderRepository.findAll()
            return orders.map { it.getReadOrderDTO() }
        }

        fun getOrderByOrderUserName(orderUserName: String): List<ReadOrderModelDTO> {
            val order = orderRepository.findAllByOrderUserName(orderUserName)
            return order.map { it.getReadOrderDTO() }
        }

        @Transactional
        fun createOrder(order: CreateOrderModelDTO): CreateOrderModelDTO {

            val requestOrderCode: String = order.orderCode
            val oldOrderCode: String = orderRepository.findByOrderCode(requestOrderCode)?.orderCode ?: ""

            orderRepository.findByOrderCode(requestOrderCode).let {
                if (it != null) {
                    throw IllegalArgumentException("Order Code is already exist")
                }
            }

            return orderRepository.save(order.toEntity()).createOrderModelDTO()
        }
    }
    ```
    - createOrder 함수에, `OrderCode`가 중복되면 저장 될 수 없도록 작성했다

### Controller 작성
- 작성한 Service를 사용해서 데이터를 저장/ 읽기 해 올 수 있도록 작성하자
    ```kotlin
    @RestController
    @RequestMapping("/api/v1/orders")
    class OrderController {

        @Autowired
        private lateinit var orderService: OrderService

        @GetMapping(produces = ["application/json"])
        fun getOrders(): ResponseEntity<Any> {
            return ResponseEntity.ok(orderService.getAllOrders())
        }

        @GetMapping(value = ["/{orderUserName}"], produces = ["application/json"])
        fun getOrderByUserName(orderUserName: String): ResponseEntity<Any> {
            return ResponseEntity.ok(orderService.getOrderByOrderUserName(orderUserName))
        }

        @PostMapping()
        fun createOrder(@RequestBody createOrderDTO: CreateOrderModelDTO): ResponseEntity<Any> {
            orderService.createOrder(createOrderDTO)
            return ResponseEntity.ok().body(true)
        }
    }
    ```
