package ru.hse.antiplag.gateway.web;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public class MultipartInputResource extends ByteArrayResource {

    private final String filename;

    public MultipartInputResource(MultipartFile file) {
        super(toBytes(file));
        this.filename = file.getOriginalFilename();
    }

    @Override
    public String getFilename() {
        return filename;
    }

    private static byte[] toBytes(MultipartFile file) {
        try {
            return file.getBytes();
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read multipart file", e);
        }
    }
}
