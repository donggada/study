package com.side.mvcTraffic.global.config.security;


import com.side.mvcTraffic.domain.user.repositorty.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import static com.side.mvcTraffic.global.exception.ErrorCode.USER_NOT_FOUND;


@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;


    @Override
    public UserDetails loadUserByUsername(String loginId) {
        return new CustomUserDetails(
                userRepository.findTokenCheckByLoginId(loginId)
                        .orElseThrow(() -> USER_NOT_FOUND.build())
        ); // UserDetails 구현체 반환
    }
}

