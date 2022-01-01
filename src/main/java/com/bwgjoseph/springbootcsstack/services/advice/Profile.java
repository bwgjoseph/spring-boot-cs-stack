package com.bwgjoseph.springbootcsstack.services.advice;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = As.PROPERTY, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = Person.class, name = "1")
})
@SuperBuilder
@NoArgsConstructor
public class Profile {
    private boolean type;
    @Last
    private LastUpdate parentCreated;
    @Last
    private LastUpdate parentUpdated;
}
