package com.newtype.enhanced.domain.question.answer.service;

import com.newtype.enhanced.domain.question.answer.entity.Answer;
import com.newtype.enhanced.domain.question.answer.repository.AnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AnswerService {

    private final AnswerRepository answerRepository;

    public Optional<Answer> findById(int id) {
        return answerRepository.findById(id);
    }
}
