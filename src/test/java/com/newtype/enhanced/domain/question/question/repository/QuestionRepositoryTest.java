package com.newtype.enhanced.domain.question.question.repository;

import com.newtype.enhanced.domain.question.question.entity.Question;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class QuestionRepositoryTest {

    @Autowired
    private QuestionRepository questionRepository;

    @Test
    @DisplayName("질문 저장: id가 발급되고 작성일·수정일이 자동으로 채워진다")
    void t1() {
        Question question = questionRepository.save(new Question("sbb가 무엇인가요?", "sbb에 대해서 알고 싶습니다."));

        assertThat(question.getId()).isPositive();
        assertThat(question.getCreateDate()).isNotNull();
        assertThat(question.getModifyDate()).isNotNull();
    }

    @Test
    @DisplayName("질문 전체 조회: findAll")
    void t2() {
        questionRepository.save(new Question("sbb가 무엇인가요?", "sbb에 대해서 알고 싶습니다."));
        questionRepository.save(new Question("스프링부트 모델 질문입니다.", "id는 자동으로 생성되나요?"));

        List<Question> questions = questionRepository.findAll();

        assertThat(questions).hasSize(2);
        assertThat(questions.get(0).getSubject()).isEqualTo("sbb가 무엇인가요?");
    }

    @Test
    @DisplayName("질문 단건 조회: findById")
    void t3() {
        Question saved = questionRepository.save(new Question("sbb가 무엇인가요?", "sbb에 대해서 알고 싶습니다."));

        Question question = questionRepository.findById(saved.getId()).get();

        assertThat(question.getSubject()).isEqualTo("sbb가 무엇인가요?");
    }
}
