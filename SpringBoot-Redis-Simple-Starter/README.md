# SpringBoot에서 Redis 캐시를 사용하기

## 목차

- [사용 할 Redis 간단 설명](#사용-할-redis-간단-설명)
- [프로젝트 구조](#프로젝트-구조)
- [준비 할 것](#준비-할-것)
    - [Database](#Database)
    - [SpringBoot](#SpringBoot)
- [SpringBoot와 Redis를 사용하여 프로젝트 작성](#SpringBoot와-Redis를-사용하여-프로젝트-작성)
    - [SpringBoot 어플리케이션 세팅](#SpringBoot-어플리케이션-세팅)
    - [application properties](#application-properties)
    - [SpringBoot Redis 환경설정 세팅](#SpringBoot-Redis-환경설정-세팅)
    - [domain 모델](#Domain-모델)
    - [repository](#Repository)
    - [custom exception](#Custom-exception)
    - [service](#Service)
    - [controller](#Controller)
- [실행해보기](#실행해보기)
  - [API 요청](#API-요청)

이번에는 SpringBoot와 데이터접근과 관련해서 작성 해 보려고한다
많은 경우에 Spring은 DB에 담긴 데이터를 가져오고 전달하는 역할을 하고있는데 이 때, Redis를 가지고 데이터에 빠르게 접근하는 방법을 정리 해 보려고 한다

Redis는 데이터베이스로도 사용되고, Message Broker로도 사용되지만 Cache Manager에 더 많이 사용된다
한번 만들어보면서 정리 해 보도록 하겠다

## 사용 할 Redis 간단 설명

- Redis 사용 형태
    - In Memory Database
        - 데이터베이스 작업을 수행하기 위해 사용, No SQL 데이터베이스 역할을 함
        - No Tables, No Sequences, No Joins의 개념이 있음
        - String, Hash, List, Set 등의 형태로 데이터 저장
    - Cache
        - Redis를 캐시로 사용하여 애플리케이션의 성능을 높인다
    - Message Broker(MQ)
        - Redis를 MQ로 사용하여 메시지를 전달한다

- Redis 캐시란
    - Redis Cache는 Redis에서 제공하는 캐시 관리 기능
    - 사용자가 응용프로그램의 더 나은 성능을 위해 적용/사용
    - 엑세스 되는 데이터를 저장하는 캐시
    - 한번의 데이터 요청은 하나의 네트워크 호출인데, 이를 최소화 할 수 있음

- Redis Cache 활성화를 위한 `@Annotation`
    - @EnableCaching
        - SpringBoot에게 캐싱기능이 필요하다고 전달
        - SpringBoot Starter class에 적용
    - @Cacheable
        - DB에서 애플리케이션으로 데이터를 가져오고 Cache에 저장하는데 사용
        - DB에서 데이터를 가져오는 메서드에 적용
    - @CachePut
        - DB의 데이터 업데이트가 있을 때 Redis Cache에 데이터를 업데이트
        - DB에서 PUT/PATCH와 같은 업데이트에서 사용
    - @CacheEvict
        - DB의 데이터 삭제가 있을 때 Redis Cache에 데이터를 삭제
        - DB에서 DELETE와 같은 삭제에서 사용

## 프로젝트 구조

아래는 이해를 돕기 위해서 사용 한 프로젝트 구조를 적어보았다

```shell
├── HELP.md
├── README.md
├── build
├── build.gradle
├── docker-compose-database.yml
├── example-docker-data
├── gradle
│   └── wrapper
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
├── gradlew
├── gradlew.bat
├── settings.gradle
└── src
    ├── main
    │   ├── java
    │   │   └── com
    │   │       └── example
    │   │           └── springbootredissimplestarter
    │   │               ├── SpringBootRedisSimpleStarterApplication.java
    │   │               ├── config
    │   │               │   └── RedisCacheConfig.java
    │   │               ├── controller
    │   │               │   └── OrderController.java
    │   │               ├── domain
    │   │               │   └── Order.java
    │   │               ├── exception
    │   │               │   ├── OrderNotFoundException.java
    │   │               │   └── OrderStatusException.java
    │   │               ├── repository
    │   │               │   └── OrderRepository.java
    │   │               └── service
    │   │                   ├── OrderService.java
    │   │                   └── OrderServiceImpl.java
    │   └── resources
    │       ├── application.properties
    │       ├── static
    │       └── templates
    └── test
        └── java
            └── com
                └── example
                    └── springbootredissimplestarter
                        └── SpringBootRedisSimpleStarterApplicationTests.java

```

## 준비 할 것

당연하지만 `Spring Boot`와 `Redis`를 준비해야 한다.
SpringBoot는 `Spring Data Redis`라는 디펜던시를 통해서 Redis와 연결을 지원한다. 이 디펜던시를 통해서 Redis와 소통 할 것이고, Redis 서버를 docker를 사용해서 띄워 사용
하려고 한다

### Database

데이터베이스 환경을 먼저 구축하려고 한다. 모두의 컴퓨터환경이 같지 않기때문에, 현재 글 작성일 기준으로 `docker-compose`로 `Database`를 구성하고자한다.

- docker-compose-database.yml
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
      MYSQL_DATABASE: rediswithspring
      MYSQL_USER: paul
      MYSQL_PASSWORD: qwerqwer123
      container_name: "docker-maria"
      volumes:
        - ./example-docker-data/maria:/var/lib/mysql

    redis-docker:
      image: redis:latest
      command: redis-server --port 6379
      container_name: "docker-redis"
      volumes:
        - ./example-docker-data/redis:/data
      labels:
        - "name=redis"
        - "mode=standalone"
      ports:
        - 6379:6379
  ```
    - mysql
        - mysql-docker는 docker환경으로 설치된 mysql(maria)서버이다
        - image로 `arm64v8/mariadb`를 사용했는데, 이는 m1에서 mysql을 구동하기 위함이다
        - 각 환경에 맞게 이미지를 변경시켜주면 된다
        - mariadb와 mysql의 docker세팅이 비슷하기때문에 mysql / mariadb 동일하게 사용해도 된다
        - mysql에서 사용할 환경변수파일은 `.mysql_env`로 작성했으며 내용은 아래와 같다(docker-compose에 명시하지않고 env파일로 빼고싶은 사람만 사용하면 될 것 같다)
          ```
          MYSQL_HOST=localhost
          MYSQL_PORT=3306
          MYSQL_ROOT_PASSWORD=qwerqwer123
          MYSQL_DATABASE=rediswithspring
          MYSQL_USER=paul
          MYSQL_PASSWORD=qwerqwer123
          ```
    - redis
        - redis-docker는 docker환경으로 설치된 redis 서버이다
        - command에 `--requirepass`로 비밀번호를 설정 해 주고, `--port`로 6379 포트를 열어주었다

### SpringBoot

SpringBoot Starter를 사용해 프로젝트를 시작하고, Dependency 세팅을 해 준다

- Dependency
    - Spring Web
    - Spring Data JPA
    - MYSQL Driver
    - Spring Data Redis
    - Lombok
    - Spring Boot Devtools

## SpringBoot와 Redis를 사용하여 프로젝트 작성

- 정교하고 자세한 모델은 아니지만, 배달음식 주문 어플리케이션을 만들어보려고 한다
- 자세한 기능보다는, CRUD와 해당하는 쿼리가 캐싱이 되었는지에 초점을 맞춰보려고 한다

### SpringBoot 어플리케이션 세팅
SpringBoot에게 Redis Cache를 사용 할 것이라고 알려주어야 한다. 위에 적어놓은 어노테이션 중 `@EnableCaching`을 스타터 클래스에 적용한다
```java
package com.example.springbootredissimplestarter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class SpringBootRedisSimpleStarterApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootRedisSimpleStarterApplication.class, args);
    }

}
```

### application properties
```properties
spring.datasource.driver-class-name=org.mariadb.jdbc.Driver
spring.datasource.url=jdbc:mysql://localhost:3306/rediswithspring
spring.datasource.username=paul
spring.datasource.password=qwerqwer123
spring.jpa.database-platform=org.hibernate.dialect.MariaDB103Dialect
spring.jpa.show-sql=true
spring.jpa.hibernate.ddl-auto=update
spring.cache.type=redis
spring.cache.redis.cache-null-values=true
```
- database가 mariadb로 올렸기때문에 jpa platform과 datasource driver-class 를 mariadb으로 해주었다
- mysql로 하고싶다면 드라이버 이름과 platform이름을 아래와 같이 적용하면 됨
  ```properties
  spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
  spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
  ```

### SpringBoot Redis 환경설정 세팅
- properties와 spring starter class에 캐시를 적용한다고 알려주었다
- Data를 가져오고 보낼 때, 우리가 만든 도메인 모델을 Serialize 해 주기 위해 설정이 필요하다
- config 패키지를 만들고 하위에 `RedisCacheConfig.java` 를 생성 해 주었다
- 여기서 생성한 `testCacheManger` 객체를 앞으로 사용 할 레디스 어노테이션에 명시 해 주어야 한다
```java
package com.example.springbootredissimplestarter.config;


import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
@EnableCaching
public class RedisCacheConfig {

    @Bean
    public CacheManager testCacheManager(RedisConnectionFactory cf) {
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()))
                .entryTtl(Duration.ofMinutes(3L));

        return RedisCacheManager.RedisCacheManagerBuilder.fromConnectionFactory(cf).cacheDefaults(redisCacheConfiguration).build();
    }
}

```

### Domain 모델
- 배달 어플리케이션을 위해 모델을 생성
- Order모델 생성
  - 이 때 주의점은, DDL에 order이 들어가기때문에 테이블 생성 시 `order 예약어` 로 인한 생성오류가 발생한다
  - 테이블 이름 명시(`@Table`)가 필요하다
- domain패키지 하위에 `Order.java`를 생성
```java
package com.example.springbootredissimplestarter.domain;

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
public class Order {

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

### Repository
- JpaRepository를 상속받아서 생성
- repository 패키지 하위에 `OrderRepository.java`를 생성
```java
package com.example.springbootredissimplestarter.repository;

import com.example.springbootredissimplestarter.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

}

```

### Custom exception
- 데이터 CRUD를 구현하기 전, Exception을 따로 주기 위해서 Custom Exception 생성
- exception 패키지 하위에 `OrderNotFoundException.java`와 `OrderStatusException.java`를 생성
```java
// OrderNotFoundException.java
package com.example.springbootredissimplestarter.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class OrderNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public OrderNotFoundException(String message) {
        super(message);
    }
}
```

```java
// OrderStatusException.java
package com.example.springbootredissimplestarter.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class OrderStatusException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public OrderStatusException(String message) {
        super(message);
    }
}
```

### Service
- service 패키지 하위에 `OrderService.java` 인터페이스와 `OrderServiceImpl.java`를 생성
- OrderService 인터페이스를 생성
- OrderServiceImpl 에서 OrderService 구현
```java
// OrderService.java - interface
package com.example.springbootredissimplestarter.service;

import com.example.springbootredissimplestarter.domain.Order;

import java.util.List;

public interface OrderService {

    public Order createOrder(Order order);

    public Order getOrder(Integer orderId);

    public Order updateOrder(Order order, Integer orderId);

    public void deleteOrder(Integer orderId);

    public List<Order> getAllOrders();
}
```
```java
// OrderServiceImpl.java - implementation
package com.example.springbootredissimplestarter.service;

import com.example.springbootredissimplestarter.domain.Order;
import com.example.springbootredissimplestarter.exception.OrderNotFoundException;
import com.example.springbootredissimplestarter.exception.OrderStatusException;
import com.example.springbootredissimplestarter.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public Order createOrder(Order order) {

        return orderRepository.save(order);

    }

    @Override
    @Cacheable(value = "Order", key = "#orderId", cacheManager = "testCacheManager")
    public Order getOrder(Integer orderId) {
        return orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException("Order Not Found"));
    }

    @Override
    @CachePut(value = "Order", key = "#orderId", cacheManager = "testCacheManager")
    public Order updateOrder(Order order, Integer orderId) {
        /*
        order status 변화주기
        status: ready -> processing -> shipped -> delivered
         */

        Order orderObject = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException("Order Not Found"));
        if (orderObject.getOrderStatus().equals("ready")) {
            orderObject.setOrderStatus("processing");
        } else if (orderObject.getOrderStatus().equals("processing")) {
            orderObject.setOrderStatus("shipped");
        } else if (orderObject.getOrderStatus().equals("shipped")) {
            orderObject.setOrderStatus("delivered");
        } else {
            throw new OrderStatusException("Order Status Cannot Change");
        }

        return orderRepository.save(orderObject);
    }

    @Override
    @CacheEvict(value = "Order", key = "#orderId", cacheManager = "testCacheManager")
    public void deleteOrder(Integer orderId) {

        Order orderObject = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException("Order Not Found"));
        orderRepository.delete(orderObject);

    }

    @Override
    @Cacheable(value = "Order", cacheManager = "testCacheManager")
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
}
```

### Controller
- RESTful API 서비스를 위한 Controller 구현
- controller 패키지를 생성, 하위에 `OrderController.java` 생성
```java
package com.example.springbootredissimplestarter.controller;


import com.example.springbootredissimplestarter.domain.Order;
import com.example.springbootredissimplestarter.service.OrderService;
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
    public Order createOrder(@RequestBody Order order) {
        return orderService.createOrder(order);
    }

    @GetMapping()
    public ResponseEntity<List<Order>> getOrder() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/{id}")
    public Order getOrder(@PathVariable Integer id) {
        return orderService.getOrder(id);
    }

    @PutMapping("/{id}")
    public Order updateOrder(@PathVariable Integer id, @RequestBody Order order) {
        return orderService.updateOrder(order, id);
    }

    @DeleteMapping("/{id}")
    public String deleteOrder(@PathVariable Integer id) {
        orderService.deleteOrder(id);

        return "Order with id: " + id + " deleted.";
    }
}
```

## 실행해보기
- 기본적으로 생성한 CRUD가 잘 실행되는지 확인한다
  - POST를 할 때에, 우리가 생성한 domain객체 혹은 각자 적용한 dto/vo객체의 형태대로 작성을 해 준다
- 생성한 객체를 조회 할 경우에 `application.properties`의 설정에 따라서 쿼리가 찍히게 된다
- 이 때, GET 요청을 많이 날려 보았을 때 쿼리가 Request 한 만큼 찍히지 않는 것이 보인다면 Redis Cache 기능을 사용하고 있는 것을 확인 할 수 있다
### API 요청
- POST /order
    ```http request
    curl -d '{"orderCode":"AGGEKF123","orderObject":"로제떡볶이","orderStatus":"ready","orderPrice":17000}' -H "Accept: application/json" -H "Content-Type: application/json" -X POST http://localhost:8080/order
    ```
- GET /order
    ```http request
    curl -X GET http://localhost:8080/order
    ```
- GET /order/1
  ```http request
  curl -X GET http://localhost:8080/order/1
  ```
