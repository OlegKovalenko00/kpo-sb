package ru.hse.antiplag.analysis.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class StorageClient {

    private final RestTemplate restTemplate;
    private final String storageBaseUrl;

    public StorageClient(RestTemplate restTemplate,
                         @Value("${services.storage.base-url}") String storageBaseUrl) {
        this.restTemplate = restTemplate;
        this.storageBaseUrl = storageBaseUrl;
    }

    public byte[] loadFileBytes(Long workId) {
        String url = storageBaseUrl + "/internal/files/" + workId;
        return restTemplate.getForObject(url, byte[].class);
    }
}
