# SpringBoot로 만드는 어플리케이션을 MSA로 만들기까지

## 목차

- [어플리케이션 요구사항](#어플리케이션-요구사항)
- [작성하기](#작성하기)
    - [비즈니스로직 만들기](#비즈니스로직-만들기)
    - [엔드포인트 만들기](#엔드포인트-만들기)
    - [데이터 레이어](#데이터-레이어)
    - [사용자 페이지 만들기](#사용자-페이지-만들기)
- [기본적인 어플리케이션 구성하기](#기본적인-어플리케이션-구성하기)
    - [기본적인 곱셈 도메인모델 만들어 테스트하기](#기본적인-곱셈-도메인모델-만들어-테스트하기)
    - [Multiplication 도메인 모델과 인터페이스 작성하기](#multiplication-도메인-모델과-인터페이스-작성하기)
    - [Multiplication 객체에 사용되는 RandomGenerator 객체 테스트](#multiplication-객체에-사용되는-randomgenerator-객체-테스트)
    - [RandomGeneratorImplTest와 MultiplicationServiceImplTest 작성](#randomgeneratorimpltest와-multiplicationserviceimpltest-작성)
- [도메인 설계](#도메인-설계하기)
    - [Lombok 패키지 설치](#Lombok-패키지-설치)
    - [간단한 lombok 어노테이션 정리](#간단한-lombok-어노테이션-정리)
    - [Multiplication Domain 객체 수정](#Multiplication-Domain-객체-수정)
    - [User Domain 객체 추가](#User-Domain-객체-추가)
    - [MultiplicationResultAttempt Doamin 객체 추가](#MultiplicationResultAttempt-Doamin-객체-추가)

## 어플리케이션 요구사항

- 사용자가 페이지에 접속할 때마다 두 자릿수 곱셈 보여주기
- 사용자는 암산결과와 닉네임을 입력하고 전송
- 계산 결과(성공/실패) 가 나타남
- 계산 결과에 따라 매일 사용자 순위를 결과페이지에 보여줌

## 작성하기

- DDD, TDD를 사용해서 설계 함
- 애매한 요구사항을 명확하게 정리하고 유효하지 않은 요구사항은 거부할 수 있음
- (주의) 테스트만 통과시키고 리팩터링은 나중에하자라는 생각은 없어야함

### 비즈니스로직 만들기

- 비즈니스레이어라고도 하며, 도메인과 비즈니스를 명세로 한 모델링 된 클래스
- 어플리케이션의 두뇌 역할을 함
- 해당 레이어를 도메인 / 서비스 로 나눌 수 있음

### 엔드포인트 만들기

- 프레젠테이션 레이어라고도 함
- 웹 클라이언트에 기능을 제공하는 컨트롤러 클래스가 해당 됨
- 대부분 REST API 형태

### 데이터 레이터

- 개체들을 데이터 스토리지나 데이터베이스에 보관함
- DAO (Data Access Object) 또는 저장소 클래스를 포함
- DAO는 데이터베이스 모델을 다루고, 저장소 클래스는 도메인을 데이터베이스 레이어로 변환하는 클래스

### 사용자 페이지 만들기

- 사용자가 시스템과 인터렉션 할 수 있는 페이지 구성

## 기본적인 어플리케이션 구성하기

### 기본적인 곱셈 도메인모델 만들어 테스트하기

- TDD는 기본적인 로직들 보다 테스트 코드를 먼저 작성함
- 해당하는 테스트를 실패하게 만든 후, 성공하는 로직을 작성
- 이런 과정을 거치면, 세세한 요구사항에 대해 생각하게 되기 때문에 요구사항이 명확 해 짐

### Multiplication 도메인 모델과 인터페이스 작성하기

- [Multiplication.java](src/main/java/com/example/springbootmsacalculator/domain/Multiplication.java) 도메인 모델 작성하기
    - 도메인 모델을 통해서 어플리케이션에서 사용 할 모델을 작성 함
    - `모델`이라함은 데이터베이스와 연관이 있을 수 있지만, 여기서 작성하는 `모델`은 실제 비즈니스와 연관되어있는 형태
    - Multiplication 도메인모델은, 인수 2개, 곱셈 결과 1개를 가지고있는 모델

- [MultiplicationService.java](src/main/java/com/example/springbootmsacalculator/service/MultiplicationService.java)
  인터페이스 작성하기
    - 서비스 인터페이스를 정의 함
    - 테스트할 서비스 -> 여기서는 무작위 인수를 담은 Multiplication 객체 생성
    - 여기서 사용 할 무작위 인수 2개를
      만들어줄 [RandomGeneratorService.java](src/main/java/com/example/springbootmsacalculator/service/RandomGeneratorService.java)
      인터페이스 추가
    - 테스트 내에서 난수를 생성한다면 테스트 해야 할 부분에 집중하지 못하기때문에 따로 생성

- [MultiplicationServiceTest.java](src/test/java/com/example/springbootmsacalculator/service/MultiplicationServiceTest.java)
  서비스 테스트 작성하기
    - 작성한 Multiplication 도메인모델, MultiplicationService 인터페이스를 기반으로 테스트코드 작성
    - `@MockBean`을 통해서 당장 구현하지 않을
      서비스[RandomGeneratorService.java](src/main/java/com/example/springbootmsacalculator/service/RandomGeneratorService.java)
      에 대해 `Mock객체`를 주입
    - `MockitoBDD`를 통해서 BDD적용 -> `given` / `when` / `assert` 로 나누어 작성
    - `@MockBean`이 주입되지 않은 `MultiplicationService`에 대하여 테스트코드 내에서 에러 발생
        - 해결을 위해, `MultiplicationServiceImpl.java` 를 만들어 요구사항을 반영

- [MultiplicaionServiceImpl.java](src/main/java/com/example/springbootmsacalculator/service/MultiplicationServiceImpl.java)
  서비스 구현 작성하기
    - 실제 요구사항이 들어가는 서비스 객체 작성
    - `@Service` 어노테이션을 사용하여 서비스 객체로 등록
    - `@Autowired` 어노테이션을 사용하여 의존관계 주입
    - `@Override` 어노테이션을 사용하여 인터페이스에서 정의 한 메서드 구현
- 테스트하기
    - Intellij IDEA 에서 테스트 실행
    - 프로젝트가 maven이라면 maven wrapper를 사용해서 `mvnw -Dtest=MultiplicationServiceTest test`로 실행
    - 테스트 간 의존성 주입이나 빈을 찾지 못하는 에러가 난다면 Intellij의 Gradle 혹은 maven 설정에서 `빌드 및 실행`과 관련된 메뉴에서 `다음을 사용하여 테스트 실행` 항목을 Intellij
      로 해주어 해결

### Multiplication 객체에 사용되는 RandomGenerator 객체 테스트

- [MultiplicationService.java](src/main/java/com/example/springbootmsacalculator/service/MultiplicationService.java)
  , [MultiplicaionServiceImpl.java](src/main/java/com/example/springbootmsacalculator/service/MultiplicationServiceImpl.java)
  에서 계속 사용되고있는 난수생성객체인 `RandomGenerator`와 관련된 객체에 난수생성기능을 넣고 관련 객체들을 테스트

- [RandomGeneratorServiceImpl.java](src/main/java/com/example/springbootmsacalculator/service/RandomGeneratorServiceImpl.java)
  서비스 구현 작성하기
    - `@Service` 어노테이션을 사용하여 서비스 객체로 등록
    - `@Autowired` 어노테이션을 사용하여 의존관계 주입
    - `@Override` 어노테이션을 사용하여 인터페이스에서 정의 한 메서드 구현
    - `Random()` 객체를 사용해서 난수생성로직 생성
- [RandomGeneratorServiceTest.java](src/test/java/com/example/springbootmsacalculator/service/RandomGeneratorServiceTest.java)
  테스트 객체 작성하기
    - `@RunWith` 어노테이션을 사용하여 `MockitoJUnitRunner`를 사용하여 테스트 실행
    - `@MockBean` 어노테이션을 사용하여 `RandomGenerator` 객체를 주입
    - `@Test` 어노테이션을 사용하여 테스트 실행
    - `@MockitoBDD`를 사용하여 BDD적용
    - `given` / `when` / `assert` 로 나누어 작성
- `@SpringBootTest` 사용?
    - SpringRunner는 애플리케이션 컨택스트를 초기화하고 객체를 주입하는 역할
    - 컨택스트는 캐시로 재사용 할 수 있어서 테스트 당 한번만 로딩
    - 단순히 하나의 클래스(`RandomGeneratorService`) 테스트를 위해서는 컨택스트 로딩이 필요하지 않음
    - 여러 클래스간의 상호작용을 확인하는 통합테스트에서 `@SpringBootTest` 사용하는 것이 효율적임

### RandomGeneratorImplTest와 MultiplicationServiceImplTest 작성

- [RandomGeneratorImplTest.java](src/test/java/com/example/springbootmsacalculator/service/RandomGeneratorImplTest.java)
  테스트 객체 작성하기
- [MultiplicationServiceImplTest.java](src/test/java/com/example/springbootmsacalculator/service/MultiplicationServiceImplTest.java)
  테스트 객체 작성하기

## 도메인 설계하기

### Lombok 패키지 설치

도메인모델 내부에 getter, toString 및 기타 생성자의 기본적인 생성을 위해서 `lombok`이라는 패키지를 사용하려고 한다

- maven(pom.xml)
  ```xml
  <dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <version>1.18.22</version>
  </dependency>
  ```
- gradle(build.gradle)
  ```
  implementation 'org.projectlombok:lombok:1.18.22'
  ```

### 간단한 lombok 어노테이션 정리

- `@RequiredArgsConstructor` : 모든 상수 필드를 갖는 생성자 생성
- `@Getter` : 모든 필드에 getter를 만듬
- `@ToString` : 해당 클래스에 toString() 생성
- `@EqualsAndHashCode` : equals() 와 hashCode()메서드 생성

### Multiplication Domain 객체 수정

```java
package com.example.springbootmsacalculator.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
public class Multiplication {

    // 인수
    private final int factorA;
    private final int factorB;

    Multiplication() {
        this(0, 0);
    }

}
```

### User Domain 객체 추가

```java
package com.example.springbootmsacalculator.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
public final class User {

    private final String alias;

    private User() {
        alias = null;
    }
}
```

### MultiplicationResultAttempt Doamin 객체 추가

```java
package com.example.springbootmsacalculator.domain;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
public final class MultiplicationResultAttempt {

    private final User user;
    private final Multiplication multiplication;
    private final int resultAttempt;

    MultiplicationResultAttempt() {
        user = null;
        multiplication = null;
        resultAttempt = -1;
    }

}
```

## 비즈니스 로직 레이어생성

- 위에서 도메인 모델을 정의했으니, 비즈니스 로직을 생성하자
- 비즈니스 로직에서 필요한 역할은 아래의 두가지이다
    - 제출한 답안의 정답여부 확인
    - 곱셈 만들어내기

### MultiplicationService.java

```java
package com.example.springbootmsacalculator.service;


import com.example.springbootmsacalculator.domain.Multiplication;
import com.example.springbootmsacalculator.domain.MultiplicationResultAttempt;

public interface MultiplicationService {

    Multiplication createRandomMultiplication();

    boolean checkAttempt(final MultiplicationResultAttempt resultAttempt);

}
```

- 기존의 인터페이스에서, `checkAttempt`를 추가 해 주자
- 곱셈결과를 판단하는 함수로 사용하고, 구현체에서 구현 해 주도록 하자

### MultiplicationServiceImpl.java

- 테스트를 진행하기 위해서 우선 항상 false가 되는 로직을 구현하자
  ```java
  @Override
  public boolean checkAttempt(final MultiplicationResultAttempt resultAttempt) {

      return false;
  }
  ```

### 테스트코드 작성 -> MultiplicationServiceImplTest.java
```java
@Test
public void checkCorrectAttemptTest() {
    //given
    Multiplication multiplication = new Multiplication(50, 60);
    User user = new User("wool");
    MultiplicationResultAttempt attempt = new MultiplicationResultAttempt(user, multiplication, 3000);

    //when
    boolean attemptResult = multiplicationServiceImpl.checkAttempt(attempt);

    //assert
    assertThat(attemptResult).isTrue();
}

@Test
public void checFalseAttemptTest() {
    //given
    Multiplication multiplication = new Multiplication(50, 60);
    User user = new User("wool");
    MultiplicationResultAttempt attempt = new MultiplicationResultAttempt(user, multiplication, 3010);

    //when
    boolean attemptResult = multiplicationServiceImpl.checkAttempt(attempt);

    //assert
    assertThat(attemptResult).isFalse();
}
```
- 기존의 테스트코드에 `Attempt` 와 관련된 함수들을 2개 생성하여 맞는경우, 틀릴경우를 분리 해 놓는다
- `given`, `when`, `then` 으로 나누어서 주어진 값, 조건, 결과 로 나누어 작성한다

### MultiplicationServiceImpl.java 작성
- 아까 작성했던 구현체에 로직을 넣는다
    ```java
    @Override
    public boolean checkAttempt(final MultiplicationResultAttempt resultAttempt) {

        return resultAttempt.getResultAttempt() == resultAttempt.getMultiplication().getFactorA() * resultAttempt.getMultiplication().getFactorB();
    }
    ```
