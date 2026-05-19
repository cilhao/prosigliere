package dev.ercilio.prosigliere.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Payload to add a comment to a blog post")
public record CreateCommentRequest(
        @NotBlank
        @Schema(example = "Great article, thanks for sharing!")
        String content
) {
}
