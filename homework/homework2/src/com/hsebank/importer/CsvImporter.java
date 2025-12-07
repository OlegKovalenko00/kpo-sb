package com.hsebank.importer;

import java.util.*;

public final class CsvImporter extends Importer {

    @Override
    protected List<Map<String, Object>> parse(String raw) {
        List<String> lines = Arrays.asList(raw.split("\\R"));
        if (lines.isEmpty()) return Collections.emptyList();

        String header = lines.get(0);
        String[] cols = header.split(",");
        Map<String, Integer> idx = new HashMap<>();
        for (int i = 0; i < cols.length; i++) {
            idx.put(cols[i].trim(), i);
        }

        List<Map<String, Object>> res = new ArrayList<>();
        for (int li = 1; li < lines.size(); li++) {
            String line = lines.get(li).trim();
            if (line.isEmpty()) continue;
            String[] parts = line.split(",");

            Map<String, Object> m = new LinkedHashMap<>();
            if (idx.containsKey("id")) m.put("id", safeGet(parts, idx.get("id")));
            if (idx.containsKey("type")) m.put("type", safeGet(parts, idx.get("type")));
            if (idx.containsKey("accountId")) m.put("accountId", safeGet(parts, idx.get("accountId")));
            if (idx.containsKey("bankAccountId")) m.put("bankAccountId", safeGet(parts, idx.get("bankAccountId")));
            if (idx.containsKey("categoryId")) m.put("categoryId", safeGet(parts, idx.get("categoryId")));
            if (idx.containsKey("amount")) m.put("amount", safeGet(parts, idx.get("amount")));
            if (idx.containsKey("date")) m.put("date", safeGet(parts, idx.get("date")));
            if (idx.containsKey("description")) m.put("description", safeGet(parts, idx.get("description")));

            if (!m.isEmpty()) res.add(m);
        }

        System.out.println("Imported CSV maps: " + res);
        return res;
    }

    private static String safeGet(String[] arr, Integer i) {
        if (i == null) return null;
        if (i >= arr.length) return "";
        return arr[i].trim();
    }
}
