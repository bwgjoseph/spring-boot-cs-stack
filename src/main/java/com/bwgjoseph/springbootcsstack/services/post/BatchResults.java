package com.bwgjoseph.springbootcsstack.services.post;

import lombok.Value;

@Value
public class BatchResults {
    private int numberOfBatches;
    private int totalRowsAffected;
}
