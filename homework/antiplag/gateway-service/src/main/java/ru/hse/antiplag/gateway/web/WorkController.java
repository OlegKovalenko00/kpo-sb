package ru.hse.antiplag.gateway.web;

import java.io.IOException;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import ru.hse.antiplag.gateway.client.AnalysisClient;
import ru.hse.antiplag.gateway.client.StorageClient;
import ru.hse.antiplag.gateway.dto.*;

@RestController
@RequestMapping("/api")
public class WorkController {

    private final StorageClient storageClient;
    private final AnalysisClient analysisClient;

    public WorkController(StorageClient storageClient,
                          AnalysisClient analysisClient) {
        this.storageClient = storageClient;
        this.analysisClient = analysisClient;
    }

    @PostMapping(value = "/works", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public WorkWithReportDto uploadWork(@RequestParam("file") MultipartFile file,
                                        @RequestParam("studentName") String studentName,
                                        @RequestParam("assignmentId") Long assignmentId) throws IOException {

        byte[] bytes = file.getBytes();
        StoredWorkDto storedWork = storageClient.upload(
                bytes,
                file.getOriginalFilename(),
                studentName,
                assignmentId
        );

        AnalyzeRequestDto request = new AnalyzeRequestDto();
        request.setWorkId(storedWork.getId());
        request.setAssignmentId(assignmentId);
        request.setStudentName(studentName);

        ReportDto report = analysisClient.analyze(request);

        return new WorkWithReportDto(storedWork, report);
    }

    @GetMapping("/assignments/{assignmentId}/reports")
    public List<ReportDto> getReports(@PathVariable Long assignmentId) {
        return analysisClient.getReportsByAssignment(assignmentId);
    }
}
