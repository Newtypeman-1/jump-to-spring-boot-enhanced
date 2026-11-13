package com.newtype.enhanced.domain.question.question.repository;

import com.newtype.enhanced.domain.question.question.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Integer> {
}
