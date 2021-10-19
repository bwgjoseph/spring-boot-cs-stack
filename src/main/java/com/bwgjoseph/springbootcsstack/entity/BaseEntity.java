package com.bwgjoseph.springbootcsstack.entity;

import java.time.LocalDateTime;

import com.bwgjoseph.springbootcsstack.services.post.CreatedBy;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@ToString
// See https://www.baeldung.com/lombok-builder-inheritance#lombok-builder-and-inheritance-1
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseEntity {
    private LocalDateTime createdAt;
    @CreatedBy
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;
}
