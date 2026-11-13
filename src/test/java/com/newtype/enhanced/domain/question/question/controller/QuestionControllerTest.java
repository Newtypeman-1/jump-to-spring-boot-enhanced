package com.newtype.enhanced.domain.question.question.controller;

import com.newtype.enhanced.domain.question.question.entity.Question;
import com.newtype.enhanced.domain.question.question.repository.QuestionRepository;
import com.newtype.enhanced.domain.user.user.entity.SiteUser;
import com.newtype.enhanced.domain.user.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class QuestionControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private UserService userService;

    private SiteUser author;

    @BeforeEach
    void setUp() {
        author = userService.create("user1", "1234", "user1@test.com");
    }

    @Test
    @DisplayName("GET /question/list: 질문 목록을 보여준다")
    void t1() throws Exception {
        questionRepository.save(new Question(author, "sbb가 무엇인가요?", "sbb에 대해서 알고 싶습니다."));

        mvc.perform(get("/question/list"))
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(QuestionController.class))
                .andExpect(handler().methodName("list"))
                .andExpect(view().name("question/question/list"))
                .andExpect(content().string(containsString("sbb가 무엇인가요?")));
    }

    @Test
    @DisplayName("GET /question/detail/{id}: 질문 상세를 보여준다")
    void t2() throws Exception {
        Question question = questionRepository.save(new Question(author, "sbb가 무엇인가요?", "sbb에 대해서 알고 싶습니다."));

        mvc.perform(get("/question/detail/" + question.getId()))
                .andExpect(status().isOk())
                .andExpect(handler().methodName("detail"))
                .andExpect(view().name("question/question/detail"))
                .andExpect(content().string(containsString("sbb에 대해서 알고 싶습니다.")));
    }

    @Test
    @DisplayName("GET /question/detail/{id}: 없는 질문이면 404")
    void t3() throws Exception {
        mvc.perform(get("/question/detail/999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /question/detail/{id}: 답변 개수와 답변 목록을 보여준다")
    void t4() throws Exception {
        Question question = questionRepository.save(new Question(author, "sbb가 무엇인가요?", "sbb에 대해서 알고 싶습니다."));
        question.addAnswer(author, "첫 번째 답변");
        question.addAnswer(author, "두 번째 답변");
        questionRepository.flush();

        mvc.perform(get("/question/detail/" + question.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("2개의 답변이 있습니다.")))
                .andExpect(content().string(containsString("두 번째 답변")));
    }

    @Test
    @DisplayName("GET /question/create: 질문 등록 폼을 보여준다")
    void t5() throws Exception {
        mvc.perform(get("/question/create").with(user("user1")))
                .andExpect(status().isOk())
                .andExpect(view().name("question/question/create"));
    }

    @Test
    @DisplayName("POST /question/create: 질문을 저장하고 목록으로 리다이렉트한다")
    void t6() throws Exception {
        mvc.perform(post("/question/create").with(csrf()).with(user("user1"))
                        .param("subject", "새 질문")
                        .param("content", "새 질문 내용"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/question/list"));

        Question question = questionRepository.findBySubject("새 질문").get();
        assertThat(question.getAuthor().getUsername()).isEqualTo("user1");
    }

    @Test
    @DisplayName("POST /question/create: 제목·내용이 비면 폼을 다시 보여주고 오류를 순서대로 출력한다")
    void t7() throws Exception {
        long count = questionRepository.count();

        mvc.perform(post("/question/create").with(csrf()).with(user("user1"))
                        .param("subject", "")
                        .param("content", " "))
                .andExpect(status().isOk())
                .andExpect(view().name("question/question/create"))
                .andExpect(model().attributeHasFieldErrors("form", "subject", "content"))
                .andExpect(content().string(containsString("제목은 필수항목입니다.")))
                .andExpect(content().string(containsString("내용은 필수항목입니다.")));

        assertThat(questionRepository.count()).isEqualTo(count);
    }

    @Test
    @DisplayName("공통 레이아웃: 모든 화면에 내비게이션 바(SBB 로고)가 나온다")
    void t8() throws Exception {
        mvc.perform(get("/question/list"))
                .andExpect(content().string(containsString("navbar-brand")));
    }

    @Test
    @DisplayName("GET /question/list?page=1: 최신순으로 10개씩 나눠 보여준다")
    void t9() throws Exception {
        for (int i = 1; i <= 15; i++) {
            questionRepository.save(new Question(author, "페이징 질문 %02d".formatted(i), "내용"));
        }

        mvc.perform(get("/question/list").param("page", "1"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("paging", hasProperty("number", is(1))))
                .andExpect(content().string(containsString("페이징 질문 05")))
                .andExpect(content().string(not(containsString("페이징 질문 06"))));
    }

    @Test
    @DisplayName("GET /question/list: 페이지 이동 링크를 보여준다")
    void t10() throws Exception {
        for (int i = 1; i <= 15; i++) {
            questionRepository.save(new Question(author, "페이징 질문 %02d".formatted(i), "내용"));
        }

        mvc.perform(get("/question/list"))
                .andExpect(content().string(containsString("pagination")))
                .andExpect(content().string(containsString("/question/list?page=1")));
    }

    @Test
    @DisplayName("GET /question/list: 번호는 전체 개수에서 거꾸로 매긴다")
    void t11() throws Exception {
        for (int i = 1; i <= 15; i++) {
            questionRepository.save(new Question(author, "페이징 질문 %02d".formatted(i), "내용"));
        }

        mvc.perform(get("/question/list").param("page", "1"))
                .andExpect(content().string(containsString("<td>5</td>")))
                .andExpect(content().string(not(containsString("<td>6</td>"))));
    }

    @Test
    @DisplayName("GET /question/list: 답변이 있으면 제목 옆에 답변 개수를 보여준다")
    void t12() throws Exception {
        Question question = questionRepository.save(new Question(author, "답변 개수 질문", "내용"));
        question.addAnswer(author, "답변 1");
        question.addAnswer(author, "답변 2");
        question.addAnswer(author, "답변 3");
        questionRepository.flush();

        mvc.perform(get("/question/list"))
                .andExpect(content().string(containsString("text-danger small ms-2\">3</span>")));
    }

    @Test
    @DisplayName("POST /question/create: CSRF 토큰이 없으면 403")
    void t13() throws Exception {
        mvc.perform(post("/question/create")
                        .param("subject", "제목")
                        .param("content", "내용"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /question/create: 폼에 CSRF 토큰 hidden 필드가 자동으로 들어간다")
    void t14() throws Exception {
        mvc.perform(get("/question/create").with(user("user1")))
                .andExpect(content().string(containsString("name=\"_csrf\"")));
    }

    @Test
    @DisplayName("GET /question/create: 로그인하지 않으면 로그인 화면으로 보낸다")
    void t15() throws Exception {
        mvc.perform(get("/question/create"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/login"));
    }

    @Test
    @DisplayName("목록·상세에 글쓴이를 보여주고, 비로그인 상세 화면은 답변 입력창을 막는다")
    void t16() throws Exception {
        Question question = questionRepository.save(new Question(author, "글쓴이 질문", "내용"));
        question.addAnswer(author, "글쓴이 답변");
        questionRepository.flush();

        mvc.perform(get("/question/list"))
                .andExpect(content().string(containsString("<span>user1</span>")));

        mvc.perform(get("/question/detail/" + question.getId()))
                .andExpect(content().string(containsString("<span>user1</span>")))
                .andExpect(content().string(containsString("로그인 후 답변을 작성할 수 있습니다.")));
    }

    @Test
    @DisplayName("GET /question/modify/{id}: 작성자에게 기존 값이 채워진 수정 폼을 보여준다")
    void t17() throws Exception {
        Question question = questionRepository.save(new Question(author, "수정 전 제목", "수정 전 내용"));

        mvc.perform(get("/question/modify/" + question.getId()).with(user("user1")))
                .andExpect(status().isOk())
                .andExpect(view().name("question/question/modify"))
                .andExpect(content().string(containsString("value=\"수정 전 제목\"")));
    }

    @Test
    @DisplayName("POST /question/modify/{id}: 작성자는 수정할 수 있다")
    void t18() throws Exception {
        Question question = questionRepository.save(new Question(author, "수정 전 제목", "수정 전 내용"));

        mvc.perform(post("/question/modify/" + question.getId()).with(csrf()).with(user("user1"))
                        .param("subject", "수정 후 제목")
                        .param("content", "수정 후 내용"))
                .andExpect(redirectedUrl("/question/detail/" + question.getId()));

        assertThat(question.getSubject()).isEqualTo("수정 후 제목");
    }

    @Test
    @DisplayName("POST /question/modify/{id}: 작성자가 아니면 403")
    void t19() throws Exception {
        Question question = questionRepository.save(new Question(author, "수정 전 제목", "수정 전 내용"));
        userService.create("user2", "1234", "user2@test.com");

        mvc.perform(post("/question/modify/" + question.getId()).with(csrf()).with(user("user2"))
                        .param("subject", "남의 글 수정")
                        .param("content", "남의 글 수정"))
                .andExpect(status().isForbidden());

        assertThat(question.getSubject()).isEqualTo("수정 전 제목");
    }

    @Test
    @DisplayName("POST /question/delete/{id}: 작성자는 삭제할 수 있고 답변도 함께 삭제된다")
    void t20() throws Exception {
        Question question = questionRepository.save(new Question(author, "삭제할 질문", "내용"));
        question.addAnswer(author, "함께 삭제될 답변");
        questionRepository.flush();

        mvc.perform(post("/question/delete/" + question.getId()).with(csrf()).with(user("user1")))
                .andExpect(redirectedUrl("/"));

        assertThat(questionRepository.findById(question.getId())).isEmpty();
    }

    @Test
    @DisplayName("POST /question/delete/{id}: 작성자가 아니면 403")
    void t21() throws Exception {
        Question question = questionRepository.save(new Question(author, "삭제할 질문", "내용"));
        userService.create("user2", "1234", "user2@test.com");

        mvc.perform(post("/question/delete/" + question.getId()).with(csrf()).with(user("user2")))
                .andExpect(status().isForbidden());

        assertThat(questionRepository.findById(question.getId())).isPresent();
    }

    @Test
    @DisplayName("POST /question/vote/{id}: 추천하면 추천 수가 늘고, 같은 사람이 다시 추천해도 1번만 센다")
    void t22() throws Exception {
        Question question = questionRepository.save(new Question(author, "추천 질문", "내용"));
        userService.create("user2", "1234", "user2@test.com");

        mvc.perform(post("/question/vote/" + question.getId()).with(csrf()).with(user("user2")))
                .andExpect(redirectedUrl("/question/detail/" + question.getId()));
        mvc.perform(post("/question/vote/" + question.getId()).with(csrf()).with(user("user2")));

        assertThat(question.getVoter()).hasSize(1);
        mvc.perform(get("/question/detail/" + question.getId()))
                .andExpect(content().string(containsString("bg-success\">1</span>")));
    }

    @Test
    @DisplayName("GET /question/list?kw=: 검색 결과만 보여주고, 페이지 링크에 검색어를 유지한다")
    void t23() throws Exception {
        for (int i = 1; i <= 15; i++) {
            questionRepository.save(new Question(author, "스프링 질문 %02d".formatted(i), "내용"));
        }
        questionRepository.save(new Question(author, "관계없는 글", "내용"));

        mvc.perform(get("/question/list").param("kw", "스프링"))
                .andExpect(content().string(containsString("스프링 질문 15")))
                .andExpect(content().string(not(containsString("관계없는 글"))))
                .andExpect(content().string(containsString("value=\"스프링\"")))
                .andExpect(content().string(containsString("page=1&amp;kw=")));
    }
}
