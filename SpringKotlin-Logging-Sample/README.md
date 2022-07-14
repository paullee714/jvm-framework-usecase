# 로그란?

- 로그는 간단하게 말해서 연속된 데이터의 기록이라고 할 수 있다
- 일반적으로 처음 프로그래밍을 배울 때는 보통 System.out.print 사용을 많이한다
- 이 때, 프로그램이 실행되면서 콘솔에 무엇인가가 출력되는데, 이런 것들이 로그가 될 수 있다

## Logging Framework

- System.out.print를 사용하여 디버깅을 할 수 있지만, 만약 어플리케이션의 사이즈가 커지게 되면 이런 방식은 너무 비효율적이다
- 개발 후에 로그를 편하게 볼 수 있는 방법을 고려 해 놓는것도 좋은 프로그램을 개발하고 유지하기위한 방법이다

## Slf4j

- 로그를 남기고 추적하는 요구사항이 많이 생겨, 이와같은 요구사항들을 해소하고자 Loggin Framework이 생겼다
- 스프링의 Logging Framework 중 가장 유명한 라이브러리는 Slf4j 이다
- Slf4j를 사용하면 `logback`, `log4j`, `log4j2` 와 같은 구현체를 쉽게 교체하고 사용할 수 있다

## 로깅을 적용 해 보자

- 개발환경은 다음과 같다
    - SpringBoot
    - Kotlin
    - Gradle

### 의존성 설정 (build.gradle.kts)

```kotlin
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.7.1"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    kotlin("jvm") version "1.6.21"
    kotlin("plugin.spring") version "1.6.21"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.springframework.boot:spring-boot-starter-log4j2")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

configurations.forEach {
    it.exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
    it.exclude(group = "org.apache.logging.log4j", module = "log4j-to-slf4j")
}


tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

```

### log4j2 설정

- xml 설정방법과 Jackson을 사용한 설정방법을 사용하여 log4j2 설정을 할 수 있다
- Springboot의 `resource` 폴더 하위에 `xml`파일을 생성하자
- 생성한 `xml`파일을 `log4j2.xml`로 저장하고, 아래의 내용을 적어준다

```xml
  <?xml version="1.0" encoding="UTF-8" ?>
<Configuration status="INFO">
    <Properties>
        <Property name="LOG_PATTERN">%d{HH:mm:ss.SSSZ} [%t] %-5level %logger{36} - %msg%n</Property>
    </Properties>
    <Appenders>
        <Console name="ConsoleLog" target="SYSTEM_OUT">
            <PatternLayout pattern="${LOG_PATTERN}" charset="UTF-8"/>
        </Console>
    </Appenders>
    <Loggers>
        <Root level="info">
            <AppenderRef ref="ConsoleLog"/>
            <AppenderRef ref="FileLog"/>
        </Root>
    </Loggers>
</Configuration>
```

- Configuration: 로그 설정을 위한 최상위 요소
    - status 속성: Log4j2 내부의 동작에 대한 로깅 레벨을 설정 (log4j 내부 문제를 해결하기 위한 용도의 로깅이 필요한 경우 사용)
- Properties: 하단 설정에 사용할 변수들을 정의
    - name: 위 예제에서 name=”LOG_PATTERN”으로 설정하여 LOG_PATTERN이라는 변수를 정의
    - Appenders: 로그가 출력되는 위치
    - Console: 콘솔에 출력될 로그 설정
        - name: 어펜더의 이름
        - target: 로그 타겟 (default: SYSTEM_OUT)
    - PatternLayout: 로그의 패턴을 설정
- Loggers: 로깅 작업의 주체로 각 패키지 별로 다양한 설정을 할 수 있음
    - Root: 모든 패키지에 대한 로깅을 하기 위한 일반적인 로그 정책 설정 (한 개만 설정할 수 있음)
        - AppenderRef: 상단에 설정한 Appender를 참조

### TestController 작성

- TestController를 작성하고, 해당 컨트롤러에서 Log를 출력하는 작업을 하자
- Java에서는 Slf4j를 import하면, log를 바로 사용할 수 있지만 코틀린에서는 아래와같이 선언을 해 주어야한다.
- Kotlin에서 Logger를 설정하는방법은 여러가지가 있기 때문에 참고하면 좋은 링크를 아래에 넣는다
    - [Kotlin Logging](https://www.reddit.com/r/Kotlin/comments/8gbiul/slf4j_loggers_in_3_ways/)

```kotlin
package com.example.springkotlinloggingsample

import lombok.extern.slf4j.Slf4j
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Slf4j
@Controller
class TestController {
    val logger: Logger = LoggerFactory.getLogger(TestController::class.java)

    @GetMapping("/")
    fun index(): String {
        logger.info("Hello, This is INFO Message")
        logger.debug("Hello, This is DEBUG Message")
        logger.trace("Hello, This is TRACE Message")
        logger.warn("Hello, This is WARN Message")
        logger.error("Hello, This is ERROR Message")
        return "index"
    }
}
```

- 이제 api 호출을 통해, log를 출력 하면 아래와같이 로그가 출력되는것을 볼 수 있다
  ```text
   [http-nio-8080-exec-2] INFO  com.example.springkotlinloggingsample.TestController - Hello, This is INFO Message
   [http-nio-8080-exec-2] WARN  com.example.springkotlinloggingsample.TestController - Hello, This is WARN Message
   [http-nio-8080-exec-2] ERROR com.example.springkotlinloggingsample.TestController - Hello, This is ERROR Message
  ```
    - TRACE와 DEBUG는 출력되지 않는다
    - Logging의 레벨이 있는데, TRACE와 DEBUG는 로그레벨을 낮추는 설정을 추가로 해야 볼 수 있다
    - 로그의 레벨은 다음과 같다
      ```text
      TRACE > DEBUG > INFO > WARN > ERROR
      ```

### 로그를 파일로 남기자 (log4j2.xml을 수정하자)

- 로그가 log4j2.xml에서 설정한 형태로 잘 출력이 되는 것을 볼 수 있다
- 로그는 서버가 끊기더라도 다시 찾아 볼 수 있어야하기 떄문에, 기타 다른 플랫폼으로도 보내어 로그를 확인하는 방법들을 많이 사용한다
- 이번 예제에서는 간단하기 "파일"로만 남기는 작업을 해 보려고 한다
- log4j2.xml을 아래와같이 수정한다
  ```xml
  <?xml version="1.0" encoding="UTF-8" ?>
  <Configuration status="INFO">
      <Properties>
          <Property name="LOG_PATTERN">%d{HH:mm:ss.SSSZ} [%t] %-5level %logger{36} - %msg%n</Property>
      </Properties>
      <Appenders>
          <Console name="ConsoleLog" target="SYSTEM_OUT">
              <PatternLayout pattern="${LOG_PATTERN}" charset="UTF-8"/>
          </Console>
          <RollingFile name="FileLog"
                       fileName="./logs/spring.log"
                       filePattern="./logs/spring-%d{yyyy-MM-dd}-%i.log">
              <PatternLayout pattern="${LOG_PATTERN}" charset="UTF-8" />
              <Policies>
                  <TimeBasedTriggeringPolicy interval="1" />
                  <SizeBasedTriggeringPolicy size="10000KB" />
              </Policies>
              <DefaultRolloverStrategy max="20" fileIndex="min" />
          </RollingFile>
      </Appenders>
      <Loggers>
          <Root level="info">
              <AppenderRef ref="ConsoleLog" />
              <AppenderRef ref="FileLog" />
          </Root>
      </Loggers>
  </Configuration>
  ```
    - RollingFile: 조건에 따라 파일에 로그를 출력하도록 설정
        - name: 어펜더의 이름
        - fileName: 경로를 포함한 파일 이름
        - filePattern: 롤링 조건에 따른 경로를 포함한 파일 이름 패턴
        - Policies: 파일 롤링 정책
            - TimeBasedTriggeringPolicy: 1일 단위(interval=1)로 새로운 파일에 로그를 기록
            - SizeBasedTriggeringPolicy: 파일 사이즈를 기준으로 용량이 넘칠 경우 다음 파일을 생성하여 기록
            - DefaultRolloverStrategy: 파일 용량 초과 시 생성될 수 있는 파일의 최대 개수 설정

    - 위와같이 설정하고나면, log파일이 springboot 아래의 logs 폴더가 생성되며 쌓이게 된다
