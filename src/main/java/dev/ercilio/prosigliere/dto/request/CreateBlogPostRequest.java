package dev.ercilio.prosigliere.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Payload to create a new blog post")
public record CreateBlogPostRequest(
        @NotBlank
        @Size(max = 255)
        @Schema(example = "Getting started with Spring Boot 4")
        String title,

        @NotBlank
        @Schema(example = "This post covers the basics of building REST APIs with Spring Boot.")
        String content
) {
}
