package com.reservation.service;

import com.reservation.dto.auth.UserRequest;
import com.reservation.mapper.UserMapper;
import com.reservation.model.UserEntity;
import com.reservation.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder; // AppConfig 에서 @Bean 주입받음

    @Transactional
    public UserEntity register(UserRequest user) {
        boolean exists =
                this.userRepository.existsByAccount(user.getAccount());

        if (exists) {
            throw new RuntimeException("이미 사용중인 아이디입니다.");
            // TODO: Change to Custom Exception
        }
        user.setPassword(this.passwordEncoder.encode(user.getPassword()));

        return this.userRepository.save(userMapper.toEntity(user));
    }

    @Override
    public UserDetails loadUserByUsername(String account)
            throws UsernameNotFoundException {

        return this.userRepository.findByAccount(account)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "couldn't find account -> " + account
                ));
    }
}
