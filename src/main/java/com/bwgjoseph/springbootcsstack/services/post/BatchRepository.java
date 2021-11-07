package com.bwgjoseph.springbootcsstack.services.post;

import java.util.Arrays;
import java.util.List;

import org.apache.ibatis.executor.BatchResult;

public interface BatchRepository {
    default int calculateRowsAffectedByMultipleBatches(List<List<BatchResult>> results) {
        return results.stream()
            .mapToInt(this::calculateRowsAffectedBySingleBatch)
            .sum();
    }

    default int calculateRowsAffectedBySingleBatch(List<BatchResult> results) {
        return results.stream()
            .map(BatchResult::getUpdateCounts)
            .flatMapToInt(Arrays::stream)
            .sum();
    }
}
