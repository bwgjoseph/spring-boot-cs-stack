package com.bwgjoseph.springbootcsstack.services.post;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class PostService {

    private final PostMapper postMapper;

    private Post updatePost(Post post) {
        post.setCreatedAt(LocalDateTime.now());
        post.setCreatedBy("joseph");
        post.setUpdatedAt(LocalDateTime.now());
        post.setUpdatedBy("Joseph");

        return post;
    }

    public List<Post> find() {
        return this.postMapper.find();
    }

    public Post get(Integer id) {
        return this.postMapper.get(id);
    }

    public Post create(Post post) {
        this.updatePost(post);

        this.postMapper.create(post);

        return post;
    }

    public Post update(Post post) {
        this.updatePost(post);

        this.postMapper.update(post);

        return post;
    }

    public Post updateById(Integer id, Post post) {
        this.updatePost(post);

        this.postMapper.updateById(id, post);

        post.setId(id);

        return post;
    }

    public Post delete(Integer id) {

        Post p = this.postMapper.get(id);

        this.postMapper.delete(id);

        return p;
    }
}
