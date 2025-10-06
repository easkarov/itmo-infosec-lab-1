package se.ifmo.ru.lab1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se.ifmo.ru.lab1.entity.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
}