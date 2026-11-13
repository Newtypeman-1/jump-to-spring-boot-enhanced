# jump-to-spring-boot-enhanced

[점프 투 스프링부트](https://wikidocs.net/book/7601)(sbb3)를 처음부터 다시 따라 만들면서,
jhs512 강의(14163 점프투스프링부트 개선, 14168 JPA·타임리프 개선)에서 배운 방식을 적용하는 복습 프로젝트입니다.

- 책 내용 구현(책 URL 유지) → RESTful URL 리팩터링

## 기술 스택

| 구분 | 책(sbb3) | 이 프로젝트 |
|---|---|---|
| Spring Boot | 3.2.1 | 4.1.2 |
| Java | 19 | 25 |
| 빌드 | Gradle(Groovy) | Gradle(Kotlin DSL) |
| 설정 파일 | application.properties | application.yaml |
| DB | H2 | H2 |

개선점 정리: [docs/jhs512-improvements.md](docs/jhs512-improvements.md)

## 작업내역

### 001-01 프로젝트 생성(책 1-04), Initializr 결과 커밋
- [링크](https://github.com/Newtypeman-1/jump-to-spring-boot-enhanced/commit/001-01)
- 작업: Initializr 결과 커밋, README 뼈대 추가
- 이유: 이후 변경을 비교할 출발점 고정
- 결과: `./gradlew test` 통과(contextLoads)

### 001-02 개선 계획 문서, jhs512 강의 개선점 정리
- [링크](https://github.com/Newtypeman-1/jump-to-spring-boot-enhanced/commit/001-02)
- 작업: 책과 jhs512 강의 차이, 적용 시점 정리
- 이유: 단계마다 무엇을 바꿀지 기준 마련
- 결과: docs/jhs512-improvements.md 추가

### 002-01 프로젝트 구조(책 2-01), dev/test 프로필 설정 분리
- [링크](https://github.com/Newtypeman-1/jump-to-spring-boot-enhanced/commit/002-01)
- 작업: application.yaml을 공통/dev/test로 분리
- 이유: 이후 DB·로그 설정을 환경별로 넣을 자리 마련
- 결과: 테스트 로그에서 test 프로필 활성 확인

### 002-02 간단한 웹 프로그램(책 2-02), HelloController
- [링크](https://github.com/Newtypeman-1/jump-to-spring-boot-enhanced/commit/002-02)
- 작업: GET /hello 컨트롤러와 MockMvc 테스트 추가
- 이유: URL 요청이 컨트롤러로 연결되는 흐름 확인
- 결과: 테스트 통과, 브라우저에서 Hello World 확인

### 003-01 H2 DB 연결(책 2-03), dev 파일 DB / test 메모리 DB
- [링크](https://github.com/Newtypeman-1/jump-to-spring-boot-enhanced/commit/003-01)
- 작업: dev는 H2 파일 DB(./db_dev)와 h2-console, test는 H2 메모리 DB 설정
- 이유: 테스트가 개발 데이터를 건드리지 않고 매번 빈 DB로 시작
- 결과: 테스트 통과(메모리 DB), db_dev.mv.db 생성, h2-console 접속 확인
