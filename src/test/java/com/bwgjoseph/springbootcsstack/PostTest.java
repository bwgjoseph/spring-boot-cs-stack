package com.bwgjoseph.springbootcsstack;

import static org.assertj.core.api.Assertions.assertThat;

import com.bwgjoseph.springbootcsstack.services.post.Post;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class PostTest {
    @LocalServerPort
	private int port;

    @Autowired
	private TestRestTemplate restTemplate;

    @Test
    public void shouldInsertNewPost() {
        String url = "http://localhost:" + port + "/posts";
        Post p = Post.builder()
            .title("title")
            .body("body")
            .build();

        Post pp = this.restTemplate.postForObject(url, p, Post.class);
        Post ppp = this.restTemplate.getForObject(url + "/{id}", Post.class, pp.getId());

        assertThat(pp)
            .usingRecursiveComparison()
            .isEqualTo(ppp);
    }
}
