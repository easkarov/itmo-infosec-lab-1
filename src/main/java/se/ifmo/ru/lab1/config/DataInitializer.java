package se.ifmo.ru.lab1.config;

import se.ifmo.ru.lab1.entity.User;
import se.ifmo.ru.lab1.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataInitializer {
    
    private final PasswordEncoder passwordEncoder;
    
    @Bean
    CommandLineRunner init(UserRepository userRepository) {
        return args -> {
            // Create test users
            if (userRepository.count() == 0) {
                User admin = User.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("admin123"))
                        .role(User.Role.ADMIN)
                        .enabled(true)
                        .build();
                admin = userRepository.save(admin);
                log.info("Admin user created: {}", admin.getUsername());
                
                User user = User.builder()
                        .username("user")
                        .password(passwordEncoder.encode("user123"))
                        .role(User.Role.USER)
                        .enabled(true)
                        .build();
                user = userRepository.save(user);
                log.info("Regular user created: {}", user.getUsername());
                
                log.info("Sample data initialized");
            }
        };
    }
}