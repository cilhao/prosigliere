package dev.ercilio.prosigliere.controller;

import dev.ercilio.prosigliere.dto.request.CreateBlogPostRequest;
import dev.ercilio.prosigliere.dto.request.CreateCommentRequest;
import dev.ercilio.prosigliere.dto.response.BlogPostResponse;
import dev.ercilio.prosigliere.dto.response.BlogPostSummaryResponse;
import dev.ercilio.prosigliere.dto.response.CommentResponse;
import dev.ercilio.prosigliere.service.BlogPostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Slf4j
@RestController
@RequestMapping(value = "/api/posts", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Blog Posts", description = "Manage blog posts and their comments")
public class BlogPostController {

    private final BlogPostService blogPostService;

    public BlogPostController(BlogPostService blogPostService) {
        this.blogPostService = blogPostService;
    }

    @GetMapping
    @Operation(
            summary = "List blog posts",
            description = "Returns a paginated list of post titles and comment counts (5 per page by default)")
    @ApiResponse(responseCode = "200", description = "Posts retrieved successfully")
    public Page<BlogPostSummaryResponse> listPosts(@ParameterObject @PageableDefault(size = 5) Pageable pageable) {
        log.debug("GET /api/posts page={} size={}", pageable.getPageNumber(), pageable.getPageSize());
        return blogPostService.listPosts(pageable);
    }

    @PostMapping
    @Operation(summary = "Create a blog post")
    @ApiResponse(responseCode = "201", description = "Post created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request body")
    public ResponseEntity<BlogPostResponse> createPost(@Valid @RequestBody CreateBlogPostRequest request) {
        log.info("POST /api/posts title={}", request.title());
        BlogPostResponse created = blogPostService.createPost(request);
        return ResponseEntity
                .created(URI.create("/api/posts/" + created.id()))
                .body(created);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a blog post by ID", description = "Returns title, content, and all associated comments")
    @ApiResponse(responseCode = "200", description = "Post retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Post not found")
    public BlogPostResponse getPost(@PathVariable Long id) {
        log.debug("GET /api/posts/{}", id);
        return blogPostService.getPost(id);
    }

    @PostMapping("/{id}/comments")
    @Operation(summary = "Add a comment to a blog post")
    @ApiResponse(responseCode = "201", description = "Comment created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request body")
    @ApiResponse(responseCode = "404", description = "Post not found")
    public ResponseEntity<CommentResponse> addComment(@PathVariable Long id, @Valid @RequestBody CreateCommentRequest request) {
        log.info("POST /api/posts/{}/comments", id);
        CommentResponse created = blogPostService.addComment(id, request);

        return ResponseEntity
                .created(URI.create("/api/posts/" + id + "/comments/" + created.id()))
                .body(created);
    }
}
