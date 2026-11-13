# jhs512 강의 개선점 정리

책(sbb3, [pahkey/sbb3](https://github.com/pahkey/sbb3))과 jhs512 강의 두 개가 어떻게 다른지,
그리고 이 프로젝트에서 그 개선점을 언제 적용할지 정리합니다.

- 14163 점프투스프링부트 개선 — 참고 저장소 `jhs512/p-14163-2`
- 14168 JPA, 타임리프 개선(Post/PostComment 게시판) — 참고 저장소 `jhs512/p-14168-2`

## 1. 프로젝트 설정

| 항목 | 책 | jhs512 | 이 프로젝트 |
|---|---|---|---|
| 버전 | Boot 3.2.1, Java 19, Groovy | Boot 4.1.2, Java 25, Kotlin DSL | jhs512와 같음(001-01) |
| 설정 파일 | `application.properties` 한 개 | `application.yaml` + `-dev`(H2 파일 DB) + `-test`(H2 메모리 DB), 기본 프로필 dev | 적용(002-01, DB 설정은 003-01) |
| SQL 로그 | `show_sql` | `format_sql`, `highlight_sql`, `use_sql_comments`, 바인딩 파라미터 TRACE, 트랜잭션 TRACE | 적용(004, 테이블 생성 SQL이 처음 나오는 엔티티 단원) |
| H2 | 기본 설정 | `;MODE=MySQL`, DB 파일은 `.gitignore`에 추가 | 적용(003-01) |
| 템플릿 캐시 | — | dev에서 `thymeleaf.cache: false`, `prefix: file:src/main/resources/templates/` | 적용 |

## 2. 패키지 구조

- 책: `com.mysite.sbb.{question,answer,user}`
- jhs512(14168): `<base>.domain.<도메인>.<엔티티>.{controller,entity,repository,service}` + `<base>.global.*`
- 이 프로젝트: `com.newtype.enhanced.domain.question.question...`처럼 jhs512 방식을 따름.
  템플릿 경로도 패키지와 맞춤(`templates/question/question/detail.html`).

## 3. 엔티티

| 항목 | 책 | jhs512 | 이 프로젝트 |
|---|---|---|---|
| 공통 필드 | 엔티티마다 `id`, `createDate` 선언 | `BaseEntity`(`@MappedSuperclass`, `@CreatedDate`/`@LastModifiedDate`, id 기반 `equals`/`hashCode`), `@EnableJpaAuditing` | 적용 |
| id 타입 | `Integer` | `int` | 적용 |
| setter | `@Getter @Setter` | `@Getter @NoArgsConstructor`, setter 없음 | 적용 |
| 변경 방법 | 서비스에서 setter 호출 | 도메인 메서드(`addAnswer`, `modify`, `deleteAnswer` 등) + 변경 감지 | 적용 |
| 연관관계 | 기본 설정 | `@ManyToOne(LAZY)`, `@OneToMany(cascade = {PERSIST, REMOVE}, orphanRemoval = true)`, 컬렉션은 `new ArrayList<>()`로 초기화 | 적용 |

## 4. 계층 구조와 트랜잭션

- **애그리거트 단위 저장소**: jhs512는 자식(Answer/PostComment)에 Repository를 만들지 않고 부모를 통해 다룹니다.
  이 프로젝트는 처음엔 그렇게 하고, 자식에 Repository·Service가 정말 필요해지는 시점(예: 답변 단독 수정·추천)에 이유를 남기고 추가합니다.
- **`@Transactional` 위치**: 책은 서비스, jhs512는 컨트롤러(조회는 `readOnly = true`)에 붙입니다.
  여기에 hikari `auto-commit: false`를 함께 씁니다. 이 프로젝트도 컨트롤러 방식을 쓰고, `auto-commit: false`는 컨트롤러에 `@Transactional`을 처음 붙이는 커밋에서 함께 적용합니다.
- **조회 반환**: 서비스는 `Optional`을 돌려주고, 없으면 컨트롤러에서 예외를 던집니다.

## 5. 초기 데이터

- 책: 테스트 코드로 데이터를 넣음
- jhs512: `@Configuration` + `ApplicationRunner`(`InitData`), `@Lazy` 자기 주입으로 `self.work1()`에 트랜잭션 적용, `count() > 0`이면 건너뜀
- 이 프로젝트: 적용

## 6. 폼과 검증

- 책: 클래스 폼(`QuestionForm`), `@Getter @Setter`
- jhs512: 컨트롤러 안의 중첩 `record` 폼(`WriteForm`, `ModifyForm`).
  오류 메시지는 `"01-title-제목을 입력해주세요."` 형식이라 정렬할 수 있고, `form_errors` 조각이 정렬·분리한 뒤 해당 필드에 포커스를 줍니다.
- 이 프로젝트: 적용

## 7. 테스트

- 책: `@SpringBootTest`만 사용
- jhs512: `@ActiveProfiles("test") @SpringBootTest @Transactional`, 메서드 `t1`, `t2`... + `@DisplayName`.
  `@Transactional`이 없으면 LAZY 로딩 테스트가 실패하는 것도 보여줌
- 이 프로젝트: 적용하고, jhs512에는 없는 **MockMvc 컨트롤러 테스트**도 추가

## 8. URL

- 책: `/question/list`, `/question/detail/{id}`, `/answer/create/{id}`...
- jhs512(14168): `/posts`, `/posts/{id}`, `PUT /posts/{id}/modify`, `DELETE /posts/{postId}/comments/{id}/delete`(hidden method 필터)
- 이 프로젝트: 1단계는 **책 URL을 그대로** 쓰고, 1단계가 끝나면 RESTful 리팩터링을 별도 단위로 진행

## 9. 화면

- jhs512: Bootstrap을 cdnjs로 불러옴, Pretendard 폰트, `layout:decorate` + `title-pattern`,
  CSRF 메타 태그 + `global.js`(`<a method="DELETE">` 지원), 정적 파일은 `static/resource/` 아래
- 이 프로젝트: 화면 단원에서 적용

## 두 강의 모두 다루지 않은 것

`Rq`, `RsData`, `ServiceException`, `GlobalExceptionHandler`, QueryDSL, DTO 계층, MockMvc 테스트.
필요해지는 시점에 따로 결정합니다.
