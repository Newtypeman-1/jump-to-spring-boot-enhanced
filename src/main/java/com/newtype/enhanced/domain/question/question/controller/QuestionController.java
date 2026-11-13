package com.newtype.enhanced.domain.question.question.controller;

import com.newtype.enhanced.domain.question.answer.controller.AnswerController;
import com.newtype.enhanced.domain.question.question.entity.Question;
import com.newtype.enhanced.domain.question.question.service.QuestionService;
import com.newtype.enhanced.global.exception.DataNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequestMapping("/question")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @GetMapping("/list")
    @Transactional(readOnly = true)
    public String list(@RequestParam(defaultValue = "0") int page, Model model) {
        Page<Question> paging = questionService.findAll(page);
        model.addAttribute("paging", paging);
        return "question/question/list";
    }

    @GetMapping("/detail/{id}")
    @Transactional(readOnly = true)
    public String detail(
            @PathVariable int id,
            @ModelAttribute("form") AnswerController.CreateForm form,
            Model model
    ) {
        Question question = questionService.findById(id)
                .orElseThrow(() -> new DataNotFoundException("question not found"));
        model.addAttribute("question", question);
        return "question/question/detail";
    }

    record CreateForm(
            @NotBlank(message = "01-subject-제목은 필수항목입니다.")
            @Size(max = 200, message = "01-subject-제목은 200자 이하로 입력해주세요.")
            String subject,
            @NotBlank(message = "02-content-내용은 필수항목입니다.")
            String content
    ) {
    }

    @GetMapping("/create")
    public String showCreate(@ModelAttribute("form") CreateForm form) {
        return "question/question/create";
    }

    @PostMapping("/create")
    @Transactional
    public String create(@Valid @ModelAttribute("form") CreateForm form, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "question/question/create";
        }

        questionService.create(form.subject(), form.content());
        return "redirect:/question/list";
    }
}
