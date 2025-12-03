package ru.hse.antiplag.gateway.client;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import ru.hse.antiplag.gateway.dto.AnalyzeRequestDto;
import ru.hse.antiplag.gateway.dto.ReportDto;

@Component
public class AnalysisClient {

    private final RestTemplate restTemplate;
    private final String analysisBaseUrl;

    public AnalysisClient(RestTemplate restTemplate,
                          @Value("${services.analysis.base-url}") String analysisBaseUrl) {
        this.restTemplate = restTemplate;
        this.analysisBaseUrl = analysisBaseUrl;
    }

    public ReportDto analyze(AnalyzeRequestDto request) {
        String url = analysisBaseUrl + "/internal/analyze";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<AnalyzeRequestDto> entity = new HttpEntity<>(request, headers);

        ResponseEntity<ReportDto> response =
                restTemplate.postForEntity(url, entity, ReportDto.class);

        return response.getBody();
    }

    public List<ReportDto> getReportsByAssignment(Long assignmentId) {
        String url = analysisBaseUrl + "/internal/assignments/" + assignmentId + "/reports";

        ResponseEntity<ReportDto[]> response =
                restTemplate.getForEntity(url, ReportDto[].class);

        ReportDto[] body = response.getBody();
        if (body == null) {
            return List.of();
        }
        return Arrays.asList(body);
    }
}
