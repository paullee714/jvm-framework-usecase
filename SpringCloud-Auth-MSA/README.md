# MSA를 위한 Eureka, Gateway, Config, Auth, Message 서비스 살펴보기

- [MSA관련 서비스에서 Eureka, Gateway](https://github.com/paullee714/jvm-framework-usecase/tree/develop/SpringBoot-Cloud-Gateway-Simple)
  를 살짝 다뤄보았지만 좀 더 자세히 정리하고 적용하고싶어서 정리 해 본다
- 위의 링크에 정리했었던 내용 중, `application.properties` 대신 `application.yaml` 파일을 사용 해 보려고 한다
- 기본적인 Gateway, Config서버와 연동을 완료 한 후 각각의 서비스를 구체화 해 보려고 한다
- 가장 기본적인 구성으로 아래의 그림과 같은 형태를 만들려고 한다
  <img src="https://user-images.githubusercontent.com/25498314/167964824-4eaf458d-d561-4df6-ba89-c845ed429d62.png" width=680>
    - 제공할 기능들을 담은 서버를 여러개의 Spring 서버로 나눈다
    - 각각의 서버들은 보안을 위해서 임의의 포트를 갖게된다
    - Eureka서버에서 서비스들을 관리하고 볼 수 있도록 서비스들을 Eureka서버에 등록한다
    - Gateway를 두고 하나의 창구를 만든 후에 여러가지 서비스들을 Forwarding 할 수 있도록 해 준다
    - 서비스 포워딩, 인증, 서비스간 통신 들을 작업 해 보려고 한다

## Eureka의 동작과정

- Eureka Server 동작
- Service1, Service2를 Eureka서버에 등록 (Eureka Client)
- Eureka Server는 자신에게 등록 된 치ㅑ둣들에게 30초마다 Ping을 보내어 상태를 점검한다
- 일정 횟수 이상 Ping 응답이 없으면 Client가 죽었다고 판단 (삭제)

## DB 세팅하기

- 데이터를 담을 데이터베이스를 세팅하자
- AWS나 Azure 혹은 Cloud상의 DB를 사용해도 된다
- 나는 이 프로젝트에만 단일로 데이터베이스를 사용하고싶어 프로젝트의 docker-compose를 구축했다

### docker-compsose-database.yml

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
      MYSQL_DATABASE: basketball
      MYSQL_USER: paul
      MYSQL_PASSWORD: qwerqwer123
    container_name: "docker-maria"
    volumes:
      - ./docker-database/maria:/var/lib/mysql

```

## Eureka 세팅하기
### 준비물
- Eureka Server
- Eureka Client
    - Eureka Server에 등록 할 서비스

### Eureka Server 실행
- 우리가 만들 유레카서버는 엔드포인트들을 관리하기 위해 만들기 때문에 Dependency에서 `Spring Cloud Eureka Server`만 설치 해 주면 된다
- 크게 세팅 할 것이 없기때문에 `Dependency`, `application.yml`, `어플리케이션 유레카서버 어노테이션` 만 체크 해 주고 넘어가면 될 것 같다

### Application Dependency
- `Spring Cloud Eureka Server`

### application.yml
- SpringBoot 프로젝트를 생성하면 처음에 `application.properties`가 나오는데 이 파일은 적을 때 마다 느끼는것이지만 너무 flat 하다
- 중복의 여지가 있기 때문에 hierarchy를 사용해서 적을 수 있는 application.yml을 사용하려고 한다
    ```properties
    server:
      port: 8761

    spring:
      application:
        name: spring-eureka-server

    eureka:
      client:
        register-with-eureka: false
        fetch-registry: false

    ```
    - eureka.client.register-with-eureka : 해당 서버를 클라이언트로 동작시킬 것 이냐는 설정
        - 지금 만들고있는 서버는, Eureka의 클라이언트들을 등록하기위한 "서버" 모델이기 때문에 `false`로 설정
    - eureka.client.fetch-registry : 위와 비슷 한 내용. `true`로 등록 시, 자신을 Eureka서버의 클라이언트로 등록하게 된다

### @EnableEurekaServer
- 스프링부트를 유레카 서버로 설정하겠다라는 어노테이션
- 스프링부트 스타터클래스에 달아주면 됨
  ```java
  package com.example.springcloudeureka;

  import org.springframework.boot.SpringApplication;
  import org.springframework.boot.autoconfigure.SpringBootApplication;
  import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

  @SpringBootApplication
  @EnableEurekaServer
  public class SpringCloudEurekaApplication {

      public static void main(String[] args) {
          SpringApplication.run(SpringCloudEurekaApplication.class, args);
      }

  }
  ```

## Eureka Client : MSA-Player, MSA-NBATeam
- 유레카에 등록할 서비스로 농구 구단을 관리하는 서비스를 구상하려고 한다
- 우선은 Eureka 에 등록 하고 확인 할 수 있을정도의 Controller만 구성한다

### application.yml
- Springboot를 실행하면 생성되는 application.properties를 삭제하고 application.yml을 생성한다
- MSA-Player와 MSA-NBATeam 을 설정 할 때, `server.port` 와 `appliction.name`을 다르게 생성 해 주면 된다
- 나는 `MSA-Player` 서버 포트는 54293, `MSA-NBATeam` 서버 포트는 41523 으로 했다
- `application.name`은 각각 `PLAYER-SERVER` `NBATEAM-SERVER`으로 했다
  ```yaml
  server:
    port: 54293

  spring:
    application:
      name: PLAYER-SERVER

  eureka:
    instance:
      instance-id: ${spring.application.name}-${random.uuid}
    client:
      register-with-eureka: true
      fetch-registry: true
      service-url:
        defaultZone: http://localhost:8761/eureka
  ```
### @EnableEurekaClient
- 각 서비스정보를 Eureka Server에 등재하기위해 `SpringBoot Starter class`에 추가 해 주었다
- 아래의 소스코드는 `MSA-Player`서비스인데, `MSA-NBATeam`에도 동일하게 어노테이션을 적용 해 주면 된다
```java
package com.example.msaplayer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class MsaPlayerApplication {

  public static void main(String[] args) {
    SpringApplication.run(MsaPlayerApplication.class, args);
  }

}

```

### Controller : MSA-Player, MSA-NBATeam
- 가장 기본적인 컨트롤러 기능만 사용했다
- `@RestController` 와 `@RequestMapping` 으로 기본 url을 잡아 주었다
- `@Value`를 사용해 `application.yml`에 있는 서버정보를 가져와 API 요청시 출력 해 주었다
```java
// MSA-Player.PlayerController.java
package com.example.msaplayer.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/player")
public class PlayerController {

  @Value("${server.port}")
  private String port;

  @Value("${spring.application.name}")
  private String name;

  @GetMapping("/server-info")
  public String getPlayer() {
    return "SERVER: [" + name + "] Player Server is running on port " + port;
  }
}
```
```java
// MSA-NBATeam.TeamController.java
package com.example.msanbateam.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/team")
public class TeamController {

  @Value("${server.port}")
  private String port;

  @Value("${spring.application.name}")
  private String name;

  @GetMapping("/server-info")
  public String getPlayer() {
    return "SERVER: [" + name + "] Team Server is running on port " + port;
  }
}
```

## Eureka Server, Eureka Client 확인하기
- 실행 순서는
  - Eureka Server 실행
  - MSA-Player 혹은 MSA-NBATEAM 실행 (Eureka Client)
  - 서비스 끼리의 실행순서는 상관없지만, `Eureka Server`가 `Client`보다 항상 먼저 실행되어야 한다
