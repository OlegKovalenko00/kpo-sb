package ru.hse.antiplag.analysis.web;

import org.springframework.web.bind.annotation.*;
import ru.hse.antiplag.analysis.model.AnalyzeRequest;
import ru.hse.antiplag.analysis.model.AnalysisReport;
import ru.hse.antiplag.analysis.service.AnalysisService;

import java.util.List;

@RestController
@RequestMapping("/internal")
public class AnalysisController {

    private final AnalysisService analysisService;

    public AnalysisController(AnalysisService analysisService) {
        this.analysisService = analysisService;
    }

    @PostMapping("/analyze")
    public AnalysisReport analyze(@RequestBody AnalyzeRequest request) {
        return analysisService.analyze(request);
    }

    @GetMapping("/assignments/{assignmentId}/reports")
    public List<AnalysisReport> getReportsByAssignment(@PathVariable Long assignmentId) {
        return analysisService.getReportsByAssignment(assignmentId);
    }

    @GetMapping("/reports/{id}/wordcloud")
    public WordCloudResponse getWordCloud(@PathVariable Long id) {
        String url = analysisService.getWordCloudUrl(id);
        return new WordCloudResponse(url);
    }
}
