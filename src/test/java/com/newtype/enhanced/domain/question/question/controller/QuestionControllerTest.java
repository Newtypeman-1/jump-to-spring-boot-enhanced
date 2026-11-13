package com.newtype.enhanced.domain.question.question.controller;

import com.newtype.enhanced.domain.question.question.entity.Question;
import com.newtype.enhanced.domain.question.question.repository.QuestionRepository;
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

    @Test
    @DisplayName("GET /question/list: 질문 목록을 보여준다")
    void t1() throws Exception {
        questionRepository.save(new Question("sbb가 무엇인가요?", "sbb에 대해서 알고 싶습니다."));

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
        Question question = questionRepository.save(new Question("sbb가 무엇인가요?", "sbb에 대해서 알고 싶습니다."));

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
        Question question = questionRepository.save(new Question("sbb가 무엇인가요?", "sbb에 대해서 알고 싶습니다."));
        question.addAnswer("첫 번째 답변");
        question.addAnswer("두 번째 답변");
        questionRepository.flush();

        mvc.perform(get("/question/detail/" + question.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("2개의 답변이 있습니다.")))
                .andExpect(content().string(containsString("두 번째 답변")));
    }

    @Test
    @DisplayName("GET /question/create: 질문 등록 폼을 보여준다")
    void t5() throws Exception {
        mvc.perform(get("/question/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("question/question/create"));
    }

    @Test
    @DisplayName("POST /question/create: 질문을 저장하고 목록으로 리다이렉트한다")
    void t6() throws Exception {
        mvc.perform(post("/question/create").with(csrf())
                        .param("subject", "새 질문")
                        .param("content", "새 질문 내용"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/question/list"));

        assertThat(questionRepository.findBySubject("새 질문")).isPresent();
    }

    @Test
    @DisplayName("POST /question/create: 제목·내용이 비면 폼을 다시 보여주고 오류를 순서대로 출력한다")
    void t7() throws Exception {
        long count = questionRepository.count();

        mvc.perform(post("/question/create").with(csrf())
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
            questionRepository.save(new Question("페이징 질문 %02d".formatted(i), "내용"));
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
            questionRepository.save(new Question("페이징 질문 %02d".formatted(i), "내용"));
        }

        mvc.perform(get("/question/list"))
                .andExpect(content().string(containsString("pagination")))
                .andExpect(content().string(containsString("/question/list?page=1")));
    }

    @Test
    @DisplayName("GET /question/list: 번호는 전체 개수에서 거꾸로 매긴다")
    void t11() throws Exception {
        for (int i = 1; i <= 15; i++) {
            questionRepository.save(new Question("페이징 질문 %02d".formatted(i), "내용"));
        }

        mvc.perform(get("/question/list").param("page", "1"))
                .andExpect(content().string(containsString("<td>5</td>")))
                .andExpect(content().string(not(containsString("<td>6</td>"))));
    }

    @Test
    @DisplayName("GET /question/list: 답변이 있으면 제목 옆에 답변 개수를 보여준다")
    void t12() throws Exception {
        Question question = questionRepository.save(new Question("답변 개수 질문", "내용"));
        question.addAnswer("답변 1");
        question.addAnswer("답변 2");
        question.addAnswer("답변 3");
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
        mvc.perform(get("/question/create"))
                .andExpect(content().string(containsString("name=\"_csrf\"")));
    }
}
