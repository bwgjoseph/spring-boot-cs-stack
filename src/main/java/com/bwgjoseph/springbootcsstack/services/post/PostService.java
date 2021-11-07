package com.bwgjoseph.springbootcsstack.services.post;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class PostService {

    private final PostMapper postMapper;
    private final PostBatchRepository postBatchRepository;

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

    public Post patchById(Integer id, Post post) {
        log.info("Trigger patchById");
        this.postMapper.updateById(id, post);

        return post;
    }

    @Transactional
    public BatchResults patchByIdInBatch(List<Post> post) {
        log.info("Trigger patchByIdInBatch");
        return this.postBatchRepository.patchByIdInBatch(post);
    }

    /**
     * Can either
     *
     * - Construct `UpdateDSL` in the `Mapper` and call from the service class
     * - Construct `UpdateStatementProvider` and pass into the `update` method
     */
    @Transactional
    public Post patchByIdInBatchSelective(Integer id, Post post) {
        log.info("Trigger patchByIdInBatchSelective");
        this.postBatchRepository.patchByIdInBatchSelective(id, List.of(post, post, post));

        return new Post();
    }

    @Transactional
    public Post patchByIdInBatchSelectiveProvider(Integer id, Post post) {
        log.info("Trigger patchByIdInBatchSelectiveProvider");

        this.postBatchRepository.patchByIdInBatchSelectiveProvider(id, List.of(post, post, post));

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
