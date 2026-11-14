package com.newtype.enhanced.domain.user.user.controller;

import com.newtype.enhanced.domain.user.user.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
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

    @GetMapping("/login")
    public String showLogin() {
        return "user/user/login";
    }

    @GetMapping("/signup")
    public String showSignup(@ModelAttribute("form") SignupForm form) {
        return "user/user/signup";
    }

    // 저장 중 예외를 잡아 폼으로 돌려보내므로 컨트롤러 트랜잭션을 걸지 않음
    // (트랜잭션 안에서 예외가 나면 롤백 전용이 되어 커밋 시 다시 실패)
    @PostMapping("/signup")
    public String signup(@Valid @ModelAttribute("form") SignupForm form, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "user/user/signup";
        }

        if (!form.password1().equals(form.password2())) {
            bindingResult.rejectValue("password2", "passwordInCorrect",
                    "03-password2-2개의 비밀번호가 일치하지 않습니다.");
            return "user/user/signup";
        }

        try {
            userService.create(form.username(), form.password1(), form.email());
        } catch (DataIntegrityViolationException e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", "00-signupFailed-이미 등록된 사용자입니다.");
            return "user/user/signup";
        } catch (Exception e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", "00-signupFailed-" + e.getMessage());
            return "user/user/signup";
        }

        return "redirect:/";
    }
}
