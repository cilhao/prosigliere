package dev.ercilio.prosigliere.mapper;

import dev.ercilio.prosigliere.domain.entity.BlogPostEntity;
import dev.ercilio.prosigliere.domain.entity.CommentEntity;
import dev.ercilio.prosigliere.dto.request.CreateBlogPostRequest;
import dev.ercilio.prosigliere.dto.response.BlogPostResponse;
import dev.ercilio.prosigliere.dto.response.BlogPostSummaryResponse;
import dev.ercilio.prosigliere.dto.response.CommentResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BlogPostMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "comments", ignore = true)
    BlogPostEntity toEntity(CreateBlogPostRequest request);

    BlogPostSummaryResponse toSummaryResponse(BlogPostEntity blogPostEntity);

    BlogPostResponse toBlogPostResponse(BlogPostEntity blogPostEntity);

    CommentResponse toCommentResponse(CommentEntity commentEntity);

}
