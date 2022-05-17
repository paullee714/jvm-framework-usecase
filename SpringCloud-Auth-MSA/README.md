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

## API Gateway 설정하고 실행하기

- `MSA-Player`와 `MSA-NBATeam`는 서로 다른 포트의 서버에서 실행중에 있다
- `Eureka`는 이 두 서버를 관리하고 등록하여 볼 수 있다
- MSA로 구성한 서버의 개수가 늘어나면서 port 관리와 등록, 인증절차가 매우 까다로워지는 현상이 있다
- 이 어려움을 API Gateway가 해소 해 준다

### 용어설명

- Route : 목적지의 URI, 조건, Filter 등을 이용하여 어느곳으로 Routing 될 지 명시
- Predicate : 경로의 조건 (`/team/**`) -> URI가 team 이하의 모든 곳
- Filter : Request/Response 되는 객체를 특정 필터를 거치게 설정 함으로써 헤더 조작 및 객체수정, 로그파일 작성 등을 할 수 있다

### API Gateway application.yml

```yaml
spring:
  application:
    name: GATEWAY-SERVER

server:
  port: 8000

eureka:
  client:
    fetch-registry: true
    register-with-eureka: true
    service-url:
      defaultZone: http://localhost:8761/eureka
```

- gateway도 Eureka에 등록 될 서비스 중 하나이기 때문에 다른 서비스들과 동일하게 eureka의 설정을 적어준다
- 이부분은 마찬가지로 SpringBoot Starter Class에도 적용 해준다

### SpringBoot Starter @EnableEurekaClient

```java
package com.example.springcloudgateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class SpringCloudGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringCloudGatewayApplication.class, args);
    }

}
```

- `@EnableEurekaClient` 어노테이션으로 Eureka 서버에 등록 해준다

### Gateway 실행 및 Eureka Server, client 연동

- 우리는 모든 서비스를 Gateway를 통해서 접근 하게 해 줄것이다
- 이를 위해서 앞서 설명한 `route`, `filter`, `predicate` 를 설정 해 주려고 한다
- `Configuration Class`를 작성해서 정보를 넣을수도 있고, `application.yml`에 작성해서 정보를 넣어주어도 된다

### Gateway 설정 - yml

- gateway 서버의 `application.yml` 파일에 아래와같이 cloud.gateway 설정을 추가 해준다
  ```yaml
  spring:
    application:
      name: GATEWAY-SERVER
    cloud:
      gateway:
        routes:
          - id: NBATEAM-SERVER
            uri: lb://NBATEAM-SERVER
            predicates:
              - Path=/team/**

          - id: PLAYER-SERVER
            uri: lb://PLAYER-SERVER
            predicates:
              - Path=/player/**

  ```
    - 각각의 uri에, NBATEAM-SERVER 혹은 PLAYER-SERVER 대신 실행하고있는 서버와 포트를 합쳐서 적어주어도 된다
    - 하지만 `Eureka` 서버에 등록을 해 놓았기 때문에 해당하는 서버의 `application name`으로 접근이 가능하다

### 실행

- API Gateway는 Reuqest, Response를 한곳에서 모아서 통신을 해 주는 역할을 하기 때문에 관련된 서버들이 모두 정상으로 실행이 되고 있어야한다
- 실행순서
    1. Eureka 서버 실행
    2. Eureka Client 실행 (Player, Team, ...etc)
    3. API Gateway 실행
- 실행 확인
    - Eureka local 실행서버 (localhost:8761) 으로 접속
    - Eureka Client(Player, Team, ...) 등록 확인
    - API GateWay 등록 확인
    - API Gateway가 8000 포트로 열려있기 때문에, 8000포트로 서비스 요청
- 기존에 작성했었던 각각의 컨트롤러 주소로 요청을 한다
    - Player Server uri : `/player/server-info`
    - Team Server uri : `/team/server-info`
    - 각각의 서버 포트를 몰라도, API Gateway가 8000 포트로 모아주고 있기 때문에 `localhost:8000`이 BASE_URL이 된다

## Gateway 다루기 - Router 설정하기

- 정리한다고 정리했지만, 더 많은 글이나 원문이
  좋다면 [원문 Docs]("https://cloud.spring.io/spring-cloud-gateway/reference/html/#configuring-route-predicate-factories-and-gateway-filter-factories")
  를 참고하는 것을 추천한다
- Gateway server 로 들어오는 HTTP 요청들을 Gateway 내에서 다양한 조작을 할 수 있다
- Gateway로 요청이 들어오면 `Handler Mapping` 이 동작해 조건과 상황에 맞게 라우팅한다
- 이번 섹션에서는 Router에 대한 조건과 설정을 정리 해 보려고 한다

### Gateway에서 Cookie 추가하기

- Gateway로 들어오는 HTTP 요청 헤더에 cookie값을 보고 조건을 만족하는지 검토한다
- `- Cookie=cookie_name, regexp` 의 형태로 간단하게도 사용 가능하다
- 이전에 만들었었던 Gateway의 `application.yml`파일을 수정하려고한다
  ```yaml
  spring:
    application:
      name: GATEWAY-SERVER
    cloud:
      gateway:
        routes:
          - id: NBATEAM-SERVER
            uri: lb://NBATEAM-SERVER
            predicates:
              - Path=/team/**

          - id: PLAYER-SERVER
            uri: lb://PLAYER-SERVER
            predicates:
              - Path=/player/**

  server:
    port: 8000

  eureka:
    client:
      fetch-registry: true
      register-with-eureka: true
      service-url:
        defaultZone: http://localhost:8761/eureka
  ```
    - 위의 `yaml`파일에 `cloud`는 Gateway로 들어오는 서비스들을 등록 해 놓음
    - uri에 있는 lb는 Eureka 서버에 등록 되어 있는 Service들의 Application name -> Application name으로 로드밸런싱을 할 수 있음
    - predicates 에는 들어오는 Request들을 가지고 조건을 파악하게된다. 여기에 Cookie 조건을 걸어보자
    - 위의 `application.yml` 파일의 predicates를 아래와 같이 수정한다
      ```yaml
      # ... 생략
      predicates:
        - Path=/team/**
        - name: Cookie
          args:
            name: customcookie
            regexp: hello-world
      # ... 생략
      ```
    - 위와같이 세팅 후, postman 에서 request를 요청하면 아래와같이 받아 볼 수 있다. (요청 컨트롤러 작성은 이전 글에서 확인)
        - 만약 헤더에 `Cookie customcookie=testcookie` 가 없다면 request error가 날 것
          <img src="https://user-images.githubusercontent.com/25498314/168535951-63fe5efe-673b-4075-893e-02e8c40fdf5b.png">

### Gateway에서 요청 메서드 제한하기

- 각 서버가 "GET", "POST", "PUT", "DELETE" 등 HTTP Requests 메서드 중 한가지만 사용 한다고 가정
- 각 서버에서 특정 요청 만 받도록 설정 가능
    - [Docs]("https://cloud.spring.io/spring-cloud-gateway/reference/html/#the-method-route-predicate-factory")
- 마찬가지로 `application.yml`을 아래와 같이 작업해서 요청 해 보려고 한다. (요청 컨트롤러 작성은 이전 글에서 확인)

  ```java
  @PostMapping("/server-info")
  public String postPlayer() {
    return "SERVER: [" + name + "] Team Server is running on port " + port;
  }
  ```
    - post 컨트롤러 업데이트

  ```yaml
  predicates:
    - Path=/team/**
    - Method=GET
  ```
    - Method를 통해 "GET" 메시지만 수신하도록 작업

- GET 이외의 요청을 하게되면 테스트 결과가 아래와 같이 404 Not Found로 나오게 된다
  <img src="https://user-images.githubusercontent.com/25498314/168538899-f5315b1c-6f3d-4a30-b99c-8f12d608e3f8.png">

## Gateway Filter를 통한 Verification

작성한 서버는 크게 두가지로 만들었다

- Team을 관리하는 서비스
- Player를 관리하는 서비스

위의 Team에서는 Player의 여러 정보들을 관리해야하기 때문에 관리자들이 "인증"을 통해서 접근해야 한다.
인증절차를 가지고있는 `Filter` 클래스를 생성 해서 원하는 서비스에 인증을 붙여보도록 하자

### Gateway서버에 Filter Configuration Class 작성하기

- Auth관련된 Filter를 만들어주기 위해서 Gateway서버에 `config` 패키지를 만들고 그 안에 `TeamAuthFilter`를 작성한다
- 만들어줄 `TeamAuthFilter` 클래스는 `AbstractGatewayFilterFactory`를 상속받은 클래스이다
  ```java
  @Component
  public class TeamAuthFilter extends AbstractGatewayFilterFactory<TeamAuthFilter.Config> {

      public TeamAuthFilter() {
          super(Config.class);
      }

      @Override
      public GatewayFilter apply(Config config) {
          return ((exchange, chain) -> {
              ServerHttpRequest reactiveRequest = exchange.getRequest();
              ServerHttpResponse reactiveResponse = exchange.getResponse();

              if (!reactiveRequest.getHeaders().containsKey("token")) {
                  return handleUnAutorized(exchange);
              }

              List<String> token = reactiveRequest.getHeaders().get("token");
              String myToken = Objects.requireNonNull(token).get(0);

              // 임시
              String tmpAuthToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6Indvb2wiLCJpYXQiOjE1MTYyMzkwMjJ9.U7HCQF6Yx6DM29I-w-0uzl6dJTKnJoF_XTao8jYxL4A";


              if (!myToken.equals(tmpAuthToken)) {
                  return handleUnAutorized(exchange);
              }

              return chain.filter(exchange);

          });
      }

      private Mono<Void> handleUnAutorized(ServerWebExchange exchange) {
          ServerHttpResponse reactResponse = exchange.getResponse();

          reactResponse.setStatusCode(HttpStatus.UNAUTHORIZED);
          return reactResponse.setComplete();
      }

      public static class Config {
      }
  }
  ```
    - 기억하고 넘어가야 할 것은 `ServerHttpRequest`와 `ServerHttpResponse`는 `Spring react`패키지에 있는 객체이다
    - 임시 테스트를 위한 토큰은 [jwt.io]("https://jwt.io/")에서 발급하여 적어넣어주었다
    - Token 발급 과정 및 자세한 인증과정은 생략했는데 앞/뒤로 해당하는 로직이 추가되면 실제로 인증로직과 비슷하게 갈 수 있다

- 작성 한 `TeamAuthFilter` 클래스를 이제 `Gateway`에 등록 해 주어야 한다
- 작성 한 클래스는 Route 시에 filter 역할을 할 것 이기 때문데 `application.yml`에 원하는 service 내부에 filter로 추가 해 준다
  ```yaml
  #... 생략
  cloud:
    gateway:
      routes:
        - id: NBATEAM-SERVER
          uri: lb://NBATEAM-SERVER
          predicates:
            - Path=/team/**
            - Method=GET
          filters:
            - TeamAuthFilter
  #... 생략
  ```
    - 앞서 말 했듯, Team서버에 접근 할 수 있는 사람은 관리자만 접속 할 수 있도록 세팅 하려고 했기 때문에 Team 서버에 Auth Filter를 걸어주었다

- 이제 해당하는 컨트롤러로 요청을 보내서 확인 해보면
    - `header`에 `token`이 존재하지 않으면 -> `401 Unauthorized` 가 return
    - `header`에 `token`이 존재하면 -> 발급받은 token과 일치하는지 확인 후 api 라우팅

## Spring Cloud Config 서버

- Spring Cloud MSA 구조에서는 여러 서비스들을 묶어 Gateway 서버와 Eureka 서버에 물려놓는다
- 지금까지 만들어 본 MSA구조에서는 각각의 서버가 모두 다 `application.properties` 혹은 `application.yml` 을 가지고 있는 형태여서 각각의 서버가 자신의 프로퍼티를 관리했다
- 모든 설정들을 하나의 서버에서 관리하고, 관련된 환경변수 및 정보들을 가져가서 관리하는 Config 서버를 작성 해 보자

### Spring Config 관리 방법

- 여러가지가 있지만, 대표적으로 github(gitlab), native file system이 있다
- Github로 관리하는 방법을 선택 해 보려고 한다

### Config서버 생성

- SpringBoot Starter를 사용해 Config 서버를 만들어준다
- Dependency에 `implementation 'org.springframework.cloud:spring-cloud-config-server'` 가 있는지 확인한다
- Config서버 설정을 해 준다
    - Spring Starter class 에 어노테이션 설정
      ```java
      package com.example.springcloudconfigserver;

      import org.springframework.boot.SpringApplication;
      import org.springframework.boot.autoconfigure.SpringBootApplication;
      import org.springframework.cloud.config.server.EnableConfigServer;

      @SpringBootApplication
      @EnableConfigServer
      public class SpringCloudConfigServerApplication {

      public static void main(String[] args) {

          SpringApplication.run(SpringCloudConfigServerApplication.class, args);

         }
      }

      ```
        - Springboot Application Starter Class 에 `@EnableConfigServer` 를 설정해서 Configure 서버로 명시한다
    - github 작성
        - 연동 할 정보를 담을 서버는 github서버이다
        - [github config repo](https://github.com/paullee714/springcloud-config) 처럼 yaml 파일을 담아둔다
    - properties 작성
        - 다른 어플리케이션이 아닌 `Config Server`를 위한 `properties`를 작성
      ```yaml
      server:
        port: 8888

      spring:
        application:
          name: config-service

      cloud:
        config:
          server:
            git:
              uri: https://github.com/paullee714/springcloud-config
              #username: username123
              #password: userpassword123
      ```
        - git과 연동할 것 이기 때문에 `cloud.config.server.git` 을 사용헀고, `uri`에 config 정보가
          담긴 [github repo](https://github.com/paullee714/springcloud-config)를 추가한다
        - 만약 repo가 `private`이라면 username, password 의 주석을 지워준다

### Config 서버 실행 및 테스트

- 위의 과정까지 완료되었다면, Config서버를 실행시켜서 `localhost:8888`이 실행이 되는지 확인하자
- 문제가없이 실행된다면 [github config repo](https://github.com/paullee714/springcloud-config)에 올려 둔 yaml 파일들의 서비스 이름을 잘 기억 해주자
    - 현재 `team-service`, `player-servcie` 사용
- 각 서비스 이름과 환경에 맞게 `requests` 전송
    - ex) 현재 `application.yml` 파일들을 환경별로 (dev, prod, test) 로 나누지 않았기 때문에 모두 같은 파일이 실행 될 것
        - service name : "team-service"
        - service_env : "test"
        - request url 설명 : `"config서버주소:포트/{서비스이름}/{서버환경}"`
        - request : "http://localhost:8888/team-service/test"
        - response
          ```json
          {
            "name": "player-service",
            "profiles": [
              "test"
            ],
            "label": null,
            "version": "595c3dd0b45a5ed31804d04605800e3c27a0b743",
            "state": null,
            "propertySources": [
              {
                "name": "https://github.com/paullee714/springcloud-config/player-service.yml",
                "source": {
                  "server.port": 54293,
                  "spring.application.name": "PLAYER-SERVER",
                  "eureka.instance.instance-id": "${spring.application.name}-${random.uuid}",
                  "eureka.client.register-with-eureka": true,
                  "eureka.client.fetch-registry": true,
                  "eureka.client.service-url.defaultZone": "http://localhost:8761/eureka",
                  "token.key": "my_custom_token",
                  "default.message": "player-service에서 사용될 properties"
                }
              },
              {
                "name": "https://github.com/paullee714/springcloud-config/application.yml",
                "source": {
                  "default.owner": "config-service's git folder",
                  "default.content": ":) 안녕하세요 각각의 마이크로서비스에서 사용될 데이터입니다. :)"
                }
              }
            ]
          }
          ```

### Service Server에 Dependency 추가

- Config서버와 연결 해 줄 서비스들에  `Dependency` 를 추가 해 주어야 한다
- 우리가 가지고있는 team, player 서버에 아래 dependency를 추가 해주자
    - 'org.springframework.cloud:spring-cloud-starter-config'
    - 'org.springframework.cloud:spring-cloud-starter-bootstrap'

### Service Server의 `application.yml` 내용 재작성

- 이제 Config server가 있기 때문에 각각 서버에 작성 해 놓은 application.yml은 필요가 없다
- 내부에 있는 내용들을 비워주고, Service서버에서 알아야 하는 Config server의 정보를 넣어주자
  ```yaml
  # player server
  spring:
    cloud:
      config:
        uri: http://localhost:8888
        name: player-service
        profile: test
  ```

  ```yaml
  # team server
  spring:
    cloud:
      config:
        uri: http://localhost:8888
        name: team-service
        profile: test
  ```
- 모든 세팅이 완료되었으니 `Eureka`, `Player`, `Team`, `Config(이미실행중)`, `Gateway`를 실행시키고 이전에 했던 request가 잘 오는지 확인 해주자
