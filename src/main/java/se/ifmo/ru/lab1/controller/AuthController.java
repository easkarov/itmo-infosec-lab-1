package se.ifmo.ru.lab1.controller;

import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;
import se.ifmo.ru.lab1.dto.JwtResponse;
import se.ifmo.ru.lab1.dto.LoginRequest;
import se.ifmo.ru.lab1.dto.MessageResponse;
import se.ifmo.ru.lab1.dto.RegisterRequest;
import se.ifmo.ru.lab1.entity.User;
import se.ifmo.ru.lab1.repository.UserRepository;
import se.ifmo.ru.lab1.security.jwt.JwtUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;

    private static final PolicyFactory POLICY = Sanitizers.FORMATTING.and(Sanitizers.LINKS);

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        String login = POLICY.sanitize(loginRequest.getUsername());

        log.info("Login attempt for user: {}", login);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(login, loginRequest.getPassword()));
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        return ResponseEntity.ok(JwtResponse.builder()
                .token(jwt)
                .build());
    }
    
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest signUpRequest) {
        String login = POLICY.sanitize(signUpRequest.getUsername());
        log.info("Registration attempt for user: {}", login);
        if (userRepository.existsByUsername(login)) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }
        
        User user = User.builder()
                .username(login)
                .password(encoder.encode(signUpRequest.getPassword()))
                .role(User.Role.USER)
                .enabled(true)
                .build();
        
        userRepository.save(user);
        log.info("User registered successfully: {}", login);
        
        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }
}