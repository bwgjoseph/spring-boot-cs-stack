package com.bwgjoseph.springbootcsstack.services.post;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @GetMapping
    public List<Post> find() {
        return this.postService.find();
    }

    @GetMapping("/{id}")
    public Post get(@PathVariable Integer id) {
        return this.postService.get(id);
    }

    @PostMapping
    public Post create(@RequestBody Post post) {
        return this.postService.create(post);
    }

    @PutMapping
    public Post update(@RequestBody Post post) {
        return this.postService.update(post);
    }

    @PutMapping("/{id}")
    public Post updateById(@PathVariable Integer id, @RequestBody Post post) {
        return this.postService.updateById(id, post);
    }

    @DeleteMapping("/{id}")
    public Post delete(@PathVariable Integer id) {
        return this.postService.delete(id);
    }
}
