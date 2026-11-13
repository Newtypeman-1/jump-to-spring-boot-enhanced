package com.newtype.enhanced.domain.question.question.controller;

import com.newtype.enhanced.domain.question.answer.controller.AnswerController;
import com.newtype.enhanced.domain.question.question.entity.Question;
import com.newtype.enhanced.domain.question.question.service.QuestionService;
import com.newtype.enhanced.domain.user.user.entity.SiteUser;
import com.newtype.enhanced.domain.user.user.service.UserService;
import com.newtype.enhanced.global.exception.DataNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;

import java.security.Principal;
import org.springframework.data.domain.Page;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;


@Controller
@RequestMapping("/question")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;
    private final UserService userService;

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
    @PreAuthorize("isAuthenticated()")
    public String showCreate(@ModelAttribute("form") CreateForm form) {
        return "question/question/create";
    }

    @PostMapping("/create")
    @PreAuthorize("isAuthenticated()")
    @Transactional
    public String create(
            @Valid @ModelAttribute("form") CreateForm form,
            BindingResult bindingResult,
            Principal principal
    ) {
        if (bindingResult.hasErrors()) {
            return "question/question/create";
        }

        SiteUser author = userService.getByUsername(principal.getName());
        questionService.create(author, form.subject(), form.content());
        return "redirect:/question/list";
    }

    record ModifyForm(
            @NotBlank(message = "01-subject-제목은 필수항목입니다.")
            @Size(max = 200, message = "01-subject-제목은 200자 이하로 입력해주세요.")
            String subject,
            @NotBlank(message = "02-content-내용은 필수항목입니다.")
            String content
    ) {
    }

    @GetMapping("/modify/{id}")
    @PreAuthorize("isAuthenticated()")
    @Transactional(readOnly = true)
    public String showModify(@PathVariable int id, Principal principal, Model model) {
        Question question = findQuestionOwnedBy(id, principal);
        model.addAttribute("question", question);
        model.addAttribute("form", new ModifyForm(question.getSubject(), question.getContent()));
        return "question/question/modify";
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
        Question question = findQuestionOwnedBy(id, principal);

        if (bindingResult.hasErrors()) {
            model.addAttribute("question", question);
            return "question/question/modify";
        }

        question.modify(form.subject(), form.content());
        return "redirect:/question/detail/%d".formatted(id);
    }

    @PostMapping("/delete/{id}")
    @PreAuthorize("isAuthenticated()")
    @Transactional
    public String delete(@PathVariable int id, Principal principal) {
        Question question = findQuestionOwnedBy(id, principal);
        questionService.delete(question);
        return "redirect:/";
    }

    @PostMapping("/vote/{id}")
    @PreAuthorize("isAuthenticated()")
    @Transactional
    public String vote(@PathVariable int id, Principal principal) {
        Question question = questionService.findById(id)
                .orElseThrow(() -> new DataNotFoundException("question not found"));
        question.vote(userService.getByUsername(principal.getName()));
        return "redirect:/question/detail/%d".formatted(id);
    }

    private Question findQuestionOwnedBy(int id, Principal principal) {
        Question question = questionService.findById(id)
                .orElseThrow(() -> new DataNotFoundException("question not found"));

        if (!question.isAuthoredBy(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "권한이 없습니다.");
        }

        return question;
    }
}
