package com.hsebank.importer;

import java.util.*;

public final class YamlImporter extends Importer {

    @Override
    protected List<Map<String, Object>> parse(String raw) {
        return parseListOfMaps(raw);
    }

    private static List<Map<String, Object>> parseListOfMaps(String text) {
        List<Map<String, Object>> res = new ArrayList<>();
        Map<String, Object> current = null;

        for (String line : text.split("\\R")) {
            if (line.trim().isEmpty()) continue;

            if (line.startsWith("-")) {
                if (current != null) {
                    res.add(current);
                }
                current = new LinkedHashMap<>();
                String rest = line.substring(1).trim();
                if (!rest.isEmpty()) {
                    int idx = rest.indexOf(':');
                    if (idx > 0) {
                        String k = rest.substring(0, idx).trim();
                        String v = rest.substring(idx + 1).trim();
                        if (v.startsWith("\"") && v.endsWith("\"") && v.length() >= 2) {
                            v = v.substring(1, v.length() - 1);
                        }
                        current.put(k, v);
                    }
                }
            } else {
                if (current == null) continue;
                String trimmed = line.trim();
                int idx = trimmed.indexOf(':');
                if (idx <= 0) continue;
                String k = trimmed.substring(0, idx).trim();
                String v = trimmed.substring(idx + 1).trim();
                if (v.startsWith("\"") && v.endsWith("\"") && v.length() >= 2) {
                    v = v.substring(1, v.length() - 1);
                }
                current.put(k, v);
            }
        }

        if (current != null) res.add(current);
        return res;
    }
}
