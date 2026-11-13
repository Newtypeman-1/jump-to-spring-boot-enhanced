package com.newtype.enhanced.global.initData;

import com.newtype.enhanced.domain.question.question.entity.Question;
import com.newtype.enhanced.domain.question.question.repository.QuestionRepository;
import com.newtype.enhanced.domain.user.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@Profile("dev")
@RequiredArgsConstructor
public class DevInitData {

    @Autowired
    @Lazy
    private DevInitData self;

    private final QuestionRepository questionRepository;
    private final UserService userService;

    @Bean
    public ApplicationRunner devInitDataApplicationRunner() {
        return args -> {
            self.work0();
            self.work1();
            self.work2();
        };
    }

    // 개발용 계정: admin, user1, user2 (비밀번호는 모두 1234)
    @Transactional
    public void work0() {
        if (userService.existsByUsername("admin")) return;

        userService.create("admin", "1234", "admin@test.com");
        userService.create("user1", "1234", "user1@test.com");
        userService.create("user2", "1234", "user2@test.com");
    }

    @Transactional
    public void work1() {
        if (questionRepository.count() > 0) return;

        Question question1 = questionRepository.save(new Question("sbb가 무엇인가요?", "sbb에 대해서 알고 싶습니다."));
        question1.addAnswer("sbb는 점프 투 스프링부트로 만드는 게시판입니다.");

        Question question2 = questionRepository.save(new Question("스프링부트 모델 질문입니다.", "id는 자동으로 생성되나요?"));
        question2.addAnswer("네 자동으로 생성됩니다.");
    }

    @Transactional
    public void work2() {
        if (questionRepository.count() >= 300) return;

        for (int i = 1; i <= 300; i++) {
            questionRepository.save(new Question("테스트 데이터입니다:[%03d]".formatted(i), "내용무"));
        }
    }
}
