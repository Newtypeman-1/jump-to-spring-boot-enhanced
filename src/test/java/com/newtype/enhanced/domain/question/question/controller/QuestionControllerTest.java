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

import static org.hamcrest.Matchers.containsString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
        mvc.perform(post("/question/create")
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

        mvc.perform(post("/question/create")
                        .param("subject", "")
                        .param("content", " "))
                .andExpect(status().isOk())
                .andExpect(view().name("question/question/create"))
                .andExpect(model().attributeHasFieldErrors("form", "subject", "content"))
                .andExpect(content().string(containsString("제목은 필수항목입니다.")))
                .andExpect(content().string(containsString("내용은 필수항목입니다.")));

        assertThat(questionRepository.count()).isEqualTo(count);
    }
}
