# SpringBoot 에서 JPA를 사용한 Repository 테스트

- SpringBoot2
- Spring Data JPA
- H2
- Gradle
- JUnit

## 환경 설정하기
- SpringBoot를 사용해서 h2데이터베이스와 관련된 테스트를 진행 해 보려고 한다
- 스프링부트에 `application.yml`에 h2 세팅만 해주어도 바로 데이터베이스가 연결되어서 테스트용도로 편하다

### SpringBoot Starter
- SpringBoot Starter로 실행해도 되고, Maven Repository 페이지에서 관련 Dependency를 설치해도 된다
  - Gradle 프로젝트로 실행
  - SpringBoot JPA
  - H2 Database
  - JUnit
  - lombok
  - commons-lang3

### SpringBoot application.yml
- 원래는 application.properties 이지만, 나는 hierarchy 구조를 가진 `yml`이 더 맘에 들어 바꿔주었다
- application.yml
  ```yml
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

## 구조잡기
- 여러가지 테스트를 할 것이 아니라, Repository와 관련된 패키지와 클래스만 작업 할 것이기 떄문에 domain, repository를 만든다

### domain package
- domain package에 데이베이스에 넣어줄 모델을 구현한다
- OrderModel.java
  ```java
    package com.example.springbootjpatest.domain;


    import lombok.*;
    import org.hibernate.Hibernate;

    import javax.persistence.*;
    import java.util.Objects;


    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Entity
    @Data
    @Table(name = "orders")
    public class OrderModel {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(name = "order_code", nullable = false)
        private String orderCode;

        @Column(name = "order_username", nullable = false)
        private String orderUsername;

        @Column(name = "shop_name", nullable = false)
        private String shopName;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
            OrderModel that = (OrderModel) o;
            return id != null && Objects.equals(id, that.id);
        }

        @Override
        public int hashCode() {
            return getClass().hashCode();
        }
    }
  ```

### repository package
- domain객체를 사용해서 데이터베이스에 데이터를 서빙 할 repository 객체를 생성한다
- 이 떄, JPA를 사용 해서 Repository를 구현 할 것이기 때문에 추가적인 설정 말고, JPA를 상속한다
- OrderRepository.java
  ```java
    package com.example.springbootjpatest.repository;

    import com.example.springbootjpatest.domain.OrderModel;
    import org.springframework.data.jpa.repository.JpaRepository;

    public interface OrderRepository extends JpaRepository<OrderModel, Long> {

    }
  ```

## 테스트 코드 작성하기
- Repository를 테스트하기 위해서 Test에 repository 패키지를 추가하고 아래에 테스트클래스를 작성한다
- `@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)` 어노테이션을 사용해서, 테스트 순서를 지정 할 수 있도록 한다
  ```java
    package com.example.springbootjpatest.repository;


    import com.example.springbootjpatest.domain.OrderModel;
    import org.apache.commons.lang3.RandomStringUtils;
    import org.aspectj.weaver.ast.Or;
    import org.junit.jupiter.api.*;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;


    import java.util.ArrayList;
    import java.util.List;
    import java.util.Optional;

    import static org.assertj.core.api.Assertions.assertThat;

    @DataJpaTest
    @TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
    public class OrderRepositoryTests {
        // test 요소
    }
  ```

### Repository 테스트 - Autowired
- 테스트클래스에서 우리가 만든 repository 객체를 사용하기 위해서 @Autowired를 사용한다
- 상단 / 하단의 어노테이션과 패키지가 많으므로 생략해서 작성했다
```java
// 어노테이션 생략
public class OrderRepositoryTests {
    @Autowired
    private OrderRepository orderRepository;

    private OrderModel orderModel;
}
```

### Repository 테스트 - 초기화
```java
public class OrderRepositoryTests {
    // 생략

    @BeforeEach
    public void setUp() {
        String randomCode = RandomStringUtils.randomAlphanumeric(15);
        orderModel = OrderModel
                .builder()
                .orderCode(randomCode)
                .orderUsername("test_user")
                .shopName("test_shop")
                .build();
    }
}
```

### Repository 테스트 - 저장
```java
public class OrderRepositoryTests {
    // 생략

    @DisplayName("주문 저장 테스트")
    @Test
    @Order(1)
    public void testSave() {
        System.out.println(">>> SetUp OrderModel: " + orderModel.toString());

        // given
        String randomCode = RandomStringUtils.randomAlphanumeric(15);
        orderModel = OrderModel
                .builder()
                .orderCode(randomCode)
                .orderUsername("test_user")
                .shopName("test_shop")
                .build();

        System.out.println(">>> Origin OrderModel: " + orderModel.toString());


        // when
        OrderModel savedOrderModel = orderRepository.save(orderModel);
        System.out.println(">>> Saved OrderModel: " + savedOrderModel);

        // then
        assertThat(savedOrderModel.getId()).isNotNull();
        assertThat(savedOrderModel.getOrderCode()).isEqualTo(randomCode);
        assertThat(savedOrderModel.getOrderUsername()).isEqualTo("test_user");
        assertThat(savedOrderModel.getShopName()).isEqualTo("test_shop");
        assertThat(savedOrderModel.getId()).isGreaterThan(0);

    }
}
```

### Repository 테스트 - 다수 건 저장
```java
public class OrderRepositoryTests {
    // 생략
    @DisplayName("주문 벌크 저장 테스트")
    @Test
    @Order(2)
    public void testBulkSave() {
        // given
        ArrayList<OrderModel> orderModelList = new ArrayList<OrderModel>();
        for (int i = 0; i < 100; i++) {
            String randomCode = RandomStringUtils.randomAlphanumeric(15);
            orderModel = OrderModel
                    .builder()
                    .orderCode(randomCode)
                    .orderUsername("test_user")
                    .shopName("test_shop")
                    .build();

            System.out.println(">>> Create OrderModel: " + orderModel.toString());
            orderModelList.add(orderModel);
        }

        orderRepository.saveAll(orderModelList);

        //when
        List<OrderModel> savedOrderModels = orderRepository.findAll();
        for (OrderModel orderModel : savedOrderModels) {
            System.out.println(">>> Saved #" + orderModel.getId() + " OrderModel: " + orderModel.toString());
        }

        //then
        for (OrderModel orderModel : savedOrderModels) {
            assertThat(orderModel.getId()).isNotNull();
            assertThat(orderModel.getOrderCode()).isNotNull();
            assertThat(orderModel.getOrderUsername()).isNotNull();
            assertThat(orderModel.getShopName()).isNotNull();
            assertThat(orderModel.getId()).isGreaterThan(0);
        }

    }
}

```
### Repository 테스트 - 전체 조회
```java
@DisplayName("주문정보 전체 불러오기")
@Test
@Order(3)
public void testReadAll() {
    //given
    ArrayList<OrderModel> orderModelList = new ArrayList<OrderModel>();
    for (int i = 0; i < 100; i++) {
        String randomCode = RandomStringUtils.randomAlphanumeric(15);
        orderModel = OrderModel
                .builder()
                .orderCode(randomCode)
                .orderUsername("test_user")
                .shopName("test_shop")
                .build();

        System.out.println(">>> Create OrderModel: " + orderModel.toString());
        orderModelList.add(orderModel);
    }

    orderRepository.saveAll(orderModelList);

    //when
    List<OrderModel> orderModels = orderRepository.findAll();
    System.out.println("####### " + orderModels);
    //then
    assertThat(orderModels.size()).isGreaterThan(0);
    assertThat(orderModels).isNotNull();
}
```


### Repository 테스트 - 조건 조회
```java
@DisplayName("주문정보 Order Id로 불러오기")
@Test
@Order(4)
public void testReadById() {

    //given
    int min = 0;
    int max = 0;


    ArrayList<OrderModel> orderModelList = new ArrayList<OrderModel>();
    for (int i = 0; i < 100; i++) {
        String randomCode = RandomStringUtils.randomAlphanumeric(15);
        orderModel = OrderModel
                .builder()
                .orderCode(randomCode)
                .orderUsername("test_user")
                .shopName("test_shop")
                .build();

        System.out.println(">>> Create OrderModel: " + orderModel.toString());
        orderModelList.add(orderModel);
    }
    orderRepository.saveAll(orderModelList);

    min = orderRepository.findAll().get(0).getId().intValue();
    max = orderRepository.findAll().get(99).getId().intValue();


    // when
    int callId = (int) Math.floor(Math.random() * (max - min + 1) + min);
    System.out.println(">>> Call Id: " + callId);
    Optional<OrderModel> orderModel = orderRepository.findById((long) callId);
    System.out.println(">>> Read OrderModel: " + orderModel.toString());

    // then
    assertThat(orderModel.get().getId()).isEqualTo((long) callId);
    assertThat(orderModel.get().getOrderCode()).isNotNull();
    assertThat(orderModel.get().getOrderUsername()).isNotNull();
    assertThat(orderModel.get().getShopName()).isNotNull();
    assertThat(orderModel.get().getId()).isGreaterThan(0);

}
```
