package com.bwgjoseph.springbootcsstack.services.advice;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LastUpdate {
    @At
    private LocalDateTime at;
    @By
    private String by;
}
