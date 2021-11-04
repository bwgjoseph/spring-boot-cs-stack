package com.bwgjoseph.springbootcsstack.services.post;

import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.ibatis.executor.BatchResult;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.dynamic.sql.render.RenderingStrategies;
import org.mybatis.dynamic.sql.update.UpdateDSL;
import org.mybatis.dynamic.sql.update.render.UpdateStatementProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class PostService {

    private final PostMapper postMapper;
    private final PostUpdateMapper postUpdateMapper;
    @Qualifier("batchSqlSession") private final SqlSession batchSqlSession;

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
    public Post patchByIdInBatch(Integer id, Post post) {
        log.info("Trigger patchByIdInBatch");
        List<List<BatchResult>> results = new ArrayList<>();
        int batchSize = 2;

        PostMapper pm = this.batchSqlSession.getMapper(PostMapper.class);

        for (int i = 1; i <= 4; i++) {
            pm.updateById(id++, post);

            if (i % batchSize == 0 || i == batchSize) {
                log.info("flushing batch");
                results.add(pm.flush());
            }
        }

        log.info((new BatchResults(results.size(), calculateRowsAffectedByMultipleBatches(results)).toString()));

        return new Post();
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
        List<List<BatchResult>> results = new ArrayList<>();
        int batchSize = 2;

        PostUpdateMapper p = this.batchSqlSession.getMapper(PostUpdateMapper.class);

        for (int i = 1; i <= 6; i++) {
            p.update(c -> PostUpdateMapper
                    .updateSelectiveColumns(post, c)
                    .where(PostDynamicSqlSupport.id, isEqualTo(id)));

            if (i % batchSize == 0 || i == batchSize) {
                log.info("flushing batch");
                results.add(p.flush());
            }
        }

        log.info((new BatchResults(results.size(), calculateRowsAffectedByMultipleBatches(results)).toString()));

        return new Post();
    }

    @Transactional
    public Post patchByIdInBatchSelectiveProvider(Integer id, Post post) {
        log.info("Trigger patchByIdInBatchSelectiveProvider");

        List<List<BatchResult>> results = new ArrayList<>();
        int batchSize = 4;

        PostUpdateMapper p = this.batchSqlSession.getMapper(PostUpdateMapper.class);

        for (int i = 1; i <= 8; i++) {
            UpdateStatementProvider updateStatement = UpdateDSL.update(PostDynamicSqlSupport.post)
                .set(PostDynamicSqlSupport.title).equalToWhenPresent(post::getTitle)
                .set(PostDynamicSqlSupport.body).equalToWhenPresent(post::getBody)
                .set(PostDynamicSqlSupport.createdAt).equalToWhenPresent(post::getCreatedAt)
                .where(PostDynamicSqlSupport.id, isEqualTo(id))
                .build()
                .render(RenderingStrategies.MYBATIS3);

            p.update(updateStatement);

            if (i % batchSize == 0 || i == batchSize) {
                log.info("flushing batch");
                results.add(p.flush());
            }
        }

        log.info((new BatchResults(results.size(), calculateRowsAffectedByMultipleBatches(results)).toString()));

        return post;
    }

    private int calculateRowsAffectedByMultipleBatches(List<List<BatchResult>> results) {
        return results.stream()
            .mapToInt(this::calculateRowsAffectedBySingleBatch)
            .sum();
    }

    private int calculateRowsAffectedBySingleBatch(List<BatchResult> results) {
        return results.stream()
            .map(BatchResult::getUpdateCounts)
            .flatMapToInt(Arrays::stream)
            .sum();
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
