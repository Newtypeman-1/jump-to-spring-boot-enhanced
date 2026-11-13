package com.newtype.enhanced.domain.user.user.controller;

import com.newtype.enhanced.domain.user.user.entity.SiteUser;
import com.newtype.enhanced.domain.user.user.repository.UserRepository;
import com.newtype.enhanced.domain.user.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.logout;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("GET /user/signup: 회원가입 폼")
    void t1() throws Exception {
        mvc.perform(get("/user/signup"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/user/signup"));
    }

    @Test
    @DisplayName("POST /user/signup: 가입하면 비밀번호를 암호화해 저장한다")
    void t2() throws Exception {
        mvc.perform(post("/user/signup").with(csrf())
                        .param("username", "user1")
                        .param("password1", "1234")
                        .param("password2", "1234")
                        .param("email", "user1@test.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        SiteUser user = userRepository.findByUsername("user1").get();
        assertThat(user.getPassword()).isNotEqualTo("1234");
        assertThat(passwordEncoder.matches("1234", user.getPassword())).isTrue();
    }

    @Test
    @DisplayName("POST /user/signup: 비밀번호 확인이 다르면 오류")
    void t3() throws Exception {
        mvc.perform(post("/user/signup").with(csrf())
                        .param("username", "user1")
                        .param("password1", "1234")
                        .param("password2", "5678")
                        .param("email", "user1@test.com"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("form", "password2"))
                .andExpect(content().string(containsString("2개의 비밀번호가 일치하지 않습니다.")));

        assertThat(userRepository.findByUsername("user1")).isEmpty();
    }

    @Test
    @DisplayName("POST /user/signup: 이미 있는 사용자ID·이메일이면 오류")
    void t4() throws Exception {
        userService.create("user1", "1234", "user1@test.com");

        mvc.perform(post("/user/signup").with(csrf())
                        .param("username", "user1")
                        .param("password1", "1234")
                        .param("password2", "1234")
                        .param("email", "user1@test.com"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("form", "username", "email"))
                .andExpect(content().string(containsString("이미 사용 중인 사용자ID입니다.")));
    }

    @Test
    @DisplayName("POST /user/login: 올바른 비밀번호면 로그인되고 / 로 이동")
    void t5() throws Exception {
        userService.create("user1", "1234", "user1@test.com");

        mvc.perform(formLogin("/user/login").user("user1").password("1234"))
                .andExpect(authenticated().withUsername("user1").withRoles("USER"))
                .andExpect(redirectedUrl("/"));
    }

    @Test
    @DisplayName("POST /user/login: 비밀번호가 틀리면 로그인 화면으로 돌아가 오류 표시")
    void t6() throws Exception {
        userService.create("user1", "1234", "user1@test.com");

        mvc.perform(formLogin("/user/login").user("user1").password("wrong"))
                .andExpect(unauthenticated())
                .andExpect(redirectedUrl("/user/login?error"));
    }

    @Test
    @DisplayName("admin 계정은 ADMIN 권한")
    void t7() throws Exception {
        userService.create("admin", "1234", "admin@test.com");

        mvc.perform(formLogin("/user/login").user("admin").password("1234"))
                .andExpect(authenticated().withRoles("ADMIN"));
    }

    @Test
    @DisplayName("POST /user/logout: 로그아웃 후 / 로 이동")
    void t8() throws Exception {
        mvc.perform(logout("/user/logout"))
                .andExpect(unauthenticated())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    @DisplayName("내비게이션 바: 로그인 상태면 사용자ID와 로그아웃, 아니면 로그인·회원가입")
    void t9() throws Exception {
        mvc.perform(get("/question/list").with(user("user1")))
                .andExpect(content().string(containsString("로그아웃")))
                .andExpect(content().string(org.hamcrest.Matchers.not(containsString("회원가입"))));

        mvc.perform(get("/question/list"))
                .andExpect(content().string(containsString("회원가입")))
                .andExpect(content().string(org.hamcrest.Matchers.not(containsString("로그아웃"))));
    }
}
