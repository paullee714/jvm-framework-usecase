# SpringBoot에서 Kafka Event Message 보내고 받기

빅 데이터의 세계에서 안정적인 스트리밍 플랫폼은 필수이다. 현재 가장 주목받고있는 kafka  스트리핑 플랫폼과 SpringBoot를 활용해서 간단한 Event 메시지를 주고받는 작업을 해보자

## Kafka 설치하기
당연한 얘기지만 Kafka를 사용하기 위해서는 Kakfa가 설치되어있는 서버를 사용하거나 직접 설치해서 사용해야 한다

나는 Docker를 사용해서 Kafka를 로컬 개발환경에 설치 한 후, 사용하려고 한다.
다른곳에 설치되어있는 Kafka를 사용한다면 오늘 사용하는 Kafka 주소만 변경 해 주면 될 것 같다

### 간단하게 개념정리
- Kafka Broker
  - 단일 Kafka 클러스터는 브로커로 구성
  - 생산자와 소비자를 처리하고 클러스터에 복제된 데이터를 유지하는 역할
- Kafka Topic
  - 레코드가 게시되는 범주
  - 카프카 메시지의 주제
- Kafka Producer
  - Kafka에 데이터를 가져오기 위해 작성하는 애플리케이션
  - 데이터 생산자
- Kafka Consumer
  - Kafka에서 데이터를 가져오기 위해 작성하는 프로그램
  - 데이터 소비자
- Zookeeper
  - Kafka 클러스터를 관리하고, 노드 상태를 추적하고, 주제 및 메시지 목록을 유지 관리하는 데 사용

### Dockerfile
zookeeper와 kafka를 설치하려고한다
따로 Docker파일을 작성하지 않고 `docker-compose.yml`로 작성하려고 한다
```
#docker-compose.yml - kafka/zookeper
version: '3'

services:
  zookeeper:
    image: arm64v8/zookeeper
    container_name: zookeeper
    ports:
      - "2181:2181"
  kafka:
    image: wurstmeister/kafka
    container_name: kafka
    ports:
      - "9092:9092"
    environment:
      KAFKA_ADVERTISED_HOST_NAME: localhost
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
```

`zookeeper` 에서는 2181 포트를 사용 할 것이고, `kafka`에서는 9092 포트를 사용하기 때문에  현재 포트가 사용중인지 한번 확인 해야 한다

`docker ps` 명령어를 통해서 해당 프로세스들이 잘 실행되고 있는지 확인한다

## Kafka 다루기 - 연결하기, topic 생성하기
### Kafka 연결하기
Zookeeper 및 Kafka 컨테이너가 실행되면, 카프카에 접속 해 준다
`docker exec -it kafka /bin/sh`

### Kafka Topic 만들기
Kafka 컨테이너 내에 카프카 스크립트들은 opt 폴더 내의 kafka\_<버전> 폴더 내의 bin 아래에 있다.
나의 경우, `opt/kafka_2.13-2.81/bin` 에서 작업을 했다 (설치 시기나 이미지에 따라서 카프카버전이 다를 것)`/opt/kafka_2.13-2.81/bin`경로로 들어가서 아래와 같은 명령어로 토픽을 생성한다
```
kafka-topics.sh --create --zookeeper zookeeper:2181 --replication-factor 1 --partitions 1 --topic wool_kafka_topic
```
아래의 명령어로 토픽 생성을 확인 할 수 있다
```
kafka-topics.sh --list --zookeeper zookeeper:2181
```

이제 Docker를 사용해서 간단한 kafka를 세팅은 완료했으니까, Spring과 이어질 작업을 해보자

간단한 토픽 관련된 명령어 및 python으로 핸들링 하는 방법은 미리 포스팅 해 본 적이 있다.

참조 포스팅 - [https://www.woolog.dev/data-engineering/kafka-python/1/]


## Spring Dependency 설치하기
SpringBoot를 gradle이나 maven으로 시작했는지 잘 기억하고, 각각 환경에 맞는 방법으로 설치 해 주면 될 것 같다.

혹은, Intellij에서 SpringBoot를 시작할때 Spring Starter에서 Web과 Kafka를 선택해서 설치 해 주어도 된다.

### Gradle -> build.gradle
```
// https://mvnrepository.com/artifact/org.springframework.kafka/spring-kafka
    implementation 'org.springframework.kafka:spring-kafka:2.8.5'
```
### maven -> pom.xml
```
<!-- https://mvnrepository.com/artifact/org.springframework.kafka/spring-kafka -->
<dependency>
    <groupId>org.springframework.kafka</groupId>
    <artifactId>spring-kafka</artifactId>
    <version>2.8.5</version>
</dependency>
```


## Spring Application 생성하기 - Producer, Consumer
SpringBoot Application 하나에 Producer, Consumer 모두 작성 할 수 있지만 서로 다른 스프링부트 어플리케이션에서 데이터를 주고받는 작업을 진행 해 보려고한다.

### Producer Application - config
스프링부트 어플리케이션을 생성하고, applicaion.yml을 만들어 서버 기본정보를 세팅하려고한다 (기존에 미리 세팅되어있는 application.properties는 삭제 해준다)
```yaml
spring:
  kafka:
    producer:
      bootstrap-servers: localhost:9092
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.apache.kafka.common.serialization.StringSerializer
```

### Producer Application - Controller
```java
package com.example.producerapplication.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
public class ProduceController {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @GetMapping("/publish")
    public String publish() {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();

        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        this.kafkaTemplate.send("wool_kafka_topic", generatedString);

        return "success";
    }
}
```

- `REST Controller`를 사용
- `KafkaTemplate`를 사용해서 카프카에 데이터를 전송 할 수 있도록 세팅한다
- publish 주소에서 SpringBoot -> Kafka 로 전송 할 수 있도록 세팅한다. 나의 경우에는 그냥 전송하기보다 임의의 Random String을 전송하도록 StringBuilder로 작성했다


### Consumer Application - config
컨슈머 어플리케이션의 정보는 기본 카프카의 정보와 더불어 서버 포트를 달리 해 주는 설정까지 추가했다
```yaml
spring:
  kafka:
    consumer:
      bootstrap-servers: localhost:9092
      group-id: my_group
      auto-offset-reset: earliest
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.apache.kafka.common.serialization.StringDeserializer
server:
  port: 8081
```

- auto-offset-reset: 가장 이른 것은 소비자가 가장 이른 이벤트부터 읽는다는 것을 의미
- key-deserializer 및 value-deserializer는 메시지를 보내기 위해 Kafka 생산자가 보낸 키와 값을 역직렬화하는 역할

### Consumer Application - Service
```java
package com.example.consumerapplication.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ConsumerService {

    @KafkaListener(topics = "wool_kafka_topic")
    public void receive(String message) {

        System.out.println(message);
    }

}
```

- Controller를 작성하지 않았다. Consumer Application에서 데이터를 전송하는 기능을 만들 수 있지만 현재 예제에서는 데이터를 받는것에 집중하려고한다
- Service패키지에 ConsumerSerivce를 만들고 어노테이션을 적용해서 카프카 리스너를 세팅하려고 한다.
- 여기서 사용되는 정보들은 `application.yml`에 있는 정보들을 스프링부트와 kafka 라이브러리가 파싱해서 가져간다

## 실행
- Producer 역할을 하는 SpringBoot Application을 시작한다 (localhost:8080)
- 마찬가지로 Consumer SpringBoot Application을 시작한다 (localhost:8081)
- Producer에서 `/publish`의 url 주소를 호출 해 준다
	- 이 때 , 우리가 Random으로 생성한 문자열을 kafka로 보내게 된다
- Consumer Application의 콘솔에서 kafka에서 보낸 랜덤스트링이 있는지 확인한다
