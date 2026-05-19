package dev.ercilio.prosigliere.repository;

import dev.ercilio.prosigliere.domain.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {

}
