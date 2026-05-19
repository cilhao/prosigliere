package dev.ercilio.prosigliere.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "comment")
@Getter
@Setter
public class CommentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "blog_post_id", nullable = false, insertable = false, updatable = false)
    private BlogPostEntity blogPostEntity;

    @Column(name = "blog_post_id", nullable = false)
    private Long blogPostId;

    @Column(nullable = false, columnDefinition = "CLOB")
    private String content;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() {
        createdAt = Instant.now();
    }
}
