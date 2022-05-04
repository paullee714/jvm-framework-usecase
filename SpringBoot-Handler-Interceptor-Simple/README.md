# SpringBoot 사용하여 Request요청을 로깅하기

Request 요청 로깅을 할 경우, 모든 REST API 컨트롤러에 로그를 남기는것도 하나의 방법이다. 하지만 모든 API 컨트롤러에 로깅을 작성하게 된다면 비효율적으로 작업이 될 수 있다.
Spring Interceptor 라는 것을 사용해서, 컨트롤러의 Handler로 도착하기 전에 가로채어 따로 작업을 해 주는 방법을 정리하려고 한다

## Interceptor란
`Interceptor`란, 단어에서 느낄 수 있듯이 “낚아채다” 라는 의미를 가지고있다. `Client` 이 `Server`로 요청을 보낼 때, `Request` 객체는 가장먼저 `DispatcherServelet`이라는 곳을 통과하여 `Controller`로 전달이 된다.

이 떄, `DispatcherServelet`과 `Controller`사이에 `Interceptor`를 두어 미리 `Request`객체를 가져올 수 있다.

### Interceptor를 사용하면 생기는 장점?
- 공통 코드 사용으로 중복된 코드를 제거함으로써 코드 재사용성을 증가시킨다
- 반복되는 작업을 일일히 하지 않아도 되어 코드 누락에 대한 위험성이 감소한다

## Handler Interceptor Method 종류
- preHandle()
    - `request`가 `Controller`에 진입하기 전에 동작하는 함수.
    - return 값이 true인 경우에만 `Controller`가 정상적으로 진행이 되고, false인 경우에는 실행이 종료된다.
- postHandle()
    - `request`가 `Controller`에 진입 한 후, `View`가 `Rendering`되기 전 수행
- afterCompletion()
    - `request`가 `Controller`에 진입 한 후, `View`가 정상적으로 실행 된 후에 수행
- afterConcurrentHandlingStarted()
    - 비동기요청시 사용하는 함수
    - postHandle(), afterCompletion() 메서드를 대체

## Handler Interceptor 사용하기
Handler Interceptor는 위에서 설명 한 것 처럼 이미 생성되어있는 인터페이스이다. 때문에 메서드 오버라이딩을 사용해서 `preHandle`을 구현하려고한다.

총 4단계로 구성을 하려고 한다
1. Handler Interceptor 구현체를 만들고 모든 요청을 기록
2. SpringBoot가 인식 할 수 있도록 구현체 등록
3. HttpServletRequest를 래핑하여 한번만 읽을 수 있는 요청을 여러번 읽어서 사용 할 수 있도록 래핑
4. HttpServlet 요청을 필터링 하는 서블릿 필터 생성

아래는 나의 프로젝트 구조이다
.
├── HELP.md
├── README.md
├── gradlew
├── gradlew.bat
├── settings.gradle
└── src
├── main
│   ├── java
│   │   └── com
│   │       └── example
│   │           └── springboothandlerinterceptorsimple
│   │               ├── SpringBootHandlerInterceptorSimpleApplication.java
│   │               ├── common
│   │               │   ├── MyLoggingInterceptor.java
│   │               │   ├── RequestServletFilter.java
│   │               │   └── RequestServletWrapper.java
│   │               ├── config
│   │               │   └── Configuration.java
│   │               └── controller
│   │                   └── MyTestController.java
│   └── resources
│       ├── application.properties
│       ├── static
│       └── templates
└── test
└── java
└── com
└── example
└── springboothandlerinterceptorsimple
└── SpringBootHandlerInterceptorSimpleApplicationTests.java



### Handler Interceptor 생성
package com.example.springboothandlerinterceptorsimple.common;


import java.util.Map;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class MyLoggingInterceptor implements HandlerInterceptor {

    Logger logger = LoggerFactory.getLogger(MyLoggingInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if (Objects.equals(request.getMethod(), "POST")) {
            Map<String, Object> inputMap = new ObjectMapper().readValue(request.getInputStream(), Map.class);

            logger.info("요청 정보: " + inputMap);
            logger.info("요청 URL: " + request.getRequestURL());

            return true;
        } else {

            logger.info("요청 정보: " + request.getQueryString());
            logger.info("요청 URL: " + request.getRequestURL());
            return true;
        }
    }
}


### Handler Interceptor 등록
Config 패키지를 만들어 아래와 같이 생성한다
package com.example.springboothandlerinterceptorsimple.config;

import com.example.springboothandlerinterceptorsimple.common.MyLoggingInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Component
public class Configuration implements WebMvcConfigurer {

    @Autowired
    private MyLoggingInterceptor myLoggingInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(myLoggingInterceptor);
    }

}


### Servlet Wrapper 생성
request는 spring에서 한번만 읽을 수 있다
이 request객체를 래핑하여 여러곳에서 읽을 수 있도록 처리 해 주자
package com.example.springboothandlerinterceptorsimple.common;

import java.io.IOException;
import java.io.StringReader;
import java.util.Scanner;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class RequestServletWrapper extends HttpServletRequestWrapper {

    private String requestData = null;

    public RequestServletWrapper(HttpServletRequest request) {

        super(request);

        try (Scanner s = new Scanner(request.getInputStream()).useDelimiter("\\A")) {

            requestData = s.hasNext() ? s.next() : "";

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public ServletInputStream getInputStream() throws IOException {

        StringReader reader = new StringReader(requestData);

        return new ServletInputStream() {

            private ReadListener readListener = null;

            @Override
            public int read() throws IOException {

                return reader.read();
            }

            @Override
            public void setReadListener(ReadListener listener) {
                this.readListener = listener;

                try {
                    if (!isFinished()) {

                        readListener.onDataAvailable();
                    } else {

                        readListener.onAllDataRead();
                    }
                } catch (IOException io) {

                    io.printStackTrace();
                }

            }

            @Override
            public boolean isReady() {

                return isFinished();
            }

            @Override
            public boolean isFinished() {

                try {
                    return reader.read() < 0;
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return false;

            }
        };
    }

}

- HttpServletRequest객체를 받아서 문자열로 추출하는 생성자를 만든다
    - `StringReader reader = new StringReader(requestData);`
- read(), setReadListener(), isFinished(), isReady()가 구현된 InputStream을 재정의

### Servlet Filter 생성
package com.example.springboothandlerinterceptorsimple.common;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

@Component
public class RequestServletFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest wrappedRequest = new RequestServletWrapper((HttpServletRequest) request);

        chain.doFilter(wrappedRequest, response);

    }

}
- 요청은 래퍼 개체를 사용하여 래핑되고 이 래퍼 개체는 필터 체인으로 전달 됨
- HttpServletRequest 객체를 읽을 때 구체적으로 언급하지 않더라도 래퍼 객체를 읽음(MyLoggingInterceptor 클래스)

### Controller 생성
api endpoint를 생성 해서 request를 받아 줄 수 있는 controller 클래스를 생성한다
package com.example.springboothandlerinterceptorsimple.controller;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class MyTestController {

    @GetMapping("/test-one")
    public Map<String, Object> firstAPI(@RequestParam Map<String, Object> request) {
        return request;
    }

    @PostMapping("/test-two")
    public Map<String, Object> secondAPI(@RequestBody Map<String, Object> request) {
        return request;
    }
}



## 요청 테스트하기
우리가 로그를 잘 남기고 있는지 확인하기 위해 서버를 실행 한 후 요청을 실행 해 보자
컨트롤러에 endpoint를 총 두개 생성했다
- `test-one` : GET
- `test-two` : POST

1.  GET - `localhost:8080/test-one?id=1&name=wool`
    - 결과
      INFO 14244 --- [nio-8080-exec-7] c.e.s.common.MyLoggingInterceptor        : 요청 정보: id=1&name=wool
      INFO 14244 --- [nio-8080-exec-7] c.e.s.common.MyLoggingInterceptor        : 요청 URL: http://localhost:8080/test-one

2. POST - `localhost:8080/test-two`
   body : `application/json'`
   {
   "id":1,
   "name":"wool",
   "phone":"01012341234",
   "mail":"hello@mail.com"
   }
    - 결과
      INFO 14244 --- [nio-8080-exec-9] c.e.s.common.MyLoggingInterceptor        : 요청 정보: {id=1, name=wool, phone=01012341234, mail=hello@mail.com}
      INFO 14244 --- [nio-8080-exec-9] c.e.s.common.MyLoggingInterceptor        : 요청 URL: http://localhost:8080/test-two
