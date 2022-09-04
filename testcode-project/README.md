# 비밀번호 유효성 검증기 - TDD 활용하기

## TestCode 작성하는 이유

- 코드에대한 문서화
- 코드에 대한 결함을 발견하기 위함
- 리팩토링 시 안정성 확보

## TDD란?

- Test Driven Development
- 프로덕션 코드보다 테스트코드를 먼저 작성하는 개발 방법
- TFD(Test First Development) + Refactoring
- 기능 동작을 먼저 검증 (메소드 단위)

## BDD도 있던데?

- Behavior Driven Development
- 시나리오 기반으로 테스트코드를 작성하는 방법
- 하나의 시나리오는 Given / When / Then 구조

## 비밀번호 유효성 검증기

### 요구사항 및 기능명세

- 비밀번호는 최소 9자 이상 15자 이하
- 비밀번호가 9자 미만 또는 15자 초과인경우 Exception ㅂ라생
- 경계조건 확인

### Dependency

```groovy
plugins {
    id 'java'
}

group 'org.example'
version '1.0-SNAPSHOT'

repositories {
    mavenCentral()
}

dependencies {
    testImplementation 'org.junit.jupiter:junit-jupiter-api:5.9.0'
    testRuntimeOnly 'org.junit.jupiter:junit-jupiter-engine:5.9.0'
    testImplementation 'org.assertj:assertj-core:3.23.1'
}

test {
    useJUnitPlatform()
}
```

## 비밀번호 길이 검증 TestCode 작성

### 테스트 클래스 및 기본 클래스 생성하기

- 테스트코드의 패키지의 위치와, main 소스의 패키지의 위치가 동일하게 생성되면 좋다
- 테스트코드에서 요구조건을 먼저 실현하고, 실현한 요구조건에서 실제 코드에 반영되지 않은 부분들을 하나씩 만들어가며 작업

```java
package org.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * 비밀번호는 최소 9자 이상 15자 이하
 * 비밀번호가 9자 미만 또는 15자 초과인경우 Exception ㅂ라생
 * 경계조건 확인
 */
public class PasswordValidatorTest {

    @DisplayName("비밀번호가 최소 9자 이상 15자 이하면 정상") // 테스트 의도
    @Test
    void validatePasswordTest() {
        assertThatCode(() -> PasswordValidator.validate("123456789"))
                .doesNotThrowAnyException();
    }
}
```

- 작성 한 후, PasswordValidator 클래스에 붉은줄이 생성됨
- PasswordValidator를 main source에 생성
- PasswordValidator 클래스에 validate 메소드를 생성
    ```java
    package org.example;
    public class PasswordValidator {
        public static void validate(String password) {
        }
    }
    ```
- 테스트 진행 -> 성공

### 생성 된 클래스들을 Refactoring 하여 상세조건 반영하기

- 입력된 문자열이 9자 이상 15자 이하인지 확인하고, 아니라면 Exception 발생 코드 작성

```java
package org.example;

public class PasswordValidator {

    public static final String WRONG_PASSWORD_LENGTH_EXCEPTION_MESSAGE = "비밀번호는 최소 9자 이상 15자 이하입니다.";

    public static void validate(String password) {
        int length = password.length();

        if (length < 9 || length > 15) {
            throw new IllegalArgumentException(WRONG_PASSWORD_LENGTH_EXCEPTION_MESSAGE);
        }

    }
}
```

- 테스트실행 -> 성공

### 비밀번호가 9자 미만인 경우 테스트

- 위와 같은방법으로, TestCode에서 먼저 시도하고 실제 코드에 하나씩 반영하는 방법
- 이미 Exception 관련 내용을 작성했기 때문에 크게 수정 할 내용이 없다 조건만 테스트

```java
public class PasswordValidatorTest {
    @DisplayName("비밀번호가 9자 미만인 경우 Exception 발생")
    @Test
    void validatePasswordShortExceptionTest() {
        assertThatCode(() -> PasswordValidator.validate("1234"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(PasswordValidator.WRONG_PASSWORD_LENGTH_EXCEPTION_MESSAGE);
    }
}
```

### 비밀번호가 15자 초과인 경우 테스트

- 위와 같은방법으로, TestCode에서 먼저 시도하고 실제 코드에 하나씩 반영하는 방법
- 이미 Exception 관련 내용을 작성했기 때문에 크게 수정 할 내용이 없다 조건만 테스트

```java

public class PasswordValidatorTest {
    @DisplayName("비밀번호가 15자 초과인 경우 Exception 발생")
    @Test
    void validatePasswordLongExceptionTest() {
        assertThatCode(() -> PasswordValidator.validate("1234512345123456"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(PasswordValidator.WRONG_PASSWORD_LENGTH_EXCEPTION_MESSAGE);
    }
}
```

## 경계조건에 대한 테스트

- 우리의 요구조건에 부합하는 비밀번호의 길이는 9자 이상 15자 이하이다.
- 위의 조건에대한 경계값은 비밀번호가 8자, 혹은 16자 일 경우이다
- 경계값에 대한 테스트를 추가 해 주면 좋은 테스트를 작성할 수 있다.
- `@Parameterize` 를 추가하여 테스트 해 보자

### Dependency 추가

```groovy
testImplementation 'org.junit.jupiter:junit-jupiter-params:5.9.0'
```

### Parameterize를 이용한 테스트

```java
public class PasswordValidatorTest {

    @DisplayName("경계조건에 대해 테스트")
    @ParameterizedTest
    @ValueSource(strings = {"12345678", "1234567890123456"})
    void validatePasswordBoundaryTest(String password) {
        assertThatCode(() -> PasswordValidator.validate(password))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(PasswordValidator.WRONG_PASSWORD_LENGTH_EXCEPTION_MESSAGE);

    }

}
```

- ValueSource 내에 들어가있는 값들을 자동으로 바꿔가며 실행시켜 준다
- ValueSource 내에 선언된 데이터를 함수에서 하나씩 받아 줄 수 있다
- 실행을 해 보면, 다른 테스트와는 다르게 테스트가 두번 돌아간다

### 결과화면
<img src="https://user-images.githubusercontent.com/25498314/188338290-c43b141b-3c25-4413-b3a9-cac5fc531600.png" alt="테스트결과화면">
