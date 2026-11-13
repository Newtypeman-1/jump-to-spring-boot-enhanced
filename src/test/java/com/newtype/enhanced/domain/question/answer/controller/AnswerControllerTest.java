package com.newtype.enhanced.domain.question.answer.controller;

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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AnswerControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private QuestionRepository questionRepository;

    @Test
    @DisplayName("POST /answer/create/{id}: 답변을 등록하고 질문 상세로 리다이렉트한다")
    void t1() throws Exception {
        Question question = questionRepository.save(new Question("sbb가 무엇인가요?", "sbb에 대해서 알고 싶습니다."));

        mvc.perform(post("/answer/create/" + question.getId()).param("content", "답변입니다."))
                .andExpect(handler().handlerType(AnswerController.class))
                .andExpect(handler().methodName("create"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/question/detail/" + question.getId()));

        assertThat(question.getAnswerList()).hasSize(1);
        assertThat(question.getAnswerList().get(0).getContent()).isEqualTo("답변입니다.");
    }

    @Test
    @DisplayName("POST /answer/create/{id}: 없는 질문이면 404")
    void t2() throws Exception {
        mvc.perform(post("/answer/create/999999").param("content", "답변입니다."))
                .andExpect(status().isNotFound());
    }
}
