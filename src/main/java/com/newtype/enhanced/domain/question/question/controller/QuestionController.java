package com.newtype.enhanced.domain.question.question.controller;

import com.newtype.enhanced.domain.question.question.entity.Question;
import com.newtype.enhanced.domain.question.question.service.QuestionService;
import com.newtype.enhanced.global.exception.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @GetMapping("/question/list")
    @Transactional(readOnly = true)
    public String list(Model model) {
        List<Question> questionList = questionService.findAll();
        model.addAttribute("questionList", questionList);
        return "question/question/list";
    }

    @GetMapping("/question/detail/{id}")
    @Transactional(readOnly = true)
    public String detail(@PathVariable int id, Model model) {
        Question question = questionService.findById(id)
                .orElseThrow(() -> new DataNotFoundException("question not found"));
        model.addAttribute("question", question);
        return "question/question/detail";
    }
}
