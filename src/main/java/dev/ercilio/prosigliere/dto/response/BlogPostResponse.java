package dev.ercilio.prosigliere.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;

@Schema(description = "Full blog post with associated comments")
public record BlogPostResponse(
        @Schema(example = "1")
        Long id,

        @Schema(example = "Getting started with Spring Boot 4")
        String title,

        @Schema(example = "This post covers the basics of building REST APIs with Spring Boot.")
        String content,

        @Schema(example = "2026-05-18T10:00:00Z")
        Instant createdAt,

        @Schema(example = "2026-05-18T10:00:00Z")
        Instant updatedAt,

        List<CommentResponse> comments

) {
}
