package dev.ercilio.prosigliere.service;

import dev.ercilio.prosigliere.domain.entity.BlogPostEntity;
import dev.ercilio.prosigliere.domain.entity.CommentEntity;
import dev.ercilio.prosigliere.dto.request.CreateBlogPostRequest;
import dev.ercilio.prosigliere.dto.request.CreateCommentRequest;
import dev.ercilio.prosigliere.dto.response.BlogPostResponse;
import dev.ercilio.prosigliere.dto.response.BlogPostSummaryResponse;
import dev.ercilio.prosigliere.dto.response.CommentResponse;
import dev.ercilio.prosigliere.exception.ResourceNotFoundException;
import dev.ercilio.prosigliere.mapper.BlogPostMapper;
import dev.ercilio.prosigliere.repository.BlogPostRepository;
import dev.ercilio.prosigliere.repository.CommentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class BlogPostService {

    private final CommentRepository commentRepository;
    private final BlogPostRepository blogPostRepository;
    private final BlogPostMapper blogPostMapper;

    public BlogPostService(CommentRepository commentRepository, BlogPostRepository blogPostRepository, BlogPostMapper blogPostMapper) {
        this.commentRepository = commentRepository;
        this.blogPostRepository = blogPostRepository;
        this.blogPostMapper = blogPostMapper;
    }

    @Transactional(readOnly = true)
    public Page<BlogPostSummaryResponse> listPosts(Pageable pageable) {
        Page<BlogPostSummaryResponse> page = blogPostRepository.findAll(pageable).map(blogPostMapper::toSummaryResponse);
        log.debug(
                "Listed page {} with {} blog post(s) (total {})",
                page.getNumber(),
                page.getNumberOfElements(),
                page.getTotalElements());
        return page;
    }

    @Transactional
    public BlogPostResponse createPost(CreateBlogPostRequest request) {
        BlogPostEntity blogPostEntity = blogPostRepository.save(blogPostMapper.toEntity(request));
        log.info("Created blog post id={}", blogPostEntity.getId());
        return blogPostMapper.toBlogPostResponse(blogPostEntity);
    }

    @Transactional(readOnly = true)
    public BlogPostResponse getPost(Long id) {
        BlogPostEntity blogPostEntity = blogPostRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Blog post not found id={}", id);
                    return new ResourceNotFoundException("Blog post not found with id: " + id);
                });
        log.debug("Retrieved blog post id={} with {} comment(s)", id, blogPostEntity.getComments().size());
        return blogPostMapper.toBlogPostResponse(blogPostEntity);
    }

    @Transactional
    public CommentResponse addComment(Long postId, CreateCommentRequest request) {
        BlogPostEntity blogPostEntity = blogPostRepository.findById(postId)
                .orElseThrow(() -> {
                    log.warn("Blog post not found id={} when adding comment", postId);
                    return new ResourceNotFoundException("Blog post not found with id: " + postId);
                });

        CommentEntity commentEntity = new CommentEntity();
        commentEntity.setBlogPostId(blogPostEntity.getId());
        commentEntity.setContent(request.content());
        commentRepository.save(commentEntity);
        log.info("Added comment to blog post id={}", postId);

        return blogPostMapper.toCommentResponse(commentEntity);
    }
}
