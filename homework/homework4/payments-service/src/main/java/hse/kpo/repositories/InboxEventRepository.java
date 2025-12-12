package hse.kpo.repositories;

import hse.kpo.domains.InboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface InboxEventRepository extends JpaRepository<InboxEvent, UUID> {
    boolean existsByMessageId(String messageId);
}
