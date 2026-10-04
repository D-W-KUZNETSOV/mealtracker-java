package com.e.mealtracker.security;

import com.e.mealtracker.entity.User;
import com.e.mealtracker.exception.AccountDeletedException;
import com.e.mealtracker.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j  // <-- Эта аннотация создает переменную log
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(login)
                .or(() -> userRepository.findByUsername(login))
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + login));
        return new UserDetailsImpl(user);
    }
}
