package com.bwgjoseph.springbootcsstack.services.advice;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
public class Person extends Profile {
    private String name;
    private int age;
    private SourceOfInfo soi;
    @Last
    private LastUpdate created;
    @Last
    private LastUpdate updated;
}
