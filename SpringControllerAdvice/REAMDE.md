# SpringBoot Exception 처리하기

- SpringBoot를 사용해서 개발을 하다보면 Exception 처리를 해야하는 경우가 생긴다. 해당하는 경우에 어떻게 예외처리하면 좋은지, 어떤 방법들이 있는지 한번 정리해보고자 한다

## Exception 처리하기

- 컨트롤러에 Excpetion을 하나씩 붙여나가면서 작업을 해 보자

### 기본 Controller와 Return 객체 만들기

- 기본 컨트롤러를 하나 만들고, 컨트롤러의 Response에 담아 줄 객체를 만들어 보자
- 컨트롤러와 객체는 아래와 같이 만들어주었다

    ```java
    // 컨트롤러 클래스
    package com.example.springcontrolleradvice.controller;

    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;

    @RestController
    @RequestMapping("/orders")
    public class OrderController {

        @GetMapping("/{orderId}")
        public ResponseEntity<CommonResponse> getOrder(@PathVariable String orderId) {
            return ResponseEntity.ok(new CommonResponse("success", "order found", orderId));
        }
    }

    ```
    - `/orders/{orderId}`로 GET 요청을 보내면, `orderId`를 Response에 담아서 보내준다

    ```java
    // 컨트롤러의 Response 객체
    package com.example.springcontrolleradvice.controller;

    public record CommonResponse(
    String Status,
    String message,
    Object data
    ) {}
    ```

### Exception 붙이기

- 컨트롤러에서 orderId가 100이 아닌 경우에는 Exception을 발생시켜보자

    ```java
    package com.example.springcontrolleradvice.controller;


    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;

    @RestController
    @RequestMapping("/orders")
    public class OrderController {

        @GetMapping("/{orderId}")
        public ResponseEntity<CommonResponse> getOrder(@PathVariable String orderId) throws Exception {
            if(!orderId.equals("100")){
                throw new Exception("order not found");
            }
            return ResponseEntity.ok(new CommonResponse("success", "order found", orderId));
        }

    }

    ```
    - 위처럼 Exception을 적용시키면, orderID가 100이 아닌경우에는 Exception 이 로그에 나온다

### Custom Exception 만들기

- 발생하는 Exception을 상속받아 CustomException을 만들어보자

  ```java
  package com.example.springcontrolleradvice.controller.exception;

  public class CustomException extends RuntimeException {
      private String message;
      private String errorCode;
      private String hint;

      protected CustomException() {
      }

      public CustomException(String message, String errorCode, String hint) {
          this.message = message;
          this.errorCode = errorCode;
          this.hint = hint;
      }

      // getters and setters
  }

  ```

### 컨트롤러에 CustomException을 적용하자

```java
package com.example.springcontrolleradvice.controller;

import com.example.springcontrolleradvice.controller.exception.CustomException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
RequestMapping("/orders")

public class OrderController {

    @GetMapping("/{orderId}")
    public ResponseEntity<CommonResponse> getOrder(@PathVariable String orderId) {
        if (!orderId.equals("100")) {
            throw new CustomException("order not found", "ERROR400", "please check the order id");
        }
        return ResponseEntity.ok(new CommonResponse("success", "order found", orderId));
    }

}
```

- CustomException을 적용시키면, Exception이 발생했을 때, CustomException이 발생한다

### CustomException을 처리하는 Handler 만들기

- 위의 과정은 Exception 발생시 Exception만 발생한다. 우리가 원하는 CustomException 메시지는 return되지 않는다.
- Exception을 발생시킬 때, 리턴되는 메시지에 우리의 CustomException을 넣어주도록 하자
- Exception을 intercept 해 주는 곳을 만들어서 우리의 CustomException을 return하도록 하자
- `CustomExceptionInterceptor`를 작성하자

  ```java
  package com.example.springcontrolleradvice.controller.exception;


  import org.springframework.http.HttpStatus;
  import org.springframework.http.ResponseEntity;
  import org.springframework.web.bind.annotation.ControllerAdvice;
  import org.springframework.web.bind.annotation.ExceptionHandler;
  import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

  @ControllerAdvice
  public class CustomExceptionInterceptor extends ResponseEntityExceptionHandler {

      @ExceptionHandler(CustomException.class)
      public final ResponseEntity<Object> handleCustomException(CustomException ex) {
          CustomExceptionModel exceptionResponse = new CustomExceptionModel(ex.getMessage(), ex.getErrorCode(), ex.getHint());
          return new ResponseEntity(exceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
      }
  }
  ```
    - `@ControllerAdvice`를 사용해서 Exception을 intercept 해주는 곳을 만들어준다
    - `ResponseEntityExceptionHandler`를 상속받아 CustomeExceptionInterceptor가 Exception이 발생 할 때 가로 채 주도록 만들자
    - `CustomExceptionModel`은 `CustomException`과 동일한 구조를 가지고있는 메시지객체로 작성 해준다
      ```java
      package com.example.springcontrolleradvice.controller.exception;

      public record CustomExceptionModel(
              String message,
              String errorCode,
              String hint
      ) {
      }
      ```
      - 우리가 작성한 메시지로 exception을 return 해준다
