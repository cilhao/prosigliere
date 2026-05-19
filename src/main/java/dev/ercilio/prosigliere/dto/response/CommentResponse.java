package dev.ercilio.prosigliere.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "Comment on a blog post")
public record CommentResponse(
        @Schema(example = "1")
        Long id,

        @Schema(example = "1")
        Long blogPostId,

        @Schema(example = "Great article, thanks for sharing!")
        String content,

        @Schema(example = "2026-05-18T10:15:30Z")
        Instant createdAt
) {
}
