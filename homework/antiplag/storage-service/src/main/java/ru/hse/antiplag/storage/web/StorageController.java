package ru.hse.antiplag.storage.web;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.hse.antiplag.storage.model.StoredWork;
import ru.hse.antiplag.storage.service.FileStorageService;

@RestController
@RequestMapping("/internal")
public class StorageController {

    private final FileStorageService storageService;

    public StorageController(FileStorageService storageService) {
        this.storageService = storageService;
    }

    @PostMapping("/files")
    public StoredWork upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("studentName") String studentName,
            @RequestParam("assignmentId") Long assignmentId
    ) {
        return storageService.store(file, studentName, assignmentId);
    }

    @GetMapping(
            value = "/files/{id}",
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE
    )
    public byte[] download(@PathVariable Long id) {
        return storageService.loadFileContent(id);
    }
}
