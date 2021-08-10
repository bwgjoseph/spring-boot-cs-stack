package com.bwgjoseph.springbootcsstack.services.post;

import com.bwgjoseph.springbootcsstack.core.BaseEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@ToString(callSuper=true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Post extends BaseEntity {
    private Integer id;
    private String title;
    private String body;
}
