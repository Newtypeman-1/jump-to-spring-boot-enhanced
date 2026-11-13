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

### 004-01 Question 엔티티(책 2-04), BaseEntity와 JPA Auditing
- [링크](https://github.com/Newtypeman-1/jump-to-spring-boot-enhanced/commit/004-01)
- 작업: 공통 필드(id, 작성일, 수정일)를 담은 BaseEntity와 Question 엔티티 추가
- 이유: 작성일·수정일을 JPA Auditing으로 자동 기록, setter 없이 엔티티 관리
- 결과: dev DB에 QUESTION 테이블 생성 확인

### 004-02 Answer 엔티티(책 2-04), Question과 연관관계
- [링크](https://github.com/Newtypeman-1/jump-to-spring-boot-enhanced/commit/004-02)
- 작업: Answer 엔티티와 Question-Answer 양방향 연관관계 추가
- 이유: 질문 하나에 여러 답변을 다는 구조를 테이블로 매핑
- 결과: dev DB에 ANSWER 테이블과 QUESTION_ID 외래 키 생성 확인

### 004-03 SQL 로그 설정(책 2-04), 실행 SQL과 바인딩 값 출력
- [링크](https://github.com/Newtypeman-1/jump-to-spring-boot-enhanced/commit/004-03)
- 작업: Hibernate SQL을 보기 좋게 출력하고 바인딩 값·트랜잭션 로그 추가
- 이유: JPA가 실제로 어떤 SQL을 실행하는지 확인하며 학습
- 결과: 테스트 로그에 create table question/answer와 외래 키 SQL 출력 확인

### 005-01 리포지터리(책 2-05), QuestionRepository와 저장·조회 테스트
- [링크](https://github.com/Newtypeman-1/jump-to-spring-boot-enhanced/commit/005-01)
- 작업: QuestionRepository, Question 생성자, 저장·전체 조회·단건 조회 테스트 추가
- 이유: 리포지터리로 질문을 저장·조회하고 작성일 자동 기록을 확인
- 결과: 테스트 3개 통과, 저장 시 id·작성일·수정일 자동 기록 확인

### 005-02 리포지터리(책 2-05), 쿼리 메서드 findBySubject/And/Like
- [링크](https://github.com/Newtypeman-1/jump-to-spring-boot-enhanced/commit/005-02)
- 작업: 제목, 제목+내용, 제목 일부로 질문을 찾는 쿼리 메서드와 테스트 추가
- 이유: 메서드 이름만으로 JPA가 쿼리를 만드는 방식 익히기
- 결과: 테스트 6개 통과, 로그에서 where subject=? / like ? SQL 확인

### 005-03 리포지터리(책 2-05), 질문 수정·삭제
- [링크](https://github.com/Newtypeman-1/jump-to-spring-boot-enhanced/commit/005-03)
- 작업: Question.modify 도메인 메서드와 수정·삭제 테스트 추가
- 이유: setter 없이 변경 감지로 수정하고, 수정일 자동 갱신 확인
- 결과: 테스트 8개 통과, flush 시 UPDATE 실행과 수정일 갱신 확인

### 005-04 리포지터리(책 2-05), 답변 저장·조회와 LAZY 로딩
- [링크](https://github.com/Newtypeman-1/jump-to-spring-boot-enhanced/commit/005-04)
- 작업: Answer 생성자, Question.addAnswer, 답변 저장·조회·LAZY 로딩 테스트 추가
- 이유: AnswerRepository 없이 질문을 통해 답변을 다루는 구조 확인
- 결과: 테스트 11개 통과, cascade로 답변 INSERT와 지연 조회 SELECT 확인

### 006-01 질문 목록(책 2-07), QuestionController와 list 템플릿
- [링크](https://github.com/Newtypeman-1/jump-to-spring-boot-enhanced/commit/006-01)
- 작업: GET /question/list 컨트롤러, 타임리프 목록 템플릿, MockMvc 테스트 추가
- 이유: 저장된 질문을 화면에 표 형태로 보여주기
- 결과: 테스트 통과, 브라우저에서 /question/list 표 헤더 렌더링 확인
