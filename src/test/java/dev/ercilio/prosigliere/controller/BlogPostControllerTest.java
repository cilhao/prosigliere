package dev.ercilio.prosigliere.controller;

import dev.ercilio.prosigliere.repository.BlogPostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class BlogPostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BlogPostRepository blogPostRepository;

    @BeforeEach
    void cleanDatabase() {
        blogPostRepository.deleteAll();
    }

    @Test
    void shouldManageBlogPostsAndComments() throws Exception {
        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "First Post",
                                  "content": "Content of the first post"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.title").value("First Post"))
                .andExpect(jsonPath("$.content").value("Content of the first post"))
                .andExpect(jsonPath("$.comments", hasSize(0)))
                .andExpect(jsonPath("$.id").value(1));

        mockMvc.perform(get("/api/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].title").value("First Post"))
                .andExpect(jsonPath("$.content[0].numberOfComments").value(0))
                .andExpect(jsonPath("$.size").value(5))
                .andExpect(jsonPath("$.totalElements").value(1));

        mockMvc.perform(post("/api/posts/{id}/comments", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "content": "First Nice Comment!"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.blogPostId").value(1))
                .andExpect(jsonPath("$.content").value("First Nice Comment!"));

        mockMvc.perform(post("/api/posts/{id}/comments", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "content": "Second Nice Comment!"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.blogPostId").value(1))
                .andExpect(jsonPath("$.content").value("Second Nice Comment!"));

        mockMvc.perform(get("/api/posts/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("First Post"))
                .andExpect(jsonPath("$.comments", hasSize(2)))
                .andExpect(jsonPath("$.comments[0].content").value("First Nice Comment!"))
                .andExpect(jsonPath("$.comments[1].content").value("Second Nice Comment!"));

        mockMvc.perform(get("/api/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].numberOfComments").value(2));
    }

    @Test
    void shouldPaginateBlogPosts() throws Exception {
        for (int i = 1; i <= 6; i++) {
            mockMvc.perform(post("/api/posts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "title": "Post %d",
                                      "content": "Content %d"
                                    }
                                    """.formatted(i, i)))
                    .andExpect(status().isCreated());
        }

        mockMvc.perform(get("/api/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(5)))
                .andExpect(jsonPath("$.totalElements").value(6))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.last").value(false));

        mockMvc.perform(get("/api/posts").param("page", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.number").value(1))
                .andExpect(jsonPath("$.last").value(true));
    }

    @Test
    void shouldReturnNotFoundForMissingBlogPost() throws Exception {
        mockMvc.perform(get("/api/posts/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Blog post not found with id: 999"));
    }

    @Test
    void shouldRejectInvalidBlogPostPayload() throws Exception {
        String title = IntStream.range(0, 20).mapToObj(i -> "more than 255 ").collect(Collectors.joining());

        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "%s",
                                  "content": ""
                                }
                                """.formatted(title)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.title").value("size must be between 0 and 255"))
                .andExpect(jsonPath("$.validationErrors.content").value("must not be blank"));
    }

    @Test
    void shouldRejectInvalidCommentPayload() throws Exception {
        mockMvc.perform(post("/api/posts/{id}/comments", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "content": ""
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.content").value("must not be blank"));
    }
}
