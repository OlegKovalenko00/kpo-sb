package ru.hse.antiplag.storage.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.hse.antiplag.storage.model.StoredWork;

import java.util.List;

public interface StoredWorkRepository extends JpaRepository<StoredWork, Long> {

    List<StoredWork> findByAssignmentId(Long assignmentId);
}
