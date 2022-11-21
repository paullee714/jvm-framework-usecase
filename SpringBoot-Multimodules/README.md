# SpringBoot 를 Multimodule로 구성하기
- SpringBoot를 Multimodule로 구성하려고 한다.
- 여러가지 기능과 서버들이 추가됨에 따라서, 각 서버 내의 Entity들을 수정 해 주어야 한다.
- 이 때, 모든 서버가 같은 Entity를 참조하고 있다면 수정을 해주어야 하는 서버가 많아진다.
- 도메인 엔티티를 따로 빼서 구성하려고 한다.

## 프로젝트 기술 구성
- SpringBoot
- Kotlin
- Gradle
- JPA
- [Confluent Kafka (free trial)](https://www.confluent.io/ko-kr/get-started/)
- Postgresql
- docker-compose

## 목차
1. [멀티모듈 프로젝트 세팅하기](./docs/1-MULTIMODULE-README.md)
2. [멀티모듈 프로젝트에서 Entity 공유하기](./docs/2-CORE-MODULE-README.md)
3. [API모듈에서 작업하기](./docs/3-API-MODULE-README.md)
4. [STREAM모듈 작업하기](./docs/4-STREAM-MODULE-README.md)
5. [전체 서비스구조 및 Future work](./docs/5-FULL-SERVICE-FUTUREWORK.md)
