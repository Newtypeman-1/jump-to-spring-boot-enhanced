package com.newtype.enhanced.domain.question.answer.controller;

import com.newtype.enhanced.domain.question.question.entity.Question;
import com.newtype.enhanced.domain.question.question.service.QuestionService;
import com.newtype.enhanced.global.exception.DataNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/answer")
@RequiredArgsConstructor
public class AnswerController {

    private final QuestionService questionService;

    public record CreateForm(
            @NotBlank(message = "01-content-내용은 필수항목입니다.")
            String content
    ) {
    }

    @PostMapping("/create/{id}")
    @Transactional
    public String create(
            @PathVariable int id,
            @Valid @ModelAttribute("form") CreateForm form,
            BindingResult bindingResult,
            Model model
    ) {
        Question question = questionService.findById(id)
                .orElseThrow(() -> new DataNotFoundException("question not found"));

        if (bindingResult.hasErrors()) {
            model.addAttribute("question", question);
            return "question/question/detail";
        }

        question.addAnswer(form.content());
        return "redirect:/question/detail/%d".formatted(id);
    }
}
