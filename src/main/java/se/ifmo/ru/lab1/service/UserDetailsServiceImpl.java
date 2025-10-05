package se.ifmo.ru.lab1.service;

import se.ifmo.ru.lab1.repository.UserRepository;
import se.ifmo.ru.lab1.security.jwt.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    
    private final UserRepository userRepository;
    
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
            .map(user -> new UserPrincipal(user.getId(), user.getUsername(), user.getRole(), user.getPassword()))
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }
}