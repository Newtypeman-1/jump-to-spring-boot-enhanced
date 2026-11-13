package com.newtype.enhanced.domain.question.question.repository;

import com.newtype.enhanced.domain.question.answer.entity.Answer;
import com.newtype.enhanced.domain.question.question.entity.Question;
import jakarta.persistence.EntityManager;
import com.newtype.enhanced.domain.user.user.entity.SiteUser;
import com.newtype.enhanced.domain.user.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class QuestionRepositoryTest {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private UserService userService;

    private SiteUser author;

    @BeforeEach
    void setUp() {
        author = userService.create("user1", "1234", "user1@test.com");
    }

    @Autowired
    private EntityManager em;

    @Test
    @DisplayName("질문 저장: id가 발급되고 작성일·수정일이 자동으로 채워진다")
    void t1() {
        Question question = questionRepository.save(new Question(author, "sbb가 무엇인가요?", "sbb에 대해서 알고 싶습니다."));

        assertThat(question.getId()).isPositive();
        assertThat(question.getCreateDate()).isNotNull();
        assertThat(question.getModifyDate()).isNotNull();
    }

    @Test
    @DisplayName("질문 전체 조회: findAll")
    void t2() {
        questionRepository.save(new Question(author, "sbb가 무엇인가요?", "sbb에 대해서 알고 싶습니다."));
        questionRepository.save(new Question(author, "스프링부트 모델 질문입니다.", "id는 자동으로 생성되나요?"));

        List<Question> questions = questionRepository.findAll();

        assertThat(questions).hasSize(2);
        assertThat(questions.get(0).getSubject()).isEqualTo("sbb가 무엇인가요?");
    }

    @Test
    @DisplayName("질문 단건 조회: findById")
    void t3() {
        Question saved = questionRepository.save(new Question(author, "sbb가 무엇인가요?", "sbb에 대해서 알고 싶습니다."));

        Question question = questionRepository.findById(saved.getId()).get();

        assertThat(question.getSubject()).isEqualTo("sbb가 무엇인가요?");
    }

    @Test
    @DisplayName("제목으로 조회: findBySubject")
    void t4() {
        questionRepository.save(new Question(author, "sbb가 무엇인가요?", "sbb에 대해서 알고 싶습니다."));

        Question question = questionRepository.findBySubject("sbb가 무엇인가요?").get();

        assertThat(question.getContent()).isEqualTo("sbb에 대해서 알고 싶습니다.");
    }

    @Test
    @DisplayName("제목과 내용으로 조회: findBySubjectAndContent")
    void t5() {
        Question saved = questionRepository.save(new Question(author, "sbb가 무엇인가요?", "sbb에 대해서 알고 싶습니다."));

        Question question = questionRepository
                .findBySubjectAndContent("sbb가 무엇인가요?", "sbb에 대해서 알고 싶습니다.")
                .get();

        assertThat(question.getId()).isEqualTo(saved.getId());
    }

    @Test
    @DisplayName("제목 일부로 조회: findBySubjectLike")
    void t6() {
        questionRepository.save(new Question(author, "sbb가 무엇인가요?", "sbb에 대해서 알고 싶습니다."));
        questionRepository.save(new Question(author, "스프링부트 모델 질문입니다.", "id는 자동으로 생성되나요?"));

        List<Question> questions = questionRepository.findBySubjectLike("sbb%");

        assertThat(questions).hasSize(1);
        assertThat(questions.get(0).getSubject()).isEqualTo("sbb가 무엇인가요?");
    }

    @Test
    @DisplayName("질문 수정: modify 후 flush하면 UPDATE되고 수정일이 바뀐다")
    void t7() {
        Question question = questionRepository.save(new Question(author, "sbb가 무엇인가요?", "sbb에 대해서 알고 싶습니다."));
        LocalDateTime oldModifyDate = question.getModifyDate();

        question.modify("수정된 제목", "수정된 내용");
        questionRepository.flush();

        Question modified = questionRepository.findById(question.getId()).get();
        assertThat(modified.getSubject()).isEqualTo("수정된 제목");
        assertThat(modified.getModifyDate()).isAfter(oldModifyDate);
    }

    @Test
    @DisplayName("질문 삭제: delete")
    void t8() {
        Question question = questionRepository.save(new Question(author, "sbb가 무엇인가요?", "sbb에 대해서 알고 싶습니다."));
        assertThat(questionRepository.count()).isEqualTo(1);

        questionRepository.delete(question);

        assertThat(questionRepository.count()).isZero();
    }

    @Test
    @DisplayName("답변 저장: addAnswer 후 flush하면 cascade PERSIST로 답변도 저장된다")
    void t9() {
        Question question = questionRepository.save(new Question(author, "sbb가 무엇인가요?", "sbb에 대해서 알고 싶습니다."));

        Answer answer = question.addAnswer(author, "네 자동으로 생성됩니다.");
        questionRepository.flush();

        assertThat(answer.getId()).isPositive();
        assertThat(answer.getCreateDate()).isNotNull();
    }

    @Test
    @DisplayName("답변으로 질문 조회: answer.getQuestion")
    void t10() {
        Question question = questionRepository.save(new Question(author, "sbb가 무엇인가요?", "sbb에 대해서 알고 싶습니다."));
        Answer saved = question.addAnswer(author, "네 자동으로 생성됩니다.");
        questionRepository.flush();
        em.clear();

        Answer answer = em.find(Answer.class, saved.getId());

        assertThat(answer.getQuestion().getId()).isEqualTo(question.getId());
    }

    @Test
    @DisplayName("질문으로 답변 목록 조회: LAZY 로딩은 트랜잭션 안에서 동작한다")
    void t11() {
        Question saved = questionRepository.save(new Question(author, "sbb가 무엇인가요?", "sbb에 대해서 알고 싶습니다."));
        saved.addAnswer(author, "네 자동으로 생성됩니다.");
        questionRepository.flush();
        em.clear();

        Question question = questionRepository.findById(saved.getId()).get();
        List<Answer> answers = question.getAnswerList();

        assertThat(answers).hasSize(1);
        assertThat(answers.get(0).getContent()).isEqualTo("네 자동으로 생성됩니다.");
    }
}
