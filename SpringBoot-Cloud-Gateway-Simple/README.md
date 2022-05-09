# SpringBoot와 Spring Cloud Gateway 사용하기

Spring Cloud Gateway에 매력을 느껴서 정리를 해 보았다.
작성 할 양이 많아 조금 지치기는 했지만, 잘 알아두면 좋을 것 같다.

### 목차
- [개요](#개요)
- [API Gateway](#api-gateway)
    - [API Gateway가 필요한 이유](#api-gateway가-필요한-이유)
    - [API Gateway의 단점](#api-gateway의-단점)
- [Spring Cloud Gateway 생성하기](#spring-cloud-gateway-생성하기)
    - [데이터베이스 생성](#데이터베이스-생성)
    - [마이크로 서비스 생성 1. 유레카 서버 생성](#마이크로-서비스-생성-1-유레카-서버-생성)
        - [Dependency](#dependency)
        - [Eureka Annotation 설정](#eureka-annotation-설정)
        - [application properties 값 작성](#application-properties-값-작성)
    - [마이크로 서비스 생성 2. 주문서비스 서버 생성](#마이크로-서비스-생성-2-주문서비스-서버-생성)
        - [order service Dependency](#order-service-dependency)
        - [order service Eureka Annotation 설정](#order-service-eureka-annotation-설정)
        - [order service applciation properties 값 작성](#order-service-applciation-properties-값-작성)
        - [order service Domain](#order-service-domain)
        - [order service Repository](#order-service-repository)
        - [order service ServiceImpl, Service Interface](#order-service-serviceimpl-service-interface)
        - [order service Controller](#order-service-controller)
    - [마이크로 서비스 생성 3. 결제서비스 서버 생성](#마이크로-서비스-생성-3-결제서비스-서버-생성)
        - [Payment service Dependency](#payment-service-dependency)
        - [Payment service Eureka Annotation 설정](#payment-service-eureka-annotation-설정)
        - [Payment service applciation properties 값 작성](#payment-service-applciation-properties-값-작성)
        - [Payment service Controller](#payment-service-controller)
    - [마이크로 서비스 생성 4. API 게이트웨 서비스](#마이크로-서비스-생성-4-api-gateway-생성)
        - [API Gateway Dependency](#api-gateway-dependency)
        - [API Gateway Eureka Annotation 설정](#api-gateway-eureka-annotation-설정)
        - [API Gateway application properties 값 작성](#api-gateway-application-properties-값-작성)
        - [API Gateway Configuration](#api-gateway-configuration)



## 개요
- 마이크로서비스 아키텍처는 여러 서비스를 배포 할 수 있도록 하는 기술이다.
- 각각의 서버들에 `Request`를 보낼 때 인증을 거쳐야 하는데, 마이크로서비스가 늘어날수록 서비스의 수 만큼 인증을 받아야 하는 번거로움이 있다
- 다수의 인증을 줄여주고, 한번의 인증으로 여러서비스를 한번에 사용 할 수 있도록 도와주는 Spring Cloud Gateway를 사용 해 보도록 하자

## API Gateway
- API Gateway는 다수의 서버를 하나로 묶어, 외부 애플리케이션 혹은 클라이언트에 대한 하나의 진입점이라고 생각하면 된다
- 외부 어플리케이션 및 외부 클라이언트들은 마이크로 서비스에 직접 엑세스 하는 것이 제한되어 있기 때문에 그 사이의 중개자 역할을 한다

### API Gateway가 필요한 이유
- 마이크로서비스 기반 애플리케이션에서는 일반적으로 서비스 마다 하나의 서버에 배포된다
- 이 때, 서비스를 요창하는 주체는 각각의 서비스의 호스트 및 포트를 기억해야 하고, 보안 인증을 거쳐야 한다
- 위에서 설명했듯, 이 일련의 과정들을 API Gateway로 해결이 가능하다

### API Gateway의 단점
- 모든 요청이 API gateway를 거쳐지나가기 때문에 성능이 저하될 수 있음
- Request를 할 때 API Gateway에서 오류가나면 요청이 더이상 처리되지 않음

## Spring Cloud Gateway 생성하기
- 배달 서비스를 제공하는 어플리케이션의 일부분을 만들어보려고 한다.
- 자세하게 구현은 되지 않겠지만 `endpoint`가 나뉘고 따로 관리가 된다는 점을 생각하면서 작업 해 보도록 하려고 한다

### 데이터베이스 생성
- 마찬가지로 `docker-compose`를 사용해서 데이터베이스를 구축하려고한다
    ```yaml
    version: "3"
    services:
    mysql-docker:
        image: arm64v8/mariadb
        ports:
        - "3306:3306"
        environment:
        TZ: Asia/Seoul
        MYSQL_ROOT_PASSWORD: qwerqwer123
        MYSQL_DATABASE: deliveryapp
        MYSQL_USER: paul
        MYSQL_PASSWORD: qwerqwer123
        container_name: "docker-maria"
        volumes:
        - ./docker-databasea/maria:/var/lib/mysql
    ```
- `docker-compose -f docker-compose-database.yml up -d` 명령어로 데이터베이스를 실행시킨다

### 마이크로 서비스 생성 1. 유레카 서버 생성
- 마이크로 서비스를 서로 이어주고 통신하기 위해서는 `Eureka Server`를 생성해야 한다
- `Eureka Server`를 생성하고, 다른 서비스 어플리케이션에서는 `Eureka Client`를 세팅 해야 한다

#### Dependency
- SpringBoot
- Eureka Server

#### Eureka Annotation 설정
- Spring Boot starter 클래스에 `@EnableEurekaServer`를 설정 해 주어서 유레카 서버임을 알려준다
    ```java
    package com.example.springeurekasimple;

    import org.springframework.boot.SpringApplication;
    import org.springframework.boot.autoconfigure.SpringBootApplication;
    import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

    @SpringBootApplication
    @EnableEurekaServer
    public class SpringEurekaSimpleApplication {

        public static void main(String[] args) {
            SpringApplication.run(SpringEurekaSimpleApplication.class, args);
        }

    }
    ```

#### application properties 값 작성
```properties
server.port=8761
eureka.client.register-with-eureka=false
eureka.client.fetch-registry=false
```

### 마이크로 서비스 생성 2. 주문서비스 서버 생성
- 주문서비스를 제공하는 서버를 만든다
- Order는 데이터베이스 테이블명시를 꼭 해주어야한다 (database 에약어 때문)

#### order service Dependency
- SpringBoot
- Eureka Client
- mariadb
- jpa
- lombok

#### order service Eureka Annotation 설정
- Spring Boot starter 클래스에 `@EnableEurekaClient`를 설정 해 주어서 유레카 서버와 연결된 Client임을 명시 해준다
    ```java
    package com.example.springbootsimpleorderserver;

    import org.springframework.boot.SpringApplication;
    import org.springframework.boot.autoconfigure.SpringBootApplication;
    import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

    @SpringBootApplication
    @EnableEurekaClient
    public class SpringBootSimpleOrderServerApplication {

        public static void main(String[] args) {
            SpringApplication.run(SpringBootSimpleOrderServerApplication.class, args);
        }

    }
    ```

#### order service applciation properties 값 작성
```properties
server.port=9009
#Service Id
spring.application.name=ORDERING-SERVICE
#Publish Application(Eureka 등록)
eureka.client.service-url.default-zone=http://localhost:8761/eureka
#id for eureka server
eureka.instance.instance-id=${spring.application.name}:${random.value}

## Database
spring.datasource.driver-class-name=org.mariadb.jdbc.Driver
spring.datasource.url=jdbc:mysql://localhost:3306/deliveryapp
spring.datasource.username=paul
spring.datasource.password=qwerqwer123
spring.jpa.database-platform=org.hibernate.dialect.MariaDB103Dialect
spring.jpa.show-sql=true
spring.jpa.hibernate.ddl-auto=update
```

#### order service Domain
- domain 패키지 생성
- 테이블 이름 설정
    ```java
    package com.example.springbootsimpleorderserver.domain;

    import lombok.AllArgsConstructor;
    import lombok.Data;
    import lombok.NoArgsConstructor;

    import javax.persistence.Entity;
    import javax.persistence.GeneratedValue;
    import javax.persistence.Id;
    import javax.persistence.Table;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Entity
    @Table(name = "ORDERS")
    public class OrderDomain {

        private static final long serialVersionUID = 1L;

        @Id
        @GeneratedValue
        private Integer id;
        private String orderCode;
        private String orderObject;
        private String orderStatus;
        private Integer orderPrice;


    }
    ```

#### order service Repository
- JPARepository를 상속받는 OrderRepository 생성
- repository 패키지 생성 후 하위에 작성
    ```java
    package com.example.springbootsimpleorderserver.repository;

    import com.example.springbootsimpleorderserver.domain.OrderDomain;
    import org.springframework.data.jpa.repository.JpaRepository;
    import org.springframework.stereotype.Repository;

    @Repository
    public interface OrderRepository extends JpaRepository<OrderDomain, Integer> {

    }
    ```

#### order service ServiceImpl, Service Interface
- order의 service레이어 작성
- service 패키지 작성 후, `service`인터페이스, `serviceimpl` 클래스 생성
    ```java
    // service interface
    package com.example.springbootsimpleorderserver.service;

    import com.example.springbootsimpleorderserver.domain.OrderDomain;

    import java.util.List;

    public interface OrderService {

        public OrderDomain createOrder(OrderDomain order);

        public OrderDomain getOrder(Integer orderId);

        public OrderDomain updateOrder(OrderDomain order, Integer orderId) throws Exception;

        public void deleteOrder(Integer orderId) throws Exception;

        public List<OrderDomain> getAllOrders();

    }

    ```

    ```java
    // serviceImpl
    package com.example.springbootsimpleorderserver.service;

    import com.example.springbootsimpleorderserver.domain.OrderDomain;
    import com.example.springbootsimpleorderserver.repository.OrderRepository;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Service;

    import java.util.List;


    @Service
    public class OrderServiceImpl implements OrderService {

        @Autowired
        private OrderRepository orderRepository;

        @Override
        public OrderDomain createOrder(OrderDomain order) {

            return orderRepository.save(order);
        }

        @Override
        public OrderDomain getOrder(Integer orderId) {
            return orderRepository.findById(orderId).orElse(null);
        }

        @Override
        public OrderDomain updateOrder(OrderDomain order, Integer orderId) throws Exception {
            /*
            order status 변화주기
            status: ready -> processing -> shipped -> delivered
            */

            OrderDomain orderObject = orderRepository.findById(orderId).orElse(null);
            if (orderObject == null) {
                throw new Exception("Order not found");
            }

            switch (orderObject.getOrderStatus()) {
                case "ready":
                    orderObject.setOrderStatus("processing");
                    break;
                case "processing":
                    orderObject.setOrderStatus("shipped");
                    break;
                case "shipped":
                    orderObject.setOrderStatus("delivered");
                    break;
                default:
                    throw new Exception("order status is not ready");
            }

            return orderRepository.save(orderObject);
        }

        @Override
        public void deleteOrder(Integer orderId) throws Exception {
            OrderDomain orderObject = orderRepository.findById(orderId).orElse(null);
            if (orderObject != null) {
                orderRepository.delete(orderObject);
            } else {
                throw new Exception("Order not found");
            }
        }

        @Override
        public List<OrderDomain> getAllOrders() {
            return orderRepository.findAll();
        }
    }

    ```

#### order service Controller
- 컨트롤러 패키지 생성 후 하위에 API 엔트포인트 작성
    ```java
    package com.example.springbootsimpleorderserver.controller;


    import com.example.springbootsimpleorderserver.domain.OrderDomain;
    import com.example.springbootsimpleorderserver.service.OrderService;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;

    import java.util.List;

    @RestController
    @RequestMapping("/order")
    public class OrderController {

        @Autowired
        OrderService orderService;

        @PostMapping()
        public OrderDomain createOrder(@RequestBody OrderDomain order) {
            return orderService.createOrder(order);
        }

        @GetMapping()
        public ResponseEntity<List<OrderDomain>> getOrder() {

            return ResponseEntity.ok(orderService.getAllOrders());
        }

        @GetMapping("/{id}")
        public OrderDomain getOrder(@PathVariable Integer id) {
            return orderService.getOrder(id);
        }

        @PutMapping("/{id}")
        public OrderDomain updateOrder(@PathVariable Integer id, @RequestBody OrderDomain order) throws Exception {
            return orderService.updateOrder(order, id);
        }

        @DeleteMapping("/{id}")
        public String deleteOrder(@PathVariable Integer id) throws Exception {
            orderService.deleteOrder(id);

            return "Order with id: " + id + " deleted.";
        }
    }

    ```

### 마이크로 서비스 생성 3. 결제서비스 서버 생성
- 결제서비스를 제공하는 서버를 만든다
- 상세 로직말고 컨트롤러만 작성해서, Endpoint를 확인한다

#### Payment service Dependency
- SpringBoot web
- Eureka Client


#### Payment service Eureka Annotation 설정
- Spring Boot starter 클래스에 `@EnableEurekaClient`를 설정 해 주어서 유레카 서버와 연결된 Client임을 명시 해준다
    ```java
    package com.example.springbootsimplepaymentserver;

    import org.springframework.boot.SpringApplication;
    import org.springframework.boot.autoconfigure.SpringBootApplication;
    import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

    @SpringBootApplication
    @EnableEurekaClient
    public class SpringBootSimplePaymentServerApplication {

        public static void main(String[] args) {
            SpringApplication.run(SpringBootSimplePaymentServerApplication.class, args);
        }

    }

```

#### Payment service applciation properties 값 작성
```properties
server.port=9560
#Service Id
spring.application.name=PAYMENT-SERVICE
#Publish Application(Eureka ??)
eureka.client.service-url.default-zone=http://localhost:8761/eureka
#id for eureka server
eureka.instance.instance-id=${spring.application.name}:${random.value}

```
#### Payment service Controller
- 결제 상태를 확인 하는 간단한 컨트롤러만 작성
- 컨트롤러 패키지 생성 후 하위에 작성
    ```java
    package com.example.springbootsimplepaymentserver.controller;


    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.GetMapping;
    import org.springframework.web.bind.annotation.RequestMapping;
    import org.springframework.web.bind.annotation.RestController;

    @RestController
    @RequestMapping("/payment")
    public class PaymentController {

        @Value("${server.port}")
        private String port;

        @GetMapping("/status")
        public ResponseEntity<String> getStatus() {
            return ResponseEntity.ok("Payment server is running on port " + port);
        }

    }

    ```


### 마이크로 서비스 생성 4. API Gateway 생성
- API Gateway를 제공하는 서버를 만든다
- OrderService, PaymentService 서버를 각각 연결 해 준다

#### API Gateway Dependency
- Spring Reactive Web
- Eureka Discovery Client
- Gateway

#### API Gateway Eureka Annotation 설정
- Spring Boot starter 클래스에 `@EnableEurekaClient`를 설정 해 주어서 유레카 서버와 연결된 Client임을 명시 해준다
    ```java
    package com.example.springbootsimplegateway;

    import org.springframework.boot.SpringApplication;
    import org.springframework.boot.autoconfigure.SpringBootApplication;
    import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

    @SpringBootApplication
    @EnableEurekaClient
    public class SpringBootSimpleGatewayApplication {

        public static void main(String[] args) {
            SpringApplication.run(SpringBootSimpleGatewayApplication.class, args);
        }

    }

    ```

#### API Gateway application properties 값 작성
```properties
server.port=8080
spring.application.name=GATEWAY-SERVICE
eureka.client.service-url.defaultZone=http://localhost:8761/eureka
```

#### API Gateway Configuration
- API Gateway에 외부로부터 `Request`가 들어왔을 때 처리 해 줄 라우터 설정을 작성 해 준다
- config 패키지 하위에 작성
    ```java
    package com.example.springbootsimplegateway.config;


    import org.springframework.cloud.gateway.route.RouteLocator;
    import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;

    @Configuration
    public class SpringCloudGatewayRouting {

        @Bean
        public RouteLocator configurationRoute(RouteLocatorBuilder rlb) {

            return rlb.routes()
                    .route("paymentId", r -> r.path("/payment/**").uri("lb://PAYMENT-SERVICE"))
                    .route("orderId", r -> r.path("/order/**").uri("lb://ORDER-SERVICE"))
                    .build();
        }
    }

    ```
    - 위에서 작성한 PAYMENT-SERVICE, ORDER-SERVICE의 이름을 잘 기억 해주고, uri에 등록 해 주어 자동으로 매핑되고 라우팅 할 수 있도록 해 준다

## API Gateway 및 Euerka 서버 테스트
1. Euerka Server시작
2. OrderService, PaymentService 서버 시작
3. (option) 각각 OrderSerivce와 PaymentService를 포트만 달리해서 여러개 띄우기
4. API Gateway 서버 시작

아래와 같은 로그를 확인했다면, 유레카 서비스와 각각 MSA어플리케이션이 잘 매핑이 되었고 등록이 서로 잘 된 것이다
```
INFO 71481 --- [nio-8761-exec-2] c.n.e.registry.AbstractInstanceRegistry  : Registered instance ORDERING-SERVICE/ORDERING-SERVICE:a94db2fd0ad472b21714a9ebcb717736 with status UP (replication=false)
INFO 71481 --- [nio-8761-exec-3] c.n.e.registry.AbstractInstanceRegistry  : Registered instance ORDERING-SERVICE/ORDERING-SERVICE:a94db2fd0ad472b21714a9ebcb717736 with status UP (replication=true)
INFO 71481 --- [nio-8761-exec-4] c.n.e.registry.AbstractInstanceRegistry  : Registered instance GATEWAY-SERVICE/192.168.31.235:GATEWAY-SERVICE:8080 with status UP (replication=false)
INFO 71481 --- [nio-8761-exec-6] c.n.e.registry.AbstractInstanceRegistry  : Registered instance GATEWAY-SERVICE/192.168.31.235:GATEWAY-SERVICE:8080 with status UP (replication=true)
INFO 71481 --- [a-EvictionTimer] c.n.e.registry.AbstractInstanceRegistry  : Running the evict task with compensationTime 0ms
INFO 71481 --- [nio-8761-exec-7] c.n.e.registry.AbstractInstanceRegistry  : Registered instance PAYMENT-SERVICE/PAYMENT-SERVICE:e94906e028963a992b89eea164abe00d with status UP (replication=false)
INFO 71481 --- [nio-8761-exec-8] c.n.e.registry.AbstractInstanceRegistry  : Registered instance PAYMENT-SERVICE/PAYMENT-SERVICE:e94906e028963a992b89eea164abe00d with status UP (replication=true)
INFO 71481 --- [a-EvictionTimer] c.n.e.registry.AbstractInstanceRegistry  : Running the evict task with compensationTime 4ms
```

## 정리
- Euerka Server는 처음 Spring Euerka에서 설정한 8761 포트로 접속하면 확인 할 수 있다
- 포트를 각각 달리 했음에도 불구하고, API Gateway에서 설정한 port `8080` 으로 접속이 가능하다
- order서비스, payment서비스는 각각 `localhost:8080/order`와 `localhost:8080/payment`으로 접속이 가능하다.
- `order서비스` , `payment서비스`를 각각 여러대의 서버를 띄워 확인하면 API Gateway가 자동적으로 포트를 바꿔 연결 해 주는 것을 확인할 수 있다
