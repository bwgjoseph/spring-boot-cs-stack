package com.bwgjoseph.springbootcsstack.services.post;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class PostService {

    public List<Post> find() {
        Post post1 = Post.builder()
            .id(1)
            .title("title")
            .body("body")
            .createdAt(LocalDateTime.now())
            .createdBy("createdBy")
            .updatedAt(LocalDateTime.now())
            .updatedBy("updatedBy")
            .build();

        return List.of(post1);
    }
}
