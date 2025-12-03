package ru.hse.antiplag.analysis.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.hse.antiplag.analysis.model.AnalysisReport;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnalysisReportRepository extends JpaRepository<AnalysisReport, Long> {

    List<AnalysisReport> findByAssignmentId(Long assignmentId);

    Optional<AnalysisReport> findFirstByAssignmentIdAndFileHashAndStudentNameNotOrderByCreatedAtAsc(
            Long assignmentId,
            String fileHash,
            String studentName
    );
}
