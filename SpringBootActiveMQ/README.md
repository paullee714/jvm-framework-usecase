# ActiveMQ와 JMS를 사용한 SpringBoot 메시지

- 이벤트 기반 통신은, 여러 서비스 및 관련 도메인 모델에 변경 사항이나 메시지들을 전파할 때 중요하다.
- 변경사항이 발생했을 경우, 여러 서비스/모델 에서 변경 사항을 적용 할 방법이 필요
- 메시지큐를 사용하면 안정적인 통신과 기능적용이 가능하다
- 메시지큐에는 여러가지가 있지만, 이번에는 ActiveMQ를 사용한다

## Event 기반 통신 / 아키텍처

이벤트기반 통신은 여러 마이크로 서비스 및 관련 도메인 모델에 변경사항을 전파 할 때 중요하다

변경 사항이 발생했을 경우, 여러 서비스/모델 에서 변경 사항을 적용 할 방법이 필요하다

이벤트 기반 아키텍처를 달성하기 위한 방법은 여러가지가 있지만, 많은 경우에 메시징 패턴을 사용한다

RabbitMQ, ActiveMQ, Apache Kafka 등과 같은 도구들은 메시징 패턴에 사용되는 메시지 브로커이다

이 중, ActiveMQ를 사용해 메시지를 보내고 받아보려고 한다

## ActiveMQ 세팅하기

ActiveMQ는 메시지 브로커로써 메시지를 보내고 받을 수 있는 기능을 제공한다

ActiveMQ는 공식페이지에서 다운받아도 괜찮고, docker-compose 를 통해서 설치해도 된다

공식페이지에서 다운받으려면, 아래 페이지에서 사용하면 된다

- ActiveMQ 공식페이지 링크: http://activemq.apache.org/activemq-5151-release.html

나는 개발환경에 따로 설치하는 것 보다, docker를 선호해서 docker-compose를 사용하려고 한다

```yaml
version: '3'

services:
  activemq:
    image: rmohr/activemq
    container_name: activemq
    ports:
      - "8161:8161"
      - "1883:1883"
      - "5672:5672"
      - "61613:61613"
      - "61616:61616"
      - "61614:61614"
```

- ActiveMQ Web Console은 8161, ActiveMQ Broker는 61616 포트를 사용한다
- 기타 다른 1883, 5672, 61613, 61614 포트는 다른 프로토콜을 사용한다
- docker 로그를 확인하면 ActiveMQ의 포트를 모두 확인할 수 있다

## SpringBoot 프로젝트 만들기

- springboot 버전은 2.7.9-SNAPSHOT 버전을 사용한다
  ```groovy
    plugins {
    id 'java'
    id 'org.springframework.boot' version '2.7.9-SNAPSHOT'
    id 'io.spring.dependency-management' version '1.0.15.RELEASE'
    }
    ```

### SpringBoot Gradle 적용

- spring web과 activemq 를 넣는다
    ```groovy
    dependencies {
        implementation 'org.springframework.boot:spring-boot-starter-activemq'
        implementation 'org.springframework.boot:spring-boot-starter-web'
        testImplementation 'org.springframework.boot:spring-boot-starter-test'
    }
    ```

### SpringBoot Resource 설정

- resources/application.properties 파일 내부에 ActiveMQ의 설정을 넣는다
    ```properties
    spring.activemq.broker-url=tcp://localhost:61616
    spring.activemq.user=admin
    spring.activemq.password=admin
    ```

### SpringBoot Application 작성하기

#### Config

- Config 패키지를 만들고, 아래의 JmsCOnfig 클래스를 만든다
    ```java
    package com.example.springbootactivemq.config;


    import org.apache.activemq.command.ActiveMQQueue;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;

    import javax.jms.Queue;

    @Configuration
    public class JmsConfig {

        @Bean
        public Queue queue() {
            return new ActiveMQQueue("test-queue");
        }
    }
    ```

#### consumer

- consumer 패키지를 만들고, 아래와같이 컨슈머를 만든다
    ```java
    package com.example.springbootactivemq.consumer;

    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;
    import org.springframework.jms.annotation.EnableJms;
    import org.springframework.jms.annotation.JmsListener;
    import org.springframework.messaging.handler.annotation.SendTo;
    import org.springframework.stereotype.Component;

    @Component
    public class MessageConsumer {

        private final Logger logger = LoggerFactory.getLogger(MessageConsumer.class);

        @JmsListener(destination = "test-queue")
        public void receiveMessage(String message) {
            logger.info("Received message: {}", message);
        }

        @JmsListener(destination = "test-queue")
        @SendTo("greet-queue")
        public String receiveMessageAndReply(String message) {
            logger.info("Received message: {}", message);
            return "Hello " + message;
        }

        @JmsListener(destination = "greet-queue")
        public void receiveGreeting(String message) {
            logger.info("Received greeting: {}", message);
        }
    }
    ```
    - `@JmsListener` 를 통해서 메시지를 받는다. `test-queue` 라는 큐에 메시지가 들어오면, `receiveMessage` 메소드가 실행된다
    - `@SendTo` 를 통해서 메시지를 보낼 수 있다. `test-queue` 라는 큐에 메시지가 들어오면, `receiveMessageAndReply` 메소드가 실행된다
        - `@SendTo`에 지정한 `greet-queue` 큐에 메시지를 보낸다
    - `receiveGreeting` 메소드는 `greet-queue` 라는 큐에 메시지가 들어오면 실행된다

#### controller

- controller를 만들어 API를 통해 Message를 보내는 역할을 만든다
    ```java
    package com.example.springbootactivemq.controller;


    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.jms.core.JmsTemplate;
    import org.springframework.web.bind.annotation.GetMapping;
    import org.springframework.web.bind.annotation.PathVariable;
    import org.springframework.web.bind.annotation.RequestMapping;
    import org.springframework.web.bind.annotation.RestController;

    import javax.jms.Queue;

    @RestController
    @RequestMapping("/api")
    public class MessageController {

        @Autowired
        private Queue queue;

        @Autowired
        private JmsTemplate jmsTemplate;


        @GetMapping("message/{message}")
        public ResponseEntity<String> publishMessage(@PathVariable("message") final String message) {
            jmsTemplate.convertAndSend(queue, message);
            return new ResponseEntity(message, HttpStatus.OK);
        }
    }
    ```

## 실행

- `/api/message/hello` 를 호출하면, `test-queue` 에 메시지가 들어가고, `greet-queue` 에 메시지가 들어간다
- 이 모든 과정을 `ActiveMQ Web Console` 에서 볼 수 있다
