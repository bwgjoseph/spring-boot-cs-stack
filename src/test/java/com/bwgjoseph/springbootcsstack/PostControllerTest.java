package com.bwgjoseph.springbootcsstack;

import static org.mockito.ArgumentMatchers.any;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bwgjoseph.springbootcsstack.security.UserClaim;
import com.bwgjoseph.springbootcsstack.security.UserClaimDetailsService;
import com.bwgjoseph.springbootcsstack.services.post.PostController;
import com.bwgjoseph.springbootcsstack.services.post.PostService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(PostController.class)
@TestInstance(Lifecycle.PER_CLASS)
public class PostControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    private PostService postService;

    @Autowired
    private UserClaimDetailsService userClaimDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    void beforeAll() {
        this.userClaimDetailsService.createUser(new UserClaim("dynamic-user", List.of()));
    }

    @Test
    @WithUserDetails("dynamic-user")
    void testPostControllerGet() throws Exception {
        Mockito.when(postService.find()).thenReturn(List.of());

        this.mockMvc
            .perform(MockMvcRequestBuilders.get("/posts"))
            .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithUserDetails("admin")
    void testPostControllerPatch() throws Exception {
        Map<String, Object> result = new HashMap<>();
        result.put("title", "tt");
        result.put("id", 1);

        Mockito.when(postService.patchById(any(), any())).thenReturn(result);

        // As the caller, we can send the requestBody using `Map` so that
        // the actual pojo is not required to be constructed
        Map<String, Object> data = new HashMap<>();
        data.put("title", "tt");
        // body will be ignore as it is null
        data.put("body", null);
        String content = this.objectMapper.writeValueAsString(data);

        this.mockMvc
            .perform(MockMvcRequestBuilders.patch("/posts/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("tt"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1));
    }
}

// See https://docs.spring.io/spring-security/site/docs/current/reference/html5/#test-method-withsecuritycontext
// @Retention(RetentionPolicy.RUNTIME)
// @WithSecurityContext(factory = WithMockCustomUserSecurityContextFactory.class)
// @interface WithMockCustomUser {

//     String username() default "";

//     String name() default "";
// }

// class WithMockCustomUserSecurityContextFactory
//     implements WithSecurityContextFactory<WithMockCustomUser> {

//     @Autowired
//     UserDetailsService userDetailsService;

//     @Override
//     public SecurityContext createSecurityContext(WithMockCustomUser customUser) {
//         System.out.println(customUser.name());
//         SecurityContext context = SecurityContextHolder.createEmptyContext();
//         UserClaim user = (UserClaim) userDetailsService.loadUserByUsername(customUser.name());
//         // UserClaim user = new UserClaim("user1", "password");
//         Authentication auth =
//             new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword(), user.getAuthorities());
//         context.setAuthentication(auth);
//         return context;
//     }
// }