# SpringBoot JPA 사용한 데이터 다루기

- SpringBoot를 사용해서 데이터를 다루고자한다
- 간단한 Entity 를 구성 한 후, h2 DB로 테스트 하려고 한다
- 원래 개발규칙은 `요건파악` -> `interface작성` -> `testcode 반영` -> `개발로직 작성` 와 같은 형태로 가려했으나 이번 정리에서는 우선 `EntityListener`를 적용하고 사용 해
  보는 것, 연관관계 매핑, 트랜잭션 매니저, 커스텀쿼리 등을 우선적으로 생각하려고한다.

## JPA 사용하기 - Entity CRUD부터 EntityListner까지

- 가장먼저 기본적인 CRUD를 구성하기위해서 프로젝트를 세팅한다
- 프로젝트의 구조는 아래와 같다
  ```shell
  └── src
    ├── main
    │   ├── java
    │   │   └── com
    │   │       └── example
    │   │           └── springbootsimplejpa
    │   │               ├── SpringBootSimpleJpaApplication.java
    │   │               ├── controller
    │   │               │   └── OrderController.java
    │   │               ├── domain
    │   │               │   ├── Auditable.java
    │   │               │   ├── OrderModel.java
    │   │               │   └── TimestampEntityListener.java
    │   │               ├── repository
    │   │               │   └── OrderRepository.java
    │   │               └── service
    │   │                   └── OrderService.java
    │   └── resources
    │       └── application.yml
    └── test
        └── java
            └── com
                └── example
                    └── springbootsimplejpa
                        ├── SpringBootSimpleJpaApplicationTests.java
                        └── repository
                            └── OrderRepositoryTest.java

  ```

### Gradle 설정

```
plugins {
    id 'org.springframework.boot' version '2.7.0'
    id 'io.spring.dependency-management' version '1.0.11.RELEASE'
    id 'java'
}

group = 'com.example'
version = '0.0.1-SNAPSHOT'
sourceCompatibility = '11'

configurations {
    compileOnly {
        extendsFrom annotationProcessor
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.modelmapper:modelmapper:3.1.0'
    implementation("com.h2database:h2:2.1.212")
    implementation 'org.apache.commons:commons-lang3:3.12.0'
    compileOnly 'org.projectlombok:lombok'
    annotationProcessor 'org.projectlombok:lombok'
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
}

tasks.named('test') {
    useJUnitPlatform()
}
```

### application.yml / application.properties 설정

- 나는 개인적으로 hierarchy 가 보일 수 있는 `application.yml`을 선호한다
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

### Controller, Domain, Repository, Service 패키지

#### Domain

- Domain 패키지 내부에 `OrderModel`을 만들어 사용하고자 하는 모델을 선언해준다
    ```java
    package com.example.springbootsimplejpa.domain;

    import com.sun.istack.NotNull;
    import lombok.*;

    import javax.persistence.*;
    import java.time.LocalDateTime;

    @NoArgsConstructor
    @AllArgsConstructor
    @RequiredArgsConstructor
    @Data
    @Builder
    @Entity
    @Table(name = "orders")
    public class OrderModel implements Auditable {

        @Id
        @GeneratedValue
        private Long id;

        @NotNull
        private String orderCode;

        @NotNull
        private String userMail;

        @CreationTimestamp
        private LocalDateTime createdAt;

        @UpdateTimestamp
        private LocalDateTime updatedAt;
    }
    ```
    - `@CreateionTimestamp`어노테이션으로 데이터 생성시 자동으로 Timestamp를 찍어주도록 설정 해 주자
    - 마찬가지로, Update시에도 동일하게 Timestamp가 적용 될 수 있도록 `@UpdateTimestamp`어노테이션을 설정 해 주자

#### Repository

- 위에서 작성한 모델을 가지고 CRUD 할 수 있도록 `JPAReposiotry`를 상속받아 인터페이스를 생성해주자
    ```java
    package com.example.springbootsimplejpa.repository;

    import com.example.springbootsimplejpa.domain.OrderModel;
    import org.springframework.data.jpa.repository.JpaRepository;
    import org.springframework.stereotype.Repository;

    @Repository
    public interface OrderRepository extends JpaRepository<OrderModel, String> {
        OrderModel findByOrderCode(String orderCode);
    }
    ```
    - jpa repository를 상속받으면, 모두 구현하지 않더라도 JPA 에서 제공하는 CRUD기능을 사용 할 수 있다
    - `findByOrderCode`라는 이름의 메서드를 생성해서, 기본으로 생성되지 않은 custom 쿼리를 사용한다
        - 이 때, JPAReposiotry에서 설정 해 준 이름대로 사용한다면 Implementation 을 생략하고 기능을 넣을 수 있다

#### Service

- 생성한 JPA Repository관련 기능을 사용 할 Service단을 작성 해 주자
    ```java
    package com.example.springbootsimplejpa.service;

    import com.example.springbootsimplejpa.domain.OrderModel;
    import com.example.springbootsimplejpa.repository.OrderRepository;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Service;

    import java.util.List;
    import java.util.Optional;

    @Service
    public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

        public OrderModel saveOrder(OrderModel orderDto) {
            return orderRepository.save(orderDto);
        }

        public List<OrderModel> saveOrderList(List<OrderModel> orderDtoList) {
            return orderRepository.saveAll(orderDtoList);
        }

        public List<OrderModel> getAllOrder() {
            return orderRepository.findAll();
        }

        public Optional<OrderModel> getOrderById(int id) {
            return orderRepository.findById(String.valueOf(id));
        }

        public Optional<OrderModel> getOrderByCode(String orderCode) {
            return Optional.ofNullable(orderRepository.findByOrderCode(orderCode));
        }
    }

    ```

#### Controller

- User가 Request할 수 있는 통로인 API를 작성해주자
    ```java
    package com.example.springbootsimplejpa.controller;


    import com.example.springbootsimplejpa.domain.OrderModel;
    import com.example.springbootsimplejpa.service.OrderService;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.web.bind.annotation.*;

    import java.util.List;

    @RestController
    @RequestMapping("/order")
    public class OrderController {


        @Autowired
        private OrderService orderService;


        @GetMapping
        public List<OrderModel> findAllOrderController() {
            return orderService.getAllOrder();
        }

        @PostMapping
        public OrderModel saveOrderController(@RequestBody OrderModel order) {
            return orderService.saveOrder(order);
        }
    }
    ```
    - `Controller`는 이번 기능에서 크게 의미가 없지만, 그래도 연동이 되는지 확인 해 보기만 하자

#### TestCode

- 자세한 테스트코드라기보다, 테스트 폴더에서 진행되는 crud 테스트를 진행하려고한다
    ```java
    package com.example.springbootsimplejpa.repository;

    import com.example.springbootsimplejpa.domain.OrderModel;
    import org.apache.commons.lang3.RandomStringUtils;
    import org.junit.jupiter.api.Test;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.boot.test.context.SpringBootTest;
    import org.springframework.data.domain.Sort;

    import java.util.List;
    import java.util.Scanner;

    @SpringBootTest
    public class OrderRepositoryTest {

        @Autowired
        private OrderRepository orderRepository;

        @Test
        void crud() { // create / read/ update / delete
            for (int i = 0; i < 10; i++) {
                var order = new OrderModel();
                String orderCode = RandomStringUtils.randomAlphanumeric(15);
                String orderUserMail = RandomStringUtils.randomAlphanumeric(7) + "@mail.com";
                order.setOrderCode(orderCode);
                order.setUserMail(orderUserMail);
                orderRepository.save(order);
            }

            orderRepository.findAll().forEach(System.out::println);
            for (OrderModel item : orderRepository.findAll()) {
                System.out.println(item);
            }

            List<OrderModel> orders = orderRepository.findAll(Sort.by(Sort.Direction.DESC,"orderCode"));
            orders.forEach(System.out::println);

        }
    }
    ```
    - `@Autowired`를 사용해서 `OrderReposiotry`의존성을 테스트에 주입
    - 아래의 `orderReposiotry.save(order)`가 들어있는 코드 외에는 데이터를 모두 불러와서 출력 해 주는 기능만을 사용한다
    - Random String은 직접 생성하는 방법도 있지만 `Apache common lang3` 패키지를 사용한다

### EntityListener 사용하기

- `EntityListener`를 사용하면 데이터에 접근하기 전/후 에 대한 로직을 추가적으로 작성 해 줄 수 있다
- `EntityListener`를 직접 만들기 전에, `Annotation`으로 설정 할 수 있는 여러가지들을 먼저 설정 하고 나서, `EntityListener`로 대체 해 보려고 한다

#### Entity Annotation - @pre../ @post..

- 아래의 코드를 Domain패키지 내의 `OrderModel`에 넣어준다
    ```java
    class OrderModel {
        // ... 생략

        @Column(updatable = false)
        private LocalDateTime createdAt;

        private LocalDateTime updatedAt;

        @PrePersist
        public void prePersist() {
            System.out.println(">>> prePersist");
        }

        @PostPersist
        public void postPersist() {
            System.out.println(">>> postPersist");
            this.setCreatedAt(LocalDateTime.now());
            this.setUpdatedAt(LocalDateTime.now());
        }

        @PreUpdate
        public void preUpdate() {
            System.out.println(">>> preUpdate");
        }

        @PostUpdate
        public void postUpdate() {
            System.out.println(">>> postUpdate");
            this.setUpdatedAt(LocalDateTime.now());
        }

        @PreRemove
        public void preRemove() {
            System.out.println(">>> preRemove");
        }

        @PostRemove
        public void postRemove() {
            System.out.println(">>> postRemove");
        }

        @PostLoad
        public void postLoad() {
            System.out.println(">>> postLoad");
        }
    }
    ```
    - 미리 작성했었던, createdAt과 updatedAt 만 위와같이 수정 해준다
    - 여기에 붙어있던 어노테이션들은 `@PostPresist, @PreUpdate`로 나뉘어 들어간다

- 위에서 작성했던 `TestCode`를 실행하면, 우리가 `System.out.println`으로 작성했던 프린트문이 함께 출력된다
- 엔티티 내부에서 각각 따로 설정하여 특정 엔티티에서만 동작 할 수 있는 기능을 구현 할 수 있지만, 공통적으로 만들 수 있는 로직들을 계속 만들어야 하는 단점이 있다
- 이를 위해 `EntityListener`클래스를 만들어 공통적인 요소들을 지정 해 주자

### EntityListener Class 만들기

- 우선 우리가 공통적으로 적용시킬 기능을 체크해야한다. 가장 많이 사용되는 것으로 Datetime을 저장/수정 하는 것이 있다
- 데이터가 생성되었을 때, 업데이트 되었을 때에 자동적으로 시간관련기능을 업데이트 해 줄 수 있는 기능을 예제로 만들어보고자 한다

#### Auditable Interface 만들기

- Domain 패키지 내부에 `Auditable`인터페이스를 생성하자
    ```java
    package com.example.springbootsimplejpa.domain;

    import java.time.LocalDateTime;

    public interface Auditable {
        LocalDateTime getCreatedAt();

        LocalDateTime getUpdatedAt();

        void setCreatedAt(LocalDateTime createdAt);

        void setUpdatedAt(LocalDateTime updatedAt);
    }
    ```

#### EntityListener Custom하기

- 우리의 EntityListener 클래스를 따로 생성하자
- 생성하는 EntityListener 는 위의 `Auditable` 인터페이스에서 선언 한 요소들에 대한 구현을 한다
- 따로 패키지를 구성해도 되지만, 우선은 간단한 구성을 위해 같은 domain 패키지 내부에 `TimestampEntityListener.java`를 생성했다
    ```java
    package com.example.springbootsimplejpa.domain;


    import javax.persistence.PrePersist;
    import javax.persistence.PreUpdate;
    import java.time.LocalDateTime;

    public class TimestampEntityListener {
        @PrePersist
        public void prePersist(Object o) {
            System.out.println(">>> This is Work from Entity Listener prePersist");

            if (o instanceof Auditable) {
                ((Auditable) o).setCreatedAt(LocalDateTime.now());
                ((Auditable) o).setUpdatedAt(LocalDateTime.now());
            }
        }

        @PreUpdate
        public void preUpdate(Object o) {
            System.out.println(">>> This is Work from Entity Listener preUpdate");

            if (o instanceof Auditable) {
                ((Auditable) o).setUpdatedAt(LocalDateTime.now());
            }
        }

    }
    ```

#### 생성한 EntityListener 적용하기

- 생성한 `TimestampEntityListener`를 아래와 같이 미리 만들어두었던 `OrderModel`에 적용하자
    ```java
    @EntityListeners(value = {TimestampEntityListener.class})
    public class OrderModel implements Auditable {
        // ,,, 생략
    }
    ```

#### TestCode

- 만들어두었던 테스트코드를 실행시켜보면, `createdAt`, `updatedAt`이 모두 자동적으로 생성/갱신 되는 것을 볼 수 있다
- 추가적으로 다른 Model을 생성해도 된다
