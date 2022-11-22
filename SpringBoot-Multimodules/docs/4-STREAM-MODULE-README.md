# 스트림 모듈 세팅하기

- 스트림모듈은 아직까지는 JPA나 `module-core`의 엔티티를 사용 할 일이 없었다.
- 스트림 모듈을 세팅하면서 Kafka와 어떻게 SpringBoot가 커넥션을 맺고, Consumer와 Producer가 어떻게 세팅되는지 정리해보려고한다.
- Kafka는 로컬 카프카도 괜찮고, 서버에 띄워진 Kafka, 혹은 Docker, Confluent등등 Kafka를 지원하는 솔루션이면 모두 사용 할 수 있다.
- 본문은 Kafka가 모두 준비되어있다는 가정하에 작성하려고한다.

## module-stream 만들기

- 이전시간에 만들었었던 `module-stream`에서 이어서 작업하려고 한다.
- Kafka는 `module-stream`어플리케이션에서만 사용할 것 이기 때문에 `module-stream`에만 의존성을 추가하면 된다.
- SpringBoot Application 내에서 Consumer, Producer를 만들자

## module-stream 설정하기

### build.gradle.kts 설정

- Kafka는 `module-stream`어플리케이션에서만 사용할 것 이기 때문에 `module-stream`의 `build.gradle.kts`에 의존성을 추가 해 준다
    ```kotlin
    plugins{

    }

    dependencies{
        implementation("org.springframework.kafka:spring-kafka")
    }
    ```

### Springboot Application Class 작성

- Springboot에서 사용 할 것 이기 때문에 `@SpringBootApplication`를 적용할 클래스를 만들어 준다
- 마찬가지로 패키지이름을 `com.wool` 로 작성하고, 하위에 `ModuleStreamApplication.kt` 를 생성하여 아래의 소스코드를 적어준다
    ```kotlin
    package com.wool

    import org.springframework.boot.autoconfigure.SpringBootApplication
    import org.springframework.boot.runApplication

    @SpringBootApplication
    class ModuleStreamApplication

    fun main(args: Array<String>) {
        runApplication<ModuleStreamApplication>(*args)
    }
    ```

### application.yml 작성

- Springboot에서 사용 할 것 이기 때문에 `application.yml`을 작성해 준다
- resources 패키지 하위에 `application.yml`를 작성한 후 카프카 설정값들을 적어준다
- 우선은 Produce, Consume테스트만 진행 할 것이기 때문에 모듈작업과는 조금은 달라보일 수 있다
    ```yaml
    spring:
      jackson:
        serialization:
          fail-on-empty-beans: false

      kafka:
        properties:
          session:
            timeout:
              ms: 45000
          sasl:
            mechanism: PLAIN
            jaas:
              config: {  }
          security:
            protocol: SASL_SSL
          bootstrap:
            servers: { }
    ```

## Kafka 연결하기

- order와 관련된 데이터를 구독하는 Consumer Class를 만들어 보자
- application.yml에 설정값을 잘 넣었다면 Spring이 자동으로 카프카와 연동시켜준다

### OrderConsumer Class 작성

- `com.wool`패키지 하위에 `consumer`패키지를 만들어 `OrderConsumer` 클래스를 생성 해 준다
    ```kotlin
    package com.wool.consumer

    import org.springframework.kafka.annotation.KafkaListener
    import org.springframework.stereotype.Service


    @Service
    class OrderConsumer {

        @KafkaListener(topics = ["order"], groupId = "order-consumer")
        fun consume(message: String){
            println("###########################")
            println(message)
            println("###########################")
        }
    }
    ```
    - `@KafkaListener`를 사용하여 `order`라는 토픽을 구독하고, `order-consumer`라는 그룹아이디를 가진 Consumer를 만들었다
    - `@Service`어노테이션을 붙여 SpringBoot에서 Bean으로 등록하도록 한다

### OrderProducer Class 작성

- `com.wool`패키지 하위에 `producer`패키지를 만들어 `OrderProducer` 클래스를 생성 해 준다
    ```kotlin
    package com.wool.producer

    import com.fasterxml.jackson.databind.ObjectMapper
    import com.wool.controller.dto.OrderProduceDto
    import org.springframework.kafka.core.KafkaTemplate
    import org.springframework.stereotype.Service

    @Service
    class OrderProducer(
    private val kafkaTemplate: KafkaTemplate<String, String>
    ) {

        final val KAFKA_ORDER_TOPIC: String = "order"

        fun sendOrderMessage(message: OrderProduceDto){
            // OrderProduceDto를 json serialize
            val obm:ObjectMapper = ObjectMapper()
            val jsomMessage = obm.writeValueAsString(message)

            kafkaTemplate.send(KAFKA_ORDER_TOPIC, jsomMessage)
        }

    }
    ```
    - `@Service`어노테이션을 붙여 SpringBoot에서 Bean으로 등록하도록 한다
    - `KafkaTemplate`을 주입받아 `produce`메소드를 만들었다
    - `kafkaTemplate.send(KAFKA_ORDER_TOPIC, jsomMessage)`를 통해 `order`라는 토픽에 메시지를 보낼 수 있다

### ProducerController 작성

- Stream Application 이 실행 될 때 마다 주기적으로 message를 Produce 해 주면 좋지만, `주문을 받는` 일을 한 후 나온 데이터 이기 때문에 Producer 를 호출 해 주는
  Controller를 만들어주자
- Controller 를 하나 만들고, api가 호출 될 때 `OrderProducer`를 호출하도록 작성하자
- `com.wool.controller` 패키지에 dto와 controller를 만들어주자

#### OrderProduceDto 작성

```kotlin
package com.wool.controller.dto

data class OrderProduceDto(
    val orderStoreName: String,
    val orderStoreAddress: String,
    val orderItem: String,
    val orderPrice: String,
    val customerId: Int,
)
```

- produce 시에 "앱" 혹은 "웹" 에서 사용자가 이미 로그인 되어있고, 자신의 id 값을 보내 줄 수 있다는 가정 하에 만들었다

#### ProducerController 작성

```kotlin
package com.wool.controller

import com.wool.controller.dto.OrderProduceDto
import com.wool.producer.OrderProducer
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController


@RestController
class ProducerController(
    private val orderProducer: OrderProducer
) {

    @PostMapping("/order-produce")
    fun produceOrder(@RequestBody orderDto: OrderProduceDto) {
        orderProducer.sendOrderMessage(orderDto)
    }

}
```

## 마무리

- Springboot Application 을 실행하면, Consumer가 kafka를 바라보고, `order` 토픽을 구독한 채로 있다
- `http://localhost:8080/order-produce`에 `POST`요청을 보내면, `OrderProducer`가 `order`토픽에 메시지를 보내고, Consumer가 메시지를 받아서 출력한다
- multimodule과 크게 상관없는 내용들이었지만, 다음 포스트에서 멀티모듈의 이점을 살리기 위해 `api 주문요청 -> 주문 produce -> 주문 consume -> 주문 저장 -> api조회` 의
  흐름으로 가보자
- 굳이 안태워도 될 것 같은 흐름이지만, 멀티모듈을 사용한다.... 생각하고 만들어보려한다
