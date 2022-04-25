# SpringBoot-Redis 로 만든 디저트가게 예제
- SpringBoot 와 Redis를 이용한 디저트가게 예제

## 목차
- [스택](#스택)
- [레디스서버 띄우기](#레디스서버-띄우기)
- [Configuration 작성하기](#configuration-작성하기)
- [CRUD API 작성하기](#crud-api-작성하기)
    - [Domain 모델 작성](#domain-모델-작성)
    - [Database에 접근하는 Repository 작성](#database에-접근하는-repository-작성)
    - [Service 작성](#service-작성)
    - [Contoller 생성](#contoller-생성)


## 스택
- SpringBoot
    - Lombok
    - Spring Reactive Web
    - Spring Data Reactive Redis
    - Validation
- Gradle
- Java 11 +
- Redis


## 레디스서버 띄우기
- 레디스 서버를 실행시켜야 함
- 로컬에서 임베드로 생성하거나, 로컬에 Docker로 생성하거나, 클라우드에 설치 된 redis 사용
- docker-compose를 사용해서 설치 함
    - [docker-compose.redis.yml](docker-compose.redis.yml)
        ```Docker
        version: "3"
        services:
            redis-docker:
                image: redis:latest
                command: redis-server --requirepass qwerqwer123 --port 6379
                container_name: "docker-redis"
                labels:
                - "name=redis"
                - "mode=standalone"
                volumes:
                - /Users/wool/Database-docker/data/redis:/data
                ports:
                - 6379:6379
        ```
    - redis에 비밀번호를 적용하지 않는다면 `--requirepass` 옵션을 빼고 작성
    - redis서버 실행
        ```bash
        $ docker-compose -f docker-compose.redis.yml up -d
        ```
## Configuration 작성하기
- [ReactiveRedisConfiguration.java](./src/main/java/com/example/springbootredisdessert/config/ReactiveRedisConfiguration.java)
- Redis와 Spring을 연결하기 위해 설정 파일을 작성
- `reactRedisConnectionFactory` 빈을 생성하여 레디스 연결 팩토리를 생성
    ```java
    @Bean
    public ReactiveRedisConnectionFactory reactiveRedisConnectionFactory() {
        return new LettuceConnectionFactory(
            Objects.requireNonNull(env.getProperty("spring.redis.host")),
            Integer.parseInt(Objects.requireNonNull((env.getProperty("spring.redis.port"))))
        );
    }
    ```
    혹은 `@Value`어노테이션을 사용해서 아래와 같이 데이터를 깔끔하게 정리 할 수 있음
    ```java
        @Value("${spring.redis.host}")
        private String redisHost;

        @Value("${spring.redis.port}")
        private int redisPort;

        @Bean
        public ReactiveRedisConnectionFactory reactiveRedisConnectionFactory() {
            return new LettuceConnectionFactory(redisHost, redisPort);
        }
    ```
- Redis의 데이터를 주고받아야 하기 때문에 JSON형식을 지원하는 Jackson 라이브러리를 사용한 Operation Bean을 세팅한다.
    ```java
    @Bean
    public ReactiveRedisOperations<String, Object> redisOperations(ReactiveRedisConnectionFactory reactiveRedisConnectionFactory) {
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(Object.class);

        RedisSerializationContext.RedisSerializationContextBuilder<String, Object> builder =
                RedisSerializationContext.newSerializationContext(new StringRedisSerializer());

        RedisSerializationContext<String, Object> context = builder.value(serializer).hashValue(serializer)
                .hashKey(serializer).build();

        return new ReactiveRedisTemplate<>(reactiveRedisConnectionFactory, context);
    }
    ```

## CRUD API 작성하기
- CRUD를 작성하기 위해 우선 모델 클래스가 필요
### Domain 모델 작성
- 도메인 객체 생성하기
    - pk, 제품명, 카테고리, 설명, 가격, 출시일 을 가진 [Dessert](./src/main/java/com/example/springbootredisdessert/domain/Dessert.java) 도메인객체 생성


### Database에 접근하는 Repository 작성
- Redis 데이터베이스와 연결 할 Repository를 작성한다
- Interface를 사용해서 CRUD 기능을 명시하고, 구현체를 사용해 각 내부 로직을 구현
- JSON 파싱을 위해서 [ObjectMapperUtils](./src/main/java/com/example/springbootredisdessert/config/ObjectMapperUtils.java) 클래스 생성
- Redis와 연결을 위해서 [ReactiveRedisComponent](./src/main/java/com/example/springbootredisdessert/repository/ReactiveRedisComponent.java) 클래스 생성
- [DessertRepository](./src/main/java/com/example/springbootredisdessert/repository/DessertRepository.java)
    ```java
    public interface DessertRepository {

        Mono<Dessert> save(Dessert dessert);

        Mono<Dessert> get(String key);

        Flux<Dessert> getAll();

        Mono<Long> delete(String id);
    }
    ```
- [RedisDessertRepository](./src/main/java/com/example/springbootredisdessert/repository/RedisDessertRepository.java)
    ```java
    public class RedisDessertRepository implements DessertRepository {

        private ReactiveRedisOperations<String, Object> redisOperations;

        public RedisDessertRepository(ReactiveRedisOperations<String, Object> redisOperations) {
            this.redisOperations = redisOperations;
        }

        @Override
        public Mono<Dessert> save(Dessert dessert) {
            return redisOperations.opsForValue().set(dessert.getId(), dessert);
        }

        @Override
        public Mono<Dessert> get(String key) {
            return redisOperations.opsForValue().get(key);
        }

        @Override
        public Flux<Dessert> getAll() {
            return redisOperations.keys("*")
                    .flatMap(redisOperations::opsForValue::get)
                    .cast(Dessert.class);
        }

        @Override
        public Mono<Long> delete(String id) {
            return redisOperations.delete(id);
        }
    }
    ```

### Service 작성
- Interface를 먼저 명시 해 준 후에 구현체로 구현
- DessertService
    - [DessertService](./src/main/java/com/example/springbootredisdessert/service/DessertService.java)
        ```java
        public interface DessertService {

            Mono<Dessert> create(Dessert dessert);

            Flux<Dessert> getAll();

            Mono<Dessert> getOne(String id);

            Mono<Long> deleteById(String id);

        }
        ```
    - [DessertServiceImpl](./src/main/java/com/example/springbootredisdessert/service/DessertServiceImpl.java)
        ```java
        @RequiredArgsConstructor
        @Service
        public class DessertServiceImpl implements DessertService {

            private final RedisDessertRepository redisDessertRepository;

            @Override
            public Mono<Dessert> create(Dessert dessert) {
                return redisDessertRepository.save(dessert);
            }

            @Override
            public Flux<Dessert> getAll() {
                return redisDessertRepository.getAll();
            }

            @Override
            public Mono<Dessert> getOne(String id) {
                return redisDessertRepository.get(id);
            }

            @Override
            public Mono<Long> deleteById(String id) {
                return redisDessertRepository.delete(id);
            }
        }
        ```

### Contoller 생성
- DessertController를 생성하여 API Call을 Response 할 수 있도록 작성
    ```java
    @RestController
    @RequestMapping("/v1")
    @RequiredArgsConstructor
    public class DessertController {

        private final DessertServiceImpl dessertService;

        @PostMapping("/dessert")
        @ResponseStatus(HttpStatus.CREATED)
        public Mono<Dessert> addDessert(@RequestBody @Valid Dessert dessert) {
            return dessertService.create(dessert);
        }

        @GetMapping("/dessert")
        public Flux<Dessert> getAllDessert() {
            return dessertService.getAll();
        }

        @GetMapping("/dessert/{id}")
        public Mono<Dessert> getDessert(@PathVariable String id) {
            return dessertService.getOne(id);
        }

        @DeleteMapping("/dessert/{id}")
        public Mono<Long> deleteDessert(@PathVariable String id) {
            return dessertService.deleteById(id);
        }

    }

    ```
