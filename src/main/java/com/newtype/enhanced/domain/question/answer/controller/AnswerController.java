package com.newtype.enhanced.domain.question.answer.controller;

import com.newtype.enhanced.domain.question.question.entity.Question;
import com.newtype.enhanced.domain.question.question.service.QuestionService;
import com.newtype.enhanced.global.exception.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/answer")
@RequiredArgsConstructor
public class AnswerController {

    private final QuestionService questionService;

    @PostMapping("/create/{id}")
    @Transactional
    public String create(@PathVariable int id, @RequestParam String content) {
        Question question = questionService.findById(id)
                .orElseThrow(() -> new DataNotFoundException("question not found"));
        question.addAnswer(content);
        return "redirect:/question/detail/%d".formatted(id);
    }
}
