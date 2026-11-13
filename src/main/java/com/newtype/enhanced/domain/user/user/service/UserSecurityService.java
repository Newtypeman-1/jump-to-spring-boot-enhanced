package com.newtype.enhanced.domain.user.user.service;

import com.newtype.enhanced.domain.user.user.entity.SiteUser;
import com.newtype.enhanced.domain.user.user.entity.UserRole;
import com.newtype.enhanced.domain.user.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserSecurityService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SiteUser siteUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        UserRole role = "admin".equals(username) ? UserRole.ADMIN : UserRole.USER;

        return User.withUsername(siteUser.getUsername())
                .password(siteUser.getPassword())
                .authorities(role.getValue())
                .build();
    }
}
