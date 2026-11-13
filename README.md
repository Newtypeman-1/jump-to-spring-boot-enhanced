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

### 006-02 질문 목록(책 2-07), dev 샘플 데이터 DevInitData
- [링크](https://github.com/Newtypeman-1/jump-to-spring-boot-enhanced/commit/006-02)
- 작업: dev 프로필에서 앱 시작 시 질문 2개와 답변을 넣는 DevInitData 추가
- 이유: 목록 화면을 확인할 데이터를 테스트 코드 없이 자동으로 준비
- 결과: 브라우저 /question/list에 샘플 질문 2개 표시, ANSWER 테이블에 답변 2개 확인

### 007-01 루트 URL(책 2-08), / → 질문 목록 리다이렉트
- [링크](https://github.com/Newtypeman-1/jump-to-spring-boot-enhanced/commit/007-01)
- 작업: GET / 요청을 /question/list로 리다이렉트하는 MainController 추가
- 이유: 사이트 첫 화면을 질문 목록으로
- 결과: 테스트 통과, 브라우저에서 / 접속 시 /question/list로 이동 확인

### 008-01 서비스(책 2-09), QuestionService 도입
- [링크](https://github.com/Newtypeman-1/jump-to-spring-boot-enhanced/commit/008-01)
- 작업: QuestionService를 추가하고 컨트롤러가 리포지터리 대신 서비스를 사용
- 이유: 컨트롤러와 데이터 접근 사이에 비즈니스 로직 계층을 둠
- 결과: 기존 테스트(목록 MockMvc 포함) 모두 통과

### 008-02 서비스(책 2-09), 컨트롤러 @Transactional과 auto-commit false
- [링크](https://github.com/Newtypeman-1/jump-to-spring-boot-enhanced/commit/008-02)
- 작업: 질문 목록 컨트롤러에 읽기 전용 트랜잭션, hikari auto-commit false 설정
- 이유: 요청 하나를 트랜잭션 하나로 묶어 화면 렌더링까지 같은 영속성 컨텍스트 사용
- 결과: 테스트 통과, 로그에서 QuestionController.list 트랜잭션 시작·종료 확인

### 009-01 질문 상세(책 2-10), 상세 페이지와 404 처리
- [링크](https://github.com/Newtypeman-1/jump-to-spring-boot-enhanced/commit/009-01)
- 작업: 질문 상세 URL·템플릿, 목록의 제목 링크, 없는 질문의 404 처리 추가
- 이유: 목록에서 질문을 눌러 내용을 볼 수 있게
- 결과: 테스트 통과, 브라우저 /question/detail/1에서 제목·내용 확인

### 010-01 URL 프리픽스(책 2-11), @RequestMapping("/question")
- [링크](https://github.com/Newtypeman-1/jump-to-spring-boot-enhanced/commit/010-01)
- 작업: QuestionController에 공통 URL 프리픽스 /question 적용
- 이유: 메서드마다 반복되는 /question을 클래스에서 한 번만 선언
- 결과: URL은 그대로이고 기존 MockMvc 테스트 모두 통과

### 011-01 답변 기능(책 2-12), 상세 화면에 답변 목록 표시
- [링크](https://github.com/Newtypeman-1/jump-to-spring-boot-enhanced/commit/011-01)
- 작업: 질문 상세 화면에 답변 개수와 답변 목록 출력
- 이유: 질문에 달린 답변을 상세 화면에서 확인
- 결과: 테스트 통과, 상세 화면에 "N개의 답변이 있습니다."와 답변 내용 표시

### 011-02 답변 기능(책 2-12), 답변 등록 폼과 AnswerController
- [링크](https://github.com/Newtypeman-1/jump-to-spring-boot-enhanced/commit/011-02)
- 작업: 상세 화면의 답변 등록 폼과 POST /answer/create/{id} 처리 추가
- 이유: 사용자가 질문에 답변을 달 수 있게
- 결과: 테스트 통과, 브라우저에서 답변 등록 후 상세 화면에 표시 확인

### 012-01 웹 페이지 디자인(책 2-13), 정적 파일 style.css
- [링크](https://github.com/Newtypeman-1/jump-to-spring-boot-enhanced/commit/012-01)
- 작업: 정적 CSS 파일을 추가하고 질문 상세 화면에 적용
- 이유: 답변 입력창을 화면 너비에 맞추고 버튼 간격 조정
- 결과: 브라우저에서 style.css 200 응답, textarea 전체 너비·버튼 margin 10px 적용 확인

### 013-01 부트스트랩(책 2-14), 목록·상세 화면 꾸미기
- [링크](https://github.com/Newtypeman-1/jump-to-spring-boot-enhanced/commit/013-01)
- 작업: Bootstrap 5와 Pretendard 폰트를 CDN으로 불러와 목록·상세 화면 디자인
- 이유: 표·카드·버튼 등 기본 UI를 직접 CSS 작성 없이 구성
- 결과: 브라우저에서 목록 표(번호·제목·작성일시)와 상세 카드·답변 폼 확인
