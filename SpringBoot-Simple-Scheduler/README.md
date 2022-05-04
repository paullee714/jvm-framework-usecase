# SpringBoot에서 스케쥴링 사용하기

특정 시점이나 특정 시간 간격, 혹은 정해진 시간에 실행해야 하는 작업이 있을 수 있다.
이런 작업들을 "스케쥴링 된 작업" 혹은 "배치작업" 이라고 하는데 Spring에서 지원하는 방법은 크게 두 가지가 있다.
이번에는 스프링의 스케쥴링 어노테이션을 사용한 스케쥴 작업을 생성 해 보려고 한다.

## 스케쥴링이란?

스케줄링은 사람의 개입 없이 특정 시간 간격으로 작업을 실행할 수 있는 프로세스이다.
예를 들어 매일 오전 9시 30분에 보고서를 생성하는 작업이 있다고 가정하면, 여기에 우리가 말하고 있는 스케쥴링 작업을 적용하여 요구 사항을 적절하게 충족할 수 있다.

## SpringBoot에서 @EnableScheduling을 사용하기

1. 스프링 프로젝트 생성
    - 스프링부트에 기본적으로 내장되어있는 스케쥴링 어노테이션을 사용
    - 따로 Dependency를 추가하지 않아도 됨
2. SpringBoot를 실행시켜주는 어플리케이션 실행 클래스에 `@EnableScheduling` 어노테이션 적용
    - 스프링부트에게 스케쥴링 이벤트를 사용한다고 알려줌
3. 클래스를 정의하고 스프링이 해당 내역을 판단 할 수 있도록 `@Component` 어노테이션을 추가
4. 작업을 실행 & 모니터링

SpringBoot 기본 내장 된 스케쥴러에는 크게 3가지 기능이 존재한다

- FixedRate()
- FixedDelay()
- Cron()

위의 세가지 예를 다뤄보려고 한다
새로운 `scheduler` 패키지를 만들고 그 안에 스케쥴러 클래스들을 생성하려고 한다.
아래는 나의 프로젝트 구조이다

```shell
.
├── HELP.md
├── README.md
├── build.gradle
├── gradle
│   └── wrapper
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
├── gradlew
├── gradlew.bat
├── settings.gradle
└── src
    ├── main
    │   ├── java
    │   │   └── com
    │   │       └── example
    │   │           └── springbootsimplescheduler
    │   │               ├── SpringBootSimpleSchedulerApplication.java
    │   │               └── scheduler
    │   │                   ├── CronScheduler.java
    │   │                   ├── FixedDelayScheduler.java
    │   │                   └── FixedRateScheduler.java
    │   └── resources
    │       └── application.properties
    └── test
        └── java
            └── com
                └── example
                    └── springbootsimplescheduler
                        └── SpringBootSimpleSchedulerApplicationTests.java

```

## SpringBoot Scheduler - FixedDelay

```java
@Scheduled(initialDelay = 5000, fixedDelay = 9000) // (a)
@Scheduled(initialDelayString = "5000", fixedDelayString = "9000") // (b)
public void myMethod(){
        System.out.println("FixedDelayScheduler - "+new Date());
        }
```

- initialDelay : 메서드가 등록되자마자 수행하는 것이 아닌 초기 지연시간 설정
- fixedDelay : ms 단위로(1000ms = 1s), 이전 작업이 끝난 시점으로부터 실행되는 고정 값
- ~DelayString : 해당하는 옵션 값을 String(문자열)로 적용 할 수 있게 해 줌

## SpringBoot Scheduler - FixedRate

```java
@Scheduled(fixedRate = 1000)
//@Scheduled(fixedRateString = "1000")
public void myMethod(){
        System.out.println("FixedRateScheduler - "+new Date());
        }
```

- fixedRate : ms 단위로(1000ms = 1s) 이전 작업이 수행되기 시작한 시점으로 부터 고정 된 시간을 설정

## SpringBoot Scheduler - Cron

```java
@Scheduled(cron = "* * * * * *")
public void myMethod(){
        System.out.println("Hello cron Scheduler Three :"+new Date());
        }
```

- cron은 해당하는 작업이 초/분/시/일/달/주수 에 따라서 실행 할 수 있도록 해줌
- Linux의 크론과 동일
- `* * * * * *` 는 왼쪽부터 순서대로
    - second(0 ~ 59)
    - minute(0 ~ 59)
    - hour(0 ~ 23)
    - day of month(0-31)
    - month(0-12 or JAN-DEC)
    - day of week(0-7 or MON-SUN)
- cron 표현식 TIP
    - 쉼표(,)는 skip의 뜻
    - 별표(*)는 모든 값
    - 슬래시(/)는 기간

## SpringBoot Scheduler 실행하기

위의 3가지 작업이 모두 끝났다면, 스프링부트 앱을 실행시켜 Syslog가 잘 나오는지 확인하면 된다.
잘 나오지 않는다면, `@EnableScheduling` 어노테이션을 한번 더 확인 해 주거나 `@Component`를 잘 등록했는지 확인 해 주면 된다.
