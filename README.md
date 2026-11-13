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

### 001 프로젝트 시작

- 001-01 프로젝트 생성(책 1-04), Initializr 결과 커밋
- 001-02 개선 계획 문서, jhs512 강의 개선점 정리

### 002 스프링 부트 기본 구조

- 002-01 프로젝트 구조(책 2-01), dev/test 프로필 설정 분리
