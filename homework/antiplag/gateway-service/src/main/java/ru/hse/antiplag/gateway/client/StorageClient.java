package ru.hse.antiplag.gateway.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import ru.hse.antiplag.gateway.dto.StoredWorkDto;

@Component
public class StorageClient {

    private final RestTemplate restTemplate;
    private final String storageBaseUrl;

    public StorageClient(RestTemplate restTemplate,
                         @Value("${services.storage.base-url}") String storageBaseUrl) {
        this.restTemplate = restTemplate;
        this.storageBaseUrl = storageBaseUrl;
    }

    public StoredWorkDto upload(byte[] content,
                                String filename,
                                String studentName,
                                Long assignmentId) {
        String url = storageBaseUrl + "/internal/files";

        // Оборачиваем байты в "файл" для multipart/form-data
        ByteArrayResource fileResource = new ByteArrayResource(content) {
            @Override
            public String getFilename() {
                return filename != null ? filename : "file.txt";
            }
        };

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", fileResource);
        body.add("studentName", studentName);
        body.add("assignmentId", assignmentId.toString());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> requestEntity =
                new HttpEntity<>(body, headers);

        ResponseEntity<StoredWorkDto> response =
                restTemplate.postForEntity(url, requestEntity, StoredWorkDto.class);

        return response.getBody();
    }
}
