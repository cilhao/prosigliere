package dev.ercilio.prosigliere.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "List of blog post with their number of comments")
public record BlogPostSummaryResponse(
        @Schema(example = "1")
        Long id,

        @Schema(example = "Getting started with Spring Boot 4")
        String title,

        @Schema(example = "3")
        int numberOfComments
) {
}
