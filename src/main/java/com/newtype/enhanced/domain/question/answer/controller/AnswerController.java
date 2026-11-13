package com.newtype.enhanced.domain.question.answer.controller;

import com.newtype.enhanced.domain.question.answer.entity.Answer;
import com.newtype.enhanced.domain.question.answer.service.AnswerService;
import com.newtype.enhanced.domain.question.question.entity.Question;
import com.newtype.enhanced.domain.question.question.service.QuestionService;
import com.newtype.enhanced.domain.user.user.entity.SiteUser;
import com.newtype.enhanced.domain.user.user.service.UserService;
import com.newtype.enhanced.global.exception.DataNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

import java.security.Principal;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/answer")
@RequiredArgsConstructor
public class AnswerController {

    private final QuestionService questionService;
    private final AnswerService answerService;
    private final UserService userService;

    public record CreateForm(
            @NotBlank(message = "01-content-내용은 필수항목입니다.")
            String content
    ) {
    }

    @PostMapping("/create/{id}")
    @PreAuthorize("isAuthenticated()")
    @Transactional
    public String create(
            @PathVariable int id,
            @Valid @ModelAttribute("form") CreateForm form,
            BindingResult bindingResult,
            Model model,
            Principal principal
    ) {
        Question question = questionService.findById(id)
                .orElseThrow(() -> new DataNotFoundException("question not found"));

        if (bindingResult.hasErrors()) {
            model.addAttribute("question", question);
            return "question/question/detail";
        }

        SiteUser author = userService.getByUsername(principal.getName());
        question.addAnswer(author, form.content());
        return "redirect:/question/detail/%d".formatted(id);
    }

    public record ModifyForm(
            @NotBlank(message = "01-content-내용은 필수항목입니다.")
            String content
    ) {
    }

    @GetMapping("/modify/{id}")
    @PreAuthorize("isAuthenticated()")
    @Transactional(readOnly = true)
    public String showModify(@PathVariable int id, Principal principal, Model model) {
        Answer answer = findAnswerOwnedBy(id, principal);
        model.addAttribute("answer", answer);
        model.addAttribute("form", new ModifyForm(answer.getContent()));
        return "question/answer/modify";
    }

    @PostMapping("/modify/{id}")
    @PreAuthorize("isAuthenticated()")
    @Transactional
    public String modify(
            @PathVariable int id,
            @Valid @ModelAttribute("form") ModifyForm form,
            BindingResult bindingResult,
            Principal principal,
            Model model
    ) {
        Answer answer = findAnswerOwnedBy(id, principal);

        if (bindingResult.hasErrors()) {
            model.addAttribute("answer", answer);
            return "question/answer/modify";
        }

        answer.modify(form.content());
        return "redirect:/question/detail/%d".formatted(answer.getQuestion().getId());
    }

    @PostMapping("/delete/{id}")
    @PreAuthorize("isAuthenticated()")
    @Transactional
    public String delete(@PathVariable int id, Principal principal) {
        Answer answer = findAnswerOwnedBy(id, principal);
        Question question = answer.getQuestion();
        question.deleteAnswer(answer);
        return "redirect:/question/detail/%d".formatted(question.getId());
    }

    @PostMapping("/vote/{id}")
    @PreAuthorize("isAuthenticated()")
    @Transactional
    public String vote(@PathVariable int id, Principal principal) {
        Answer answer = answerService.findById(id)
                .orElseThrow(() -> new DataNotFoundException("answer not found"));
        answer.vote(userService.getByUsername(principal.getName()));
        return "redirect:/question/detail/%d".formatted(answer.getQuestion().getId());
    }

    private Answer findAnswerOwnedBy(int id, Principal principal) {
        Answer answer = answerService.findById(id)
                .orElseThrow(() -> new DataNotFoundException("answer not found"));

        if (!answer.isAuthoredBy(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "권한이 없습니다.");
        }

        return answer;
    }
}
