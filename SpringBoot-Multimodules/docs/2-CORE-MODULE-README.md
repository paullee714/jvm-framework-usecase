# 코어모듈에 Entity 적용 해놓기

코어모듈은 우리가 만든 `module-api`, `module-stream` 에서 공동으로 사용하기 위해 만든 모듈이다.

가장 큰 이유는, 도메인 엔티티를 공유하려고 한다. 각각의 서버나 어플리케이션에서 따로 관리한다면, 엔티티 수정이 일어났을 경우 각각 서버나 어플리케이션에서 수정해야 하는 번거로움이 있다.

그래서 코어모듈에 엔티티를 만들어놓고, 각각의 모듈에서 코어모듈을 의존성으로 추가하면, 코어모듈에 있는 엔티티를 사용할 수 있다.


## 코어모듈 작성하기
- 이전시간에 작성했었던 `module-core` 모듈을 사용 한다.
- `module-core`의 `build.gralde.kts`에는 별다른 정보 없이 plugin, allOpen, noArg 의 영역만 선언 해 놓았다.
    ```kotlin
    plugins{

    }

    allOpen {
        annotation("javax.persistence.Entity")
        annotation("javax.persistence.Embeddable")
        annotation("javax.persistence.MappedSuperclass")
    }

    noArg {
        annotation("javax.persistence.Entity") // @Entity가 붙은 클래스에 한해서만 no arg 플러그인을 적용
        annotation("javax.persistence.Embeddable")
        annotation("javax.persistence.MappedSuperclass")
    }

    dependencies{

    }
    ```
- SpringBoot-Multimodules의 Root 폴더에서 이미 공통적으로 선언 해 놓았기 때문에 별도로 설정 하지 않아도, dependency들을 사용 할 수 있다.

### 엔티티를 넣을 패키지 선언하기
- `module-core`의 `src/main/kotlin` 폴더에 `com.wool.entity` 패키지를 만들어 준다.
- 여기서 `com.wool`은, `module-core`외에 `module-api`, `module-stream` 에서도 사용 해야 하기 때문에 잘 기억하자.
- Customer, Order 엔티티를 만들자

### 엔티티 만들기 1 - BaseEntity
- BaseEntity를 만들어주려고 한다. 다른 역할이 아니라 DB컬럼의 created_at, updated_at를 담당하게 하려고 한다.
- 그냥 작성하게되면 매번 패키지들마다 공통으로 계속 적어야 하지만 BaseEntity를 만들고 상속을 시키면, 모든 엔티티에서 created_at, updated_at이 사용 가능하다
- `com.wool.entity`패키지 하위에  `base` 패키지를 만들고, `BaseEntity` 데이터클래스를 생성한다.
    ```kotlin
    package com.wool.entity.base

    import org.springframework.data.annotation.CreatedDate
    import org.springframework.data.annotation.LastModifiedDate
    import org.springframework.data.jpa.domain.support.AuditingEntityListener
    import java.time.LocalDateTime
    import javax.persistence.Column
    import javax.persistence.EntityListeners
    import javax.persistence.MappedSuperclass

    @MappedSuperclass
    @EntityListeners(value = [AuditingEntityListener::class])
    open class BaseEntity(
        @CreatedDate
        @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "DATE")
        var createdAt: LocalDateTime = LocalDateTime.now(),

        @LastModifiedDate
        @Column(name = "updated_at", nullable = false, columnDefinition = "DATE")
        val updatedAt: LocalDateTime = LocalDateTime.now(),
    )
    ```
- 이제 BaseEntity를 가지고 Customer, Order 엔티티를 만들어보자

###  엔티티 만들기 2 - Customer
- Customer 엔티티를 만들자. 이 프로젝트는 엔티티를 얼마나 잘 작성하느냐 라기보다, 멀티모듈을 잘 사용 해 보자 이기 때문에 엔티티에 대한 상세한 설명이나 설정은 넘어가려고 한다.
- 아래와 같이 간단하게 customerNickname과 Address를 저장할 수 있는 Customer 엔티티를 생성한다.
- `BaseEntity()`를 상속하자
    ```kotlin
    package com.wool.entity

    import com.wool.entity.base.BaseEntity
    import org.jetbrains.annotations.NotNull
    import javax.persistence.Column
    import javax.persistence.Entity
    import javax.persistence.GeneratedValue
    import javax.persistence.GenerationType
    import javax.persistence.Id


    @Entity
    data class Customer(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val customerId: Long = 0,

        @NotNull
        @Column
        val customerNickName: String,

        @NotNull
        @Column
        val customerAddress: String,
    ): BaseEntity()
    ```

### 엔티티 만들기 3 - Order
- Order 엔티티를 만들자. Order는 주문정보와 Customer정보를 가지고 있게 만들자
- 마찬가지로 `BaseEntity()`를 상속하자
    ```kotlin
    package com.wool.entity

    import com.wool.entity.base.BaseEntity
    import org.jetbrains.annotations.NotNull
    import java.util.UUID
    import javax.persistence.*

    @Entity
    data class Order(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val orderId: Long = 0,

        @NotNull
        @Column
        val orderUUID: String = UUID.randomUUID().toString(),

        @NotNull
        @Column
        val orderStoreName: String,

        @NotNull
        @Column
        val orderStoreAddress: String,

        @NotNull
        @Column
        val orderItem: String,

        @NotNull
        @Column
        val orderPrice: Int,

        @ManyToOne(fetch = FetchType.EAGER)
        @JoinColumn(name = "customerId")
        val customer: Customer,
    ) : BaseEntity()
    ```

## module-core를 마치며
- 생각보다 `module-core`는 엔티티만 설정을 해 놓아서 크게 건들 것이 없었다.
- 이제 module-core를 마쳤다. 이제 module-core를 사용할 module-api를 만들어보자
