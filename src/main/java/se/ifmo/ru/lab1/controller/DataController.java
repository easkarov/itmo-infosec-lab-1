package se.ifmo.ru.lab1.controller;

import se.ifmo.ru.lab1.dto.UserDto;
import se.ifmo.ru.lab1.dto.UserDtoSimple;
import se.ifmo.ru.lab1.entity.User;
import se.ifmo.ru.lab1.repository.UserRepository;
import se.ifmo.ru.lab1.security.jwt.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.owasp.encoder.Encode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/data")
@RequiredArgsConstructor
public class DataController {
    
    private final UserRepository userRepository;
    
    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<UserDtoSimple>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        log.info("Fetching users - page: {}, size: {}", page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<User> users = userRepository.findAll(pageable);
        
        List<UserDtoSimple> userDtoSimple = users.getContent().stream()
                .map(this::convertToDtoSimple)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(userDtoSimple);
    }

    @GetMapping("/profile")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserDto> getCurrentUserProfile(
        @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        log.info("Fetching profile for user: {}", userPrincipal.getUsername());

        return ResponseEntity.ok(convertToDto(userPrincipal));
    }
    
//    @GetMapping("/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
//        log.info("Fetching user with id: {}", id);
//
//        return userRepository.findById(id)
//                .map(this::convertToDto)
//                .map(ResponseEntity::ok)
//                .orElse(ResponseEntity.notFound().build());
//    }

    private UserDtoSimple convertToDtoSimple(User user) {
        return UserDtoSimple.builder()
            .id(user.getId())
            // XSS Protection: Encode all user-generated content
            .username(Encode.forHtml(user.getUsername()))
            .build();
    }
    
    private UserDto convertToDto(UserPrincipal user) {
        return UserDto.builder()
                .id(user.getId())
                // XSS Protection: Encode all user-generated content
                .username(Encode.forHtml(user.getUsername()))
                .role(user.getRole().name())
                .build();
    }
}