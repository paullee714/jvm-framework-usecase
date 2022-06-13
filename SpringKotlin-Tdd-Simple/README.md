# Spring Kotlin으로 TDD 연습하기

- TDD를 Kotlin으로 적용 해 보고자 한다
- Repository -> Service -> Controller 순서로 개발을 진행한다.
- Repository 계층의 테스트는 H2와 같은 인메모리 데이터베이스 기반의 통합 테스트로 진행한다.
- Service 계층의 테스트는 Mockito를 사용해 Repository 계층을 Mock하여 진행한다.
- Controller 계층의 테스트는 SpringTest의 MockMvc를 사용해 진행한다.

## 요구사항 분석

- 요구사항이 있어야 테스트코드를 작성한다
- 테스트코드를 가지고 기초적인 뼈대와 흐름을 파악/작성

### Problem Solve

- 할인을 선택적으로 할 수 있는 카드를 만들고자한다
- 선택적으로 할인을 할 수 있어야 하기 때문에 제휴사 연결, 나의 제휴사 조회, 제휴사 포인트 조회 기능이 있어야 한다
- 간단한 어플리케이션이기 때문에 사용자 식별값은 임의의 uuid를 사용한다

### 요구사항 구현 - 상세 기술정리

- 나의 할인 제휴사 등록하기
    - 카드를 사용 할 때 할인받을 제휴사를 등록합니다
    - request: 사용자 uuid, 제휴사 이름
    - response: 제휴 id
- 나의 할인 전체/특정제휴사 조회하기
    - 카드를 사용 할 때 할인받을 제휴사를 조회합니다
    - request: 사용자 uuid
    - response: 제휴사 이름, 할인 포인트의 리스트
- 나의 할인 제휴사 포인트 적립하기
    - 카드를 사용 할 때 선택 한 제휴사 1곳에 포인트를 적립합니다
    - request: 사용자 uuid, 제휴 id, 사용금액
    - response: 보유한 제휴사 이름, 할인 포인트의 리스트
- 나의 할인 제휴사 포인트 조회하기
    - 등록한 제휴사에 저장 된 포인트를 조회합니다
    - request: 사용자 uuid, 제휴 id
    - response: 제휴사 이름, 포인트
- 나의 할인 제휴사 포인트 차감
    - 카드를 사용 할 때 선택 한 제휴사 1곳에 포인트를 차감합니다
    - request: 사용자 uuid, 제휴 id
    - response: 제휴사 이름, 할인 포인트의 리스트
- 나의 할인 제휴사 삭제하기
    - 카드를 사용 할 때 할인받을 제휴사를 삭제합니다
    - request: 사용자 uuid, 제휴 id
    - response: 보유한 제휴사 이름, 할인 포인트의 리스트

## 필요 기술 정리

- SpringBoot
- Kotlin
- MariaDB
- H2
- Spring Data JPA

### DB세팅

- 개인 Docker-compose에 Maria DB를 설치하여 사용한다

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
        MYSQL_DATABASE: wool
        MYSQL_USER: wool
        MYSQL_PASSWORD: qwerqwer123
      container_name: "docker-mysql"
      env_file: .mysql_env
      volumes:
        - ./mariadb:/var/lib/mysql
```

### Dependency

```
implementation("org.springframework.boot:spring-boot-starter-data-jpa")
implementation("org.springframework.boot:spring-boot-starter-web")
implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
implementation("org.jetbrains.kotlin:kotlin-reflect")
implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
implementation("com.google.code.gson:gson:2.9.0")
compileOnly("org.projectlombok:lombok")
runtimeOnly("org.mariadb.jdbc:mariadb-java-client")
runtimeOnly("com.h2database:h2")
annotationProcessor("org.projectlombok:lombok")
testImplementation("org.springframework.boot:spring-boot-starter-test")
```

#### application.yml

```yaml
spring:
  datasource:
    driver-class-name: org.h2.Driver
    url: jdbc:h2:mem:db;DB_CLOSE_DELAY=-1
    username: h2test
    password: h2test


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
        dialect: org.hibernate.dialect.H2Dialect

  h2:
    console:
      enabled: true
```

## Repository 테스트하기

- 개발순서가 repository -> service -> controller 순 이기 때문에 가장먼저 DB커넥션과 Entity테스트 등을 진행한다

### Reposiotry Test Package 생성하기

- test 경로에 `repository` 패키지를 생성한다
- `repository` 패키지에 `AssociationRepositoryTest.kt` 파일을 생성한다
- 가장 먼저 진행 할 테스트는 Repository가 `NotNull`인지 판단 해 보자

### AssociationRepositoryTest.kt 작성하기

```kotlin
package com.example.springkotlintddsimple.repository

import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.assertThat
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest


@DataJpaTest
class AssociationRepositoryTest {

    @Autowired
    lateinit var associationRepository: AssociationRepository

    @Test
    fun AssociationRepositoryisNotNull() {
        assertThat(associationRepository).isNotNull()
    }
}
```

- `AssociationRepository`가 작성되어있지 않으므로, 위의 시나리오는 붉은줄로 컴파일 에러를 뱉을 것 이다
- 이제 비어있는 `AssociationRepository`과 `AssociationRepository`에서 사용 할 DataClass를 만들어 주자

### AssociationRepository.kt, Data Class 작성하기

- 테스트에 사용 할 Repository를 생성한다
- `AssociationRepository`에서 사용 할 DataClass 생성한다
- Domain 객체
  ```kotlin
  //domain package의 Association.kt 데이터클래스
  package com.example.springkotlintddsimple.domain

  import java.time.LocalDateTime
  import javax.persistence.Entity
  import javax.persistence.GeneratedValue
  import javax.persistence.Id

  @Entity
  data class Association(
    @Id
    @GeneratedValue
    var id: Long,
    var associateName: String,
    var userUuid: String,
    var point: Int,
    var createdAt: LocalDateTime = LocalDateTime.now(),
  )
  ```
    - 사용할 데이터와 테이블과 관련 된 요소들을 데이터 클래스 안에 작성한다

- Repository
  ```kotlin
  //repository package의 AssociationRepository.kt 인터페이스
  package com.example.springkotlintddsimple.repository

  import com.example.springkotlintddsimple.domain.Association
  import org.springframework.data.jpa.repository.JpaRepository

  interface AssociationRepository : JpaRepository<Association, Long>{
  }
  ```
    - JPA 상속을 해 준다
    - 원하는 쿼리가 있다면 차후 작성하자

### AssociationRepositoryTest.kt 테스트하기 - 성공

- 지금까지 작성 된 AssociationRepositoryTest를 테스트하자
- Repository, Data Class 까지 작성이 잘 되었다면 현재 테스트는 `NotNull`만 체크하기 때문에 성공 할 것이다
- 실패했다면 어노테이션이 잘 붙어있는지, 엔티티 작성이 잘 되었는지, JPA상속이 잘 되어있는지 확인 해 주자

### AssociationRepositoryTest.kt 테스트 개선 -> 실제 저장 로직 구현해보기

- 위의 테스트가 성공했다면 `NotNull`체크는 굳이 필요하지 않다. (제거해도 된다)
- 이제 `AssociationRepository`에서 저장 로직(제휴 업체 등록)을 구현해 보자
- KFC 를 임의의 유저가 등록한다고 가정하고 코드를 작성 해 보자
- Java에서 Builder패턴을 사용해서 코드를 작성하고 데이터를 저장했다면 Kotlin에서는 바로 작성이 가능하다
  ```kotlin
  @Test
  fun AssociationRepositoryRegistration() {
      // given
      val association: Association = Association(1, "KFC", "uuid-wool-1", 0)

      // when
      var result: Association = associationRepository.save(association)

      // then
      assertThat(result.id).isNotNull()
      assertThat(result.id).isEqualTo(1)
      assertThat(result.associateName).isEqualTo("KFC")
      assertThat(result.userUuid).isEqualTo("uuid-wool-1")
      assertThat(result.point).isEqualTo(0)
  }
  ```
    - 위에서 저장되는 Association 객체는 id 1, associateName "KFC", userUuid "uuid-wool-1", point 0 이다
    - assertThat에서 에러가 나지 않음을 확인했다면, KFC와같은 제휴업체들이 무작위로 등록되지 않게 ENUM 화 해 주자

### Association에 ENUM 추가하고 테스트하기

- Association ENUM 클래스를 작성하여 데이터를 열거 해 주자
- 제휴 업체가 아닌곳에서는 데이터를 넣을 수 없도록 방지하는 기능이기도 하고, 들어오는 데이터의 일관성을 유지하기 위함
- 테스트 코드에 Association ENUM 클래스를 사용하여 제휴 업체를 넣자
  ```kotlin
  @Test
  fun AssociationRepositoryRegistration() {
      // given
      val association: Association = Association(1, AssociationName.KFC, "uuid-wool-1", 0)

      // when
      var result: Association = associationRepository.save(association)

      // then
      println(result)
      assertThat(result.id).isNotNull()
      assertThat(result.id).isEqualTo(1)
      assertThat(result.associateName).isEqualTo(AssociationName.KFC)
      assertThat(result.userUuid).isEqualTo("uuid-wool-1")
      assertThat(result.point).isEqualTo(0)
  }
  ```
    - "KFC"를 ENUM 타입으로 바꿨다
    - 아직 패키지와 객체를 작성하지 않아서 에러가 났지만 에러를 내고 해결하는 방법으로 개발 해 나간다
- 테스트코드에 검출되는 오류를 해결하기 위해 domain 패키지에 AssociationName.kt를 작성한다
  ```kotlin
  package com.example.springkotlintddsimple.domain

  enum class AssociationName(val value: String) {
      KFC("KFC"),
      BURGER_KING("Burger King"),
      SUBWAY("Subway"),
      OLIVE_YOUNG("Olive Young"),
      PIZZA_HUT("Pizza Hut"),
      MACDONALDS("MacDonalds"),
  }
  ```