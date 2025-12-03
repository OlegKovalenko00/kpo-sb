package ru.hse.antiplag.storage.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.hse.antiplag.storage.model.StoredWork;
import ru.hse.antiplag.storage.repo.StoredWorkRepository;

import java.io.IOException;
import java.nio.file.*;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class FileStorageService {

    private final StoredWorkRepository repository;
    private final Path rootDir;

    public FileStorageService(StoredWorkRepository repository,
                              @Value("${storage.upload-dir}") String uploadDir) {
        this.repository = repository;
        this.rootDir = Paths.get(uploadDir);
    }

    public StoredWork store(MultipartFile file, String studentName, Long assignmentId) {
        try {
            Files.createDirectories(rootDir);
            String ext = extractExtension(file.getOriginalFilename());
            String filename = UUID.randomUUID() + (ext == null ? "" : "." + ext);
            Path target = rootDir.resolve(filename);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            StoredWork work = new StoredWork(
                    studentName,
                    assignmentId,
                    file.getOriginalFilename(),
                    target.toString(),
                    Instant.now()
            );
            return repository.save(work);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot store file", e);
        }
    }

    public byte[] loadFileContent(Long id) {
        Optional<StoredWork> workOpt = repository.findById(id);
        StoredWork work = workOpt.orElseThrow(() -> new IllegalArgumentException("Work not found: " + id));
        try {
            return Files.readAllBytes(Paths.get(work.getFilePath()));
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read file: " + work.getFilePath(), e);
        }
    }

    private String extractExtension(String filename) {
        if (filename == null) {
            return null;
        }
        int idx = filename.lastIndexOf('.');
        if (idx < 0 || idx == filename.length() - 1) {
            return null;
        }
        return filename.substring(idx + 1);
    }
}
