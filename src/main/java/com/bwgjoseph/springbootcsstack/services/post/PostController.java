package com.bwgjoseph.springbootcsstack.services.post;

import java.util.List;
import java.util.Map;

import com.bwgjoseph.springbootcsstack.aspect.LogExecutionTime;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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

    @LogExecutionTime
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

    @PatchMapping(path = "/{id}")
    public Post patchById(@PathVariable Integer id, @RequestBody Post post) {
        ObjectMapper om = new ObjectMapper();
        om.setSerializationInclusion(Include.NON_NULL);
        Map<String, Object> mp = om.convertValue(post, Map.class);
        return this.postService.patchById(id, mp);
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
