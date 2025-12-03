package ru.hse.antiplag.analysis.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hse.antiplag.analysis.model.AnalyzeRequest;
import ru.hse.antiplag.analysis.model.AnalysisReport;
import ru.hse.antiplag.analysis.model.ReportStatus;
import ru.hse.antiplag.analysis.repo.AnalysisReportRepository;

import java.nio.charset.StandardCharsets;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class AnalysisService {

    private final StorageClient storageClient;
    private final AnalysisReportRepository reportRepository;

    public AnalysisService(StorageClient storageClient,
                           AnalysisReportRepository reportRepository) {
        this.storageClient = storageClient;
        this.reportRepository = reportRepository;
    }

    @Transactional
    public AnalysisReport analyze(AnalyzeRequest request) {
        AnalysisReport report = new AnalysisReport();
        report.setWorkId(request.getWorkId());
        report.setAssignmentId(request.getAssignmentId());
        report.setStudentName(request.getStudentName());
        report.setCreatedAt(Instant.now());

        try {
            byte[] bytes = storageClient.loadFileBytes(request.getWorkId());
            String hash = sha256(bytes);
            report.setFileHash(hash);

            Optional<AnalysisReport> sourceOpt =
                    reportRepository.findFirstByAssignmentIdAndFileHashAndStudentNameNotOrderByCreatedAtAsc(
                            request.getAssignmentId(),
                            hash,
                            request.getStudentName()
                    );

            if (sourceOpt.isPresent()) {
                AnalysisReport source = sourceOpt.get();

                report.setPlagiarized(true);
                report.setStatus(ReportStatus.PLAGIARISM_FOUND);
                report.setSourceStudentName(source.getStudentName());
                report.setSourceWorkId(source.getWorkId());

                source.setPlagiarized(true);
                source.setStatus(ReportStatus.PLAGIARISM_FOUND);
                reportRepository.save(source);
            } else {
                report.setPlagiarized(false);
                report.setStatus(ReportStatus.COMPLETED);
            }

            String wcUrl = buildWordCloudUrl(bytes);
            report.setWordCloudUrl(wcUrl);

        } catch (Exception e) {
            report.setStatus(ReportStatus.ERROR);
            report.setErrorMessage(e.getMessage());
        }

        return reportRepository.save(report);
    }

    public List<AnalysisReport> getReportsByAssignment(Long assignmentId) {
        return reportRepository.findByAssignmentId(assignmentId);
    }

    public Optional<AnalysisReport> getReport(Long id) {
        return reportRepository.findById(id);
    }

    public String getWordCloudUrl(Long reportId) {
        AnalysisReport report = reportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Report not found: " + reportId));

        if (report.getWordCloudUrl() == null) {
            try {
                byte[] bytes = storageClient.loadFileBytes(report.getWorkId());
                String url = buildWordCloudUrl(bytes);
                report.setWordCloudUrl(url);
                reportRepository.save(report);
            } catch (Exception e) {
                throw new RuntimeException("Failed to build word cloud", e);
            }
        }

        return report.getWordCloudUrl();
    }

    private String sha256(byte[] bytes) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(bytes);

        StringBuilder sb = new StringBuilder(hashBytes.length * 2);
        for (byte b : hashBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }


    private String buildWordCloudUrl(byte[] bytes) throws Exception {
        String text = new String(bytes, StandardCharsets.UTF_8);
        text = text.replace("\"", "'").replace("\n", " ");

        String configJson = "{"
                + "\"type\":\"wordcloud\","
                + "\"data\":{"
                + "\"datasets\":[{"
                + "\"data\":\"" + text + "\""
                + "}]"
                + "}"
                + "}";

        String encodedConfig = URLEncoder.encode(configJson, StandardCharsets.UTF_8);
        return "https://quickchart.io/wordcloud?text=" + encodedConfig;
    }
}
