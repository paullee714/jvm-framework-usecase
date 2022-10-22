# Spring Security 사용해보기

- springboot 의 인증과 인가를 담당하고있는 spring security를 사용하고 적용 해 보자
- 동작 원리와 Architecture를 이해하고 적용해보자

## Spring Security?

- Spring 기반의 Authentication, Authorization을 처리 해 주는 프레임워크
- 보안과 관련해서 많은 옵션을 제공 해 주기 때문에 편하다
- Spring Security 는 `Filter`에 따라서 인증과 인가를 거친다
- `Filter`는 Request가 서버로 들어 오는 경우에 `Request를 받아주는 Dispatcher Servlet` 앞에 위치한다
- `Interceptor`는 `Dispatcher Servlet` 뒤, `Controller`앞에 위치한다
    ```text
    [Request] -> [Dispatcher Servlet] -> [Controller]
              ㄴ> [Filter]             ㄴ> [Interceptor]
    ```

### Filter와 Interceptor


![filter_and_interceptor](https://user-images.githubusercontent.com/25498314/194521424-d1d87113-53f6-4f3d-9fef-5be94525fb50.png)

- Filter
    - 요청과 응답을 필터하는 역할을 한다. 말 그대로 거르거나 정제하는 곳
    - Dispatcher Servlet 앞에 위치한다
    - 사용처
        - 보안 및 인증, 인가
        - 모든 요청에 대한 로깅
        - 데이터 압축
        - 문자열 인코딩
    - filter의 메소드
        - `init()` : 필터를 초기화, 필터를 서비스에 추가
        - `doFilter()` : 필터링 로직을 수행
        - `destroy()` : 필터를 종료, 필터를 서비스에서 제거

- Interceptor
    - 인터셉터는 `Dispatcher Servlet`이 `Controller`로 요청을 보내기 전에 위치한다
    - `Interceptor`는 여러개가 등록되어있다면, 순차적으로 실행된다
    - 사용처
        - API 호출에 대한 로깅 및 검사
        - Controller로 가는 Param의 pre-processing
        - filter되어오는 요청에 대한 추가적인 세부작업
    - Interceptor의 메소드
        - `preHandle()` : 컨트롤러 실행 전에 실행
        - `postHandle()` : 컨트롤러 실행 후에 실행
        - `afterCompletion()` : 뷰 렌더링까지 완료된 후 실행
