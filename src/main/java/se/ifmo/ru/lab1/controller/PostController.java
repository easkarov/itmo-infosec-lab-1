package se.ifmo.ru.lab1.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.owasp.encoder.Encode;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import se.ifmo.ru.lab1.dto.CreatePostRequest;
import se.ifmo.ru.lab1.dto.PostDto;
import se.ifmo.ru.lab1.dto.UserDtoSimple;
import se.ifmo.ru.lab1.entity.Post;
import se.ifmo.ru.lab1.entity.User;
import se.ifmo.ru.lab1.repository.PostRepository;
import se.ifmo.ru.lab1.repository.UserRepository;
import se.ifmo.ru.lab1.security.jwt.UserPrincipal;

@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    private static final PolicyFactory POLICY = Sanitizers.FORMATTING.and(Sanitizers.LINKS);

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PostDto> createPost(
        @Valid @RequestBody CreatePostRequest request,
        @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        log.info("Creating post for user: {}", userPrincipal.getUsername());
        User user = userRepository.getReferenceById(userPrincipal.getId());
        Post post = Post.builder()
            .title(POLICY.sanitize(request.getTitle()))
            .content(POLICY.sanitize(request.getContent()))
            .user(user)
            .build();

        Post savedPost = postRepository.save(post);
        log.info("Post created with id: {}", savedPost.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(convertToDto(savedPost));
    }
    
    private PostDto convertToDto(Post post) {
        User author = post.getUser();
        UserDtoSimple authorDto = UserDtoSimple.builder()
                .id(author.getId())
                .username(Encode.forHtml(author.getUsername()))
                .build();
        
        return PostDto.builder()
                .id(post.getId())
                .title(Encode.forHtml(post.getTitle()))
                .content(Encode.forHtml(post.getContent()))
                .author(authorDto)
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }
}