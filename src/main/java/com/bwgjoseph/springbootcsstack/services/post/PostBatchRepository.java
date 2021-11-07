package com.bwgjoseph.springbootcsstack.services.post;

import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.executor.BatchResult;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.dynamic.sql.render.RenderingStrategies;
import org.mybatis.dynamic.sql.update.UpdateDSL;
import org.mybatis.dynamic.sql.update.render.UpdateStatementProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// if only 1 item, then the batch result will be wrong
@Slf4j
@Repository
@AllArgsConstructor
public class PostBatchRepository implements BatchRepository {
    private static final int BATCH_SIZE = 2;

    @Qualifier("batchSqlSession")
    private final SqlSession batchSqlSession;

    public BatchResults patchByIdInBatch(List<Post> post) {
        List<List<BatchResult>> results = new ArrayList<>();

        PostUpdateMapper pm = this.batchSqlSession.getMapper(PostUpdateMapper.class);

        for (int i = 0; i < post.size(); i++) {
            pm.updateByIdBatch(post.get(i));

            if ((i + 1) % BATCH_SIZE == 0 || i == BATCH_SIZE) {
                log.info("flushing batch");
                results.add(pm.flush());
            }
        }

        BatchResults result = new BatchResults(results.size(), calculateRowsAffectedByMultipleBatches(results));

        log.info(result.toString());

        return result;
    }

    public BatchResults patchByIdInBatchSelective(Integer id, List<Post> post) {
        List<List<BatchResult>> results = new ArrayList<>();

        PostUpdateMapper p = this.batchSqlSession.getMapper(PostUpdateMapper.class);

        for (int i = 0; i < post.size(); i++) {
            Post currentPost = post.get(i);

            p.update(c -> PostUpdateMapper
                    .updateSelectiveColumns(currentPost, c)
                    .where(PostDynamicSqlSupport.id, isEqualTo(id)));

            if ((i + 1) % BATCH_SIZE == 0 || i == BATCH_SIZE) {
                log.info("flushing batch selective");
                results.add(p.flush());
            }
        }

        BatchResults result = new BatchResults(results.size(), calculateRowsAffectedByMultipleBatches(results));

        log.info(result.toString());

        return result;
    }

    public BatchResults patchByIdInBatchSelectiveProvider(Integer id, List<Post> post) {
        List<List<BatchResult>> results = new ArrayList<>();

        PostUpdateMapper p = this.batchSqlSession.getMapper(PostUpdateMapper.class);

        for (int i = 0; i < post.size(); i++) {
            Post currentPost = post.get(i);

            UpdateStatementProvider updateStatement = UpdateDSL.update(PostDynamicSqlSupport.post)
                .set(PostDynamicSqlSupport.title).equalToWhenPresent(currentPost::getTitle)
                .set(PostDynamicSqlSupport.body).equalToWhenPresent(currentPost::getBody)
                .set(PostDynamicSqlSupport.createdAt).equalToWhenPresent(currentPost::getCreatedAt)
                .where(PostDynamicSqlSupport.id, isEqualTo(id))
                .build()
                .render(RenderingStrategies.MYBATIS3);

            p.update(updateStatement);

            if (i % BATCH_SIZE == 0 || i == BATCH_SIZE) {
                log.info("flushing batch selective provider");
                results.add(p.flush());
            }
        }

        BatchResults result = new BatchResults(results.size(), calculateRowsAffectedByMultipleBatches(results));

        log.info(result.toString());

        return result;
    }
}
