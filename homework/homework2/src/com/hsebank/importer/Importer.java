package com.hsebank.importer;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public abstract class Importer {

    public final List<Map<String, Object>> importAsListOfMaps(Path path) throws Exception {
        String raw = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
        return parse(raw);
    }

    protected abstract List<Map<String, Object>> parse(String raw) throws Exception;
}
