package com.newtype.enhanced.domain.user.user.service;

import com.newtype.enhanced.domain.user.user.entity.SiteUser;
import com.newtype.enhanced.domain.user.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public SiteUser create(String username, String password, String email) {
        return userRepository.save(new SiteUser(username, passwordEncoder.encode(password), email));
    }

    public Optional<SiteUser> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public SiteUser getByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("로그인한 사용자를 찾을 수 없습니다: " + username));
    }
}
