package com.newtype.enhanced.domain.user.user.controller;

import com.newtype.enhanced.domain.user.user.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    record SignupForm(
            @NotBlank(message = "01-username-사용자ID는 필수항목입니다.")
            @Size(min = 3, max = 25, message = "01-username-사용자ID는 3자 이상 25자 이하로 입력해주세요.")
            String username,
            @NotBlank(message = "02-password1-비밀번호는 필수항목입니다.")
            String password1,
            @NotBlank(message = "03-password2-비밀번호 확인은 필수항목입니다.")
            String password2,
            @NotBlank(message = "04-email-이메일은 필수항목입니다.")
            @Email(message = "04-email-이메일 형식이 올바르지 않습니다.")
            String email
    ) {
    }

    @GetMapping("/signup")
    public String showSignup(@ModelAttribute("form") SignupForm form) {
        return "user/user/signup";
    }

    @PostMapping("/signup")
    @Transactional
    public String signup(@Valid @ModelAttribute("form") SignupForm form, BindingResult bindingResult) {
        if (!bindingResult.hasFieldErrors("password2") && !form.password2().equals(form.password1())) {
            bindingResult.rejectValue("password2", "passwordInCorrect",
                    "03-password2-2개의 비밀번호가 일치하지 않습니다.");
        }
        if (!bindingResult.hasFieldErrors("username") && userService.existsByUsername(form.username())) {
            bindingResult.rejectValue("username", "duplicated", "01-username-이미 사용 중인 사용자ID입니다.");
        }
        if (!bindingResult.hasFieldErrors("email") && userService.existsByEmail(form.email())) {
            bindingResult.rejectValue("email", "duplicated", "04-email-이미 사용 중인 이메일입니다.");
        }

        if (bindingResult.hasErrors()) {
            return "user/user/signup";
        }

        userService.create(form.username(), form.password1(), form.email());
        return "redirect:/";
    }
}
