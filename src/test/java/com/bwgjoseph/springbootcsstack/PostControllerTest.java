package com.bwgjoseph.springbootcsstack;

import java.util.List;

import com.bwgjoseph.springbootcsstack.config.UserClaimDetailsService;
import com.bwgjoseph.springbootcsstack.core.UserClaim;
import com.bwgjoseph.springbootcsstack.services.post.PostController;
import com.bwgjoseph.springbootcsstack.services.post.PostService;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
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