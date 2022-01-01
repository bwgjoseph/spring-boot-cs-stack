package com.bwgjoseph.springbootcsstack.services.advice;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SourceOfInfo {
    private String source;
    @Last
    private LastUpdate childCreated;
    @Last
    private LastUpdate childUpdated;
}
