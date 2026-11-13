package com.newtype.enhanced.domain.question.question.service;

import com.newtype.enhanced.domain.question.answer.entity.Answer;
import com.newtype.enhanced.domain.question.question.entity.Question;
import com.newtype.enhanced.domain.question.question.repository.QuestionRepository;
import com.newtype.enhanced.domain.user.user.entity.SiteUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;

    public Page<Question> findAll(int page, String kw) {
        PageRequest pageRequest = PageRequest.of(page, 10, Sort.by(Sort.Order.desc("id")));

        if (kw.isBlank()) {
            return questionRepository.findAll(pageRequest);
        }

        return questionRepository.findAllByKeyword(kw, pageRequest);
    }

    public Optional<Question> findById(int id) {
        return questionRepository.findById(id);
    }

    public Question create(SiteUser author, String subject, String content) {
        return questionRepository.save(new Question(author, subject, content));
    }

    public void delete(Question question) {
        questionRepository.delete(question);
    }

    // 답변 id는 INSERT 후에 정해지므로, 앵커(#answer_{id})로 이동하려면 바로 flush
    public Answer addAnswer(Question question, SiteUser author, String content) {
        Answer answer = question.addAnswer(author, content);
        questionRepository.flush();
        return answer;
    }
}
