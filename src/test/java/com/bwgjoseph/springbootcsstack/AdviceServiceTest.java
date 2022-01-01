package com.bwgjoseph.springbootcsstack;

import java.lang.reflect.Field;
import java.util.List;

import com.bwgjoseph.springbootcsstack.services.advice.AdviceService;
import com.bwgjoseph.springbootcsstack.services.advice.Last;
import com.bwgjoseph.springbootcsstack.services.advice.Person;
import com.bwgjoseph.springbootcsstack.services.advice.SourceOfInfo;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@TestInstance(Lifecycle.PER_CLASS)
public class AdviceServiceTest {
    private AdviceService adviceService;

    @BeforeAll
    void beforeAll() {
        this.adviceService = new AdviceService();
    }

    @Test
    void shouldFindAllFieldsMarkedWithLastAnnotation() {
        Person person = Person.builder()
                            .name("joseph")
                            .age(20)
                            .soi(SourceOfInfo.builder().source("source").build())
                            .build();

        List<Field> f = this.adviceService.findFieldsAnnotatedWithAndDefine(person, Last.class);

        Assertions.assertThat(f.size()).isEqualTo(6);
        Assertions.assertThat(person).isNotNull();
        Assertions.assertThat(person.getSoi().getChildCreated()).isNotNull();
        Assertions.assertThat(person.getSoi().getChildUpdated()).isNotNull();
        Assertions.assertThat(person.getCreated()).isNotNull();
        Assertions.assertThat(person.getUpdated()).isNotNull();
        Assertions.assertThat(person.getParentCreated()).isNotNull();
        Assertions.assertThat(person.getParentUpdated()).isNotNull();
    }

    @Test
    void doesNotHandleIfChildObjectIsNull() {
        Person person = Person.builder()
                            .name("joseph")
                            .age(20)
                            // soi is not defined, will not set LastUpdate
                            .build();

        List<Field> f = this.adviceService.findFieldsAnnotatedWithAndDefine(person, Last.class);

        Assertions.assertThat(f.size()).isEqualTo(6);
        Assertions.assertThat(person).isNotNull();
        Assertions.assertThat(person.getSoi().getChildCreated()).isNotNull();
        Assertions.assertThat(person.getSoi().getChildUpdated()).isNotNull();
        Assertions.assertThat(person.getCreated()).isNotNull();
        Assertions.assertThat(person.getUpdated()).isNotNull();
        Assertions.assertThat(person.getParentCreated()).isNotNull();
        Assertions.assertThat(person.getParentUpdated()).isNotNull();
    }
}
