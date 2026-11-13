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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
}
