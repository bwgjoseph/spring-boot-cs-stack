package com.bwgjoseph.springbootcsstack;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;

import com.bwgjoseph.springbootcsstack.services.post.BatchResults;
import com.bwgjoseph.springbootcsstack.services.post.Post;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class PostControllerIT {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void test() {
        // Given
        String url = "http://localhost:" + port + "/posts/batch";
        Post post1 = Post.builder().id(65).title("title1").body("body1").viewCount(10).createdAt(LocalDateTime.now()).createdBy("createdBy").updatedAt(LocalDateTime.now()).updatedBy("updatedBy").build();
        Post post2 = Post.builder().id(66).title("title2").body("body2").viewCount(20).createdAt(LocalDateTime.now()).createdBy("createdBy").updatedAt(LocalDateTime.now()).updatedBy("updatedBy").build();
        Post post3 = Post.builder().id(68).title("title3").body("body3").viewCount(30).hidden(true).createdAt(LocalDateTime.now()).createdBy("createdBy").updatedAt(LocalDateTime.now()).updatedBy("updatedBy").build();

        BatchResults expected = BatchResults.builder().numberOfBatches(2).totalRowsAffected(3).build();

        // When
        BatchResults actual = this.restTemplate.patchForObject(url, List.of(post1, post2, post3), BatchResults.class);

        // Then
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @TestConfiguration
    static class Config {
        @Bean
        public RestTemplate httpComponentsClientRestTemplate() {
            HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
            requestFactory.setConnectTimeout(3000);
            requestFactory.setReadTimeout(3000);
            return new RestTemplate(requestFactory);
        }
    }
}
