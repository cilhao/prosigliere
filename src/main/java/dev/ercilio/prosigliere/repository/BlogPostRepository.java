package dev.ercilio.prosigliere.repository;

import dev.ercilio.prosigliere.domain.entity.BlogPostEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlogPostRepository extends JpaRepository<BlogPostEntity, Long> {

}
