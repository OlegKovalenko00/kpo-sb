package ru.hse.antiplag.storage.model;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "stored_work")
public class StoredWork {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String studentName;

    private Long assignmentId;

    private String filename;

    private String filePath;

    private Instant uploadedAt;

    public StoredWork() {}

    public StoredWork(String studentName,
                      Long assignmentId,
                      String filename,
                      String filePath,
                      Instant uploadedAt) {
        this.studentName = studentName;
        this.assignmentId = assignmentId;
        this.filename = filename;
        this.filePath = filePath;
        this.uploadedAt = uploadedAt;
    }

    public Long getId() {
        return id;
    }

    public String getStudentName() {
        return studentName;
    }

    public Long getAssignmentId() {
        return assignmentId;
    }

    public String getFilename() {
        return filename;
    }

    public String getFilePath() {
        return filePath;
    }

    public Instant getUploadedAt() {
        return uploadedAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public void setAssignmentId(Long assignmentId) {
        this.assignmentId = assignmentId;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setUploadedAt(Instant uploadedAt) {
        this.uploadedAt = uploadedAt;
    }
}
