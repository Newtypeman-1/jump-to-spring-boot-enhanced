# jhs512 강의 개선점 정리

책(sbb3, [pahkey/sbb3](https://github.com/pahkey/sbb3))과 jhs512 강의 두 개가 어떻게 다른지,
그리고 이 프로젝트에서 그 개선점을 어느 커밋에서 적용했는지 정리합니다.
책 본편(2장~3장 3-13)까지 구현했고, RESTful URL 리팩터링은 아직 하지 않았습니다.

- 14163 점프투스프링부트 개선 — 참고 저장소 `jhs512/p-14163-2`
- 14168 JPA, 타임리프 개선(Post/PostComment 게시판) — 참고 저장소 `jhs512/p-14168-2`

## 1. 프로젝트 설정

| 항목 | 책 | jhs512 | 이 프로젝트 |
|---|---|---|---|
| 버전 | Boot 3.2.1, Java 21, Groovy | Boot 4.1.2, Java 25, Kotlin DSL | jhs512와 같음(001-01) |
| 설정 파일 | `application.properties` 한 개 | `application.yaml` + `-dev`(H2 파일 DB) + `-test`(H2 메모리 DB), 기본 프로필 dev | 적용(002-01, DB 설정은 003-01) |
| SQL 로그 | `show_sql` | `format_sql`, `highlight_sql`, `use_sql_comments`, 바인딩 파라미터 TRACE, 트랜잭션 TRACE | 적용(004-03) |
| H2 | 기본 설정 | `;MODE=MySQL`, DB 파일은 `.gitignore`에 추가 | 적용(003-01) |
| 템플릿 캐시 | — | dev에서 `thymeleaf.cache: false`, `prefix: file:src/main/resources/templates/` | 적용(002-01, prefix는 006-01) |

## 2. 패키지 구조

- 책: `com.mysite.sbb.{question,answer,user}`
- jhs512(14168): `<base>.domain.<도메인>.<엔티티>.{controller,entity,repository,service}` + `<base>.global.*`
- 이 프로젝트: `com.newtype.enhanced.domain.question.question...`처럼 jhs512 방식을 따름.
  템플릿 경로도 패키지와 맞춤(`templates/question/question/detail.html`).

## 3. 엔티티

| 항목 | 책 | jhs512 | 이 프로젝트 |
|---|---|---|---|
| 공통 필드 | 엔티티마다 `id`, `createDate` 선언 | `BaseEntity`(`@MappedSuperclass`, `@CreatedDate`/`@LastModifiedDate`, id 기반 `equals`/`hashCode`), `@EnableJpaAuditing` | 적용(004-01), 수정 여부 `isModified()`(024-05) |
| id 타입 | `Integer` | `int` | 적용(004-01) |
| setter | `@Getter @Setter` | `@Getter @NoArgsConstructor`, setter 없음 | 적용(004-01부터 전체) |
| 변경 방법 | 서비스에서 setter 호출 | 도메인 메서드(`addAnswer`, `modify`, `deleteAnswer` 등) + 변경 감지 | 적용(005-03 modify, 005-04 addAnswer, 024-04 deleteAnswer, 025-01 vote) |
| 연관관계 | 기본 설정 | `@ManyToOne(LAZY)`, `@OneToMany(cascade = {PERSIST, REMOVE}, orphanRemoval = true)`, 컬렉션은 `new ArrayList<>()`로 초기화 | 적용(004-02) |

## 4. 계층 구조와 트랜잭션

- **애그리거트 단위 저장소**: jhs512는 자식(Answer/PostComment)에 Repository를 만들지 않고 부모를 통해 다룹니다.
  이 프로젝트도 답변 생성·삭제는 Question을 통해 하고(005-04, 024-04),
  답변 id만으로 찾아야 하는 답변 수정 URL이 생긴 024-03에서 AnswerRepository·AnswerService(조회 전용)를 추가했습니다.
- **`@Transactional` 위치**: 책 본편 코드에는 없고(2-05 테스트에만 잠깐 나옴), jhs512는 컨트롤러(조회는 `readOnly = true`)에 붙입니다.
  여기에 hikari `auto-commit: false`를 함께 씁니다. 이 프로젝트도 컨트롤러 방식을 씁니다(008-02에서 `auto-commit: false`와 함께 적용).
- **조회 반환**: 서비스는 `Optional`을 돌려주고, 없으면 컨트롤러에서 예외를 던집니다(009-01).

## 5. 초기 데이터

- 책: 테스트 코드로 데이터를 넣음
- jhs512: `@Configuration` + `ApplicationRunner`(`InitData`), `@Lazy` 자기 주입으로 `self.work1()`에 트랜잭션 적용, `count() > 0`이면 건너뜀
- 이 프로젝트: `DevInitData`(dev 프로필)로 적용. 샘플 질문(006-02), 샘플 300개(017-01), 개발용 계정(022-01)

## 6. 폼과 검증

- 책: 클래스 폼(`QuestionForm`), `@Getter @Setter`
- jhs512: 컨트롤러 안의 중첩 `record` 폼(`WriteForm`, `ModifyForm`).
  오류 메시지는 `"01-title-제목을 입력해주세요."` 형식이라 정렬할 수 있고, `form_errors` 조각이 정렬·분리한 뒤 해당 필드에 포커스를 줍니다.
- 이 프로젝트: 적용(015-02 질문, 015-03 답변, 021-01 회원가입, 024 수정 폼)

## 7. 테스트

- 책: `@SpringBootTest`만 사용
- jhs512: `@ActiveProfiles("test") @SpringBootTest @Transactional`, 메서드 `t1`, `t2`... + `@DisplayName`.
  `@Transactional`이 없으면 LAZY 로딩 테스트가 실패하는 것도 보여줌
- 이 프로젝트: 적용(005-01부터). 강의에 없는 MockMvc 컨트롤러 테스트를 002-02부터 넣었다가 029-01에서 제거

## 8. URL

- 책: `/question/list`, `/question/detail/{id}`, `/answer/create/{id}`...
- jhs512(14168): `/posts`, `/posts/{id}`, `PUT /posts/{id}/modify`, `DELETE /posts/{postId}/comments/{id}/delete`(hidden method 필터)
- 이 프로젝트: 1단계는 **책 URL을 그대로** 사용(완료). RESTful 리팩터링은 아직 하지 않음
  - 단, 상태를 바꾸는 요청(로그아웃, 삭제, 추천)은 책의 GET 링크 대신 처음부터 CSRF 토큰을 포함한 POST로 만듦

## 9. 화면

- jhs512: Bootstrap을 cdnjs로 불러옴, Pretendard 폰트, `layout:decorate` + `title-pattern`,
  CSRF 메타 태그 + `global.js`(`<a method="DELETE">` 지원), 정적 파일은 `static/resource/` 아래
- 이 프로젝트: Bootstrap·Pretendard CDN(013-01), `global/layout` + `title-pattern`(014-01), 정적 파일 `static/resource/global/`(012-01)
  - `global.js`(`<a method="DELETE">`)는 쓰지 않고 POST 폼으로 처리

## 두 강의 모두 다루지 않은 것

`Rq`, `RsData`, `ServiceException`, `GlobalExceptionHandler`, QueryDSL, DTO 계층, MockMvc 테스트.
모두 쓰지 않습니다(MockMvc 테스트는 넣었다가 029-01에서 제거).

## 강의 근거 없이 바꿨다가 책대로 되돌린 것 (029)

jhs512 강의에 없는 변경을 묻지 않고 넣었던 것들입니다. 029 단위에서 책 동작으로 되돌립니다.
Boot 4·Security 7 때문에 책 코드가 그대로는 동작하지 않는 부분만 바꾼 채로 둡니다.

| 넣었던 커밋 | 강의 밖 변경 | 책 동작 | 되돌린 커밋 |
|---|---|---|---|
| 002-02~ | MockMvc 컨트롤러 테스트, MarkdownRendererTest | 컨트롤러 테스트 없음 | 029-01 |
| 017-01, 017-02 | id 내림차순, 페이지 번호 1부터 표시 | `createDate` 내림차순, 0부터 표시 | 예정 |
| 019-01 | `default_batch_fetch_size: 100` | 질문마다 답변 SELECT | 예정 |
| 020-01 | `frameOptions().sameOrigin()`, `@EnableWebSecurity` 생략 | `XFrameOptionsHeaderWriter`, `@EnableWebSecurity` | 예정 |
| 021-01 | 저장 전 `existsBy` 검사·필드별 오류, `DelegatingPasswordEncoder`, 비밀번호 확인은 다른 오류 없을 때만 | `DataIntegrityViolationException` 처리, `BCryptPasswordEncoder` | 예정 |
| 022-01 | POST 로그아웃, `User.withUsername` 빌더, `AuthenticationManager` 미등록, 내비게이션 바 사용자ID | GET 로그아웃 링크, `new User(..., authorities)`, `AuthenticationManager` 빈 | 예정 |
| 023-02 | 비로그인 답변창 안내 문구 | 비활성 입력창만 | 예정 |
| 024 | 권한 없음 403, `isAuthoredBy` 메서드 | 400(`BAD_REQUEST`), 템플릿·컨트롤러에서 username 비교 | 예정 |
| 025-01 | POST 추천 | GET 추천 링크(JS confirm) | 예정 |
| 027-01 | `escapeHtml(true)`, `sanitizeUrls(true)` | 기본 `HtmlRenderer` | 예정 |
| 028 | 검색어 없으면 `findAll`, GET 폼·URL 파라미터 페이징 | 항상 검색 쿼리, 숨김 폼 + JS 페이징 | 예정 |

## 진행하면서 알게 된 것

- **Boot 4 / Hibernate 7**
  - dialect를 지정하지 않아도 H2Dialect를 자동 선택(003-01)
  - H2 2.x에서 `columnDefinition = "TEXT"`는 `VARCHAR(1000000000)`으로 생성됨(004-01)
- **Spring Security 7**
  - `AntPathRequestMatcher`가 없어져 `anyRequest()`와 경로 문자열 사용(020-01)
  - test 프로필은 H2 콘솔이 꺼져 있어 `PathRequest.toH2Console()`이 빈을 못 찾음 → `"/h2-console/**"`(020-01)
  - 로그인 화면 리다이렉트가 상대 경로 `/user/login`(023-01)
- **Thymeleaf**
  - 템플릿에서 `${@빈.메서드()}` 호출이 "Instantiation of new objects … is forbidden"으로 막혀 `@ControllerAdvice` 모델 속성으로 전달(027-01)
  - record 폼도 `th:object`/`th:field`로 바인딩됨(015-02)
  - 오류 프래그먼트는 입력 필드보다 먼저 렌더링되므로 포커스 스크립트는 `DOMContentLoaded` 뒤에 실행(015-02)
- **JPA**
  - 답변 목록이 화면에서 LAZY 조회되는 건 컨트롤러 트랜잭션이 아니라 기본으로 켜진 `open-in-view` 덕분(011-01)
  - cascade로 저장되는 답변은 커밋 시점에 INSERT되므로, 앵커용 id가 필요하면 flush(026-01)
  - Auditing은 저장 시 `createDate`와 `modifyDate`를 같은 값으로 채움 → 수정 여부는 두 값 비교(024-05)

## 남은 정리거리

- `spring.jpa.open-in-view` 끄기(지금은 기본값 켜짐, 시작 시 경고)
- `createDate`에 `@Column(updatable = false)`(수정 시 UPDATE 문에 작성일이 포함됨)
- RESTful URL 리팩터링
