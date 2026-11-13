package com.newtype.enhanced.domain.question.question.service;

import com.newtype.enhanced.domain.question.question.entity.Question;
import com.newtype.enhanced.domain.question.question.repository.QuestionRepository;
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

    public Page<Question> findAll(int page) {
        return questionRepository.findAll(PageRequest.of(page, 10, Sort.by(Sort.Order.desc("id"))));
    }

    public Optional<Question> findById(int id) {
        return questionRepository.findById(id);
    }

    public Question create(String subject, String content) {
        return questionRepository.save(new Question(subject, content));
    }
}
