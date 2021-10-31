package com.bwgjoseph.springbootcsstack.services.post;

import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class PostService {

    private final PostMapper postMapper;
    private final PostUpdateMapper postUpdateMapper;

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

    /**
     * Can either
     *
     * - Construct `UpdateStatementProvider` and pass into the `update` method
     * - Construct `UpdateDSL` in the `Mapper` and call from the service class
     */
    public Post patchById(Integer id, Post post) {
        // UpdateStatementProvider updateStatement = UpdateDSL.update(PostDynamicSqlSupport.post)
        //     .set(PostDynamicSqlSupport.title).equalToWhenPresent(post::getTitle)
        //     .set(PostDynamicSqlSupport.body).equalToWhenPresent(post::getBody)
        //     .set(PostDynamicSqlSupport.createdAt).equalToWhenPresent(post::getCreatedAt)
        //     .where(PostDynamicSqlSupport.id, isEqualTo(id))
        //     .build()
        //     .render(RenderingStrategies.MYBATIS3);

        // this.postUpdateMapper.update(updateStatement);

        this.postUpdateMapper.update(c -> PostUpdateMapper
            .updateSelectiveColumns(post, c)
            .where(PostDynamicSqlSupport.id, isEqualTo(id)));

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
