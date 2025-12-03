package ru.hse.antiplag.analysis.model;

import java.time.Instant;

public class ReportDto {

    private Long id;
    private Long workId;
    private Long assignmentId;
    private String studentName;
    private boolean plagiarized;
    private ReportStatus status;
    private String sourceStudentName;
    private Long sourceWorkId;
    private Instant createdAt;

    public ReportDto() {
    }

    public ReportDto(AnalysisReport report) {
        this.id = report.getId();
        this.workId = report.getWorkId();
        this.assignmentId = report.getAssignmentId();
        this.studentName = report.getStudentName();
        this.plagiarized = report.isPlagiarized();
        this.status = report.getStatus();
        this.sourceStudentName = report.getSourceStudentName();
        this.sourceWorkId = report.getSourceWorkId();
        this.createdAt = report.getCreatedAt();
    }

    public Long getId() {
        return id;
    }

    public Long getWorkId() {
        return workId;
    }

    public void setWorkId(Long workId) {
        this.workId = workId;
    }

    public Long getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(Long assignmentId) {
        this.assignmentId = assignmentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public boolean isPlagiarized() {
        return plagiarized;
    }

    public void setPlagiarized(boolean plagiarized) {
        this.plagiarized = plagiarized;
    }

    public ReportStatus getStatus() {
        return status;
    }

    public void setStatus(ReportStatus status) {
        this.status = status;
    }

    public String getSourceStudentName() {
        return sourceStudentName;
    }

    public void setSourceStudentName(String sourceStudentName) {
        this.sourceStudentName = sourceStudentName;
    }

    public Long getSourceWorkId() {
        return sourceWorkId;
    }

    public void setSourceWorkId(Long sourceWorkId) {
        this.sourceWorkId = sourceWorkId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
