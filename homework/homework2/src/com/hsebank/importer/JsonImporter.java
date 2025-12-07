package com.hsebank.importer;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class JsonImporter extends Importer {

    @Override
    protected List<Map<String, Object>> parse(String raw) {
        return parseArrayOfObjects(raw);
    }

    private static List<Map<String, Object>> parseArrayOfObjects(String text) {
        List<Map<String, Object>> res = new ArrayList<>();
        int i = 0;
        while (i < text.length()) {
            int start = text.indexOf('{', i);
            if (start < 0) break;
            int depth = 0;
            int end = start;
            while (end < text.length()) {
                char c = text.charAt(end);
                if (c == '{') depth++;
                else if (c == '}') {
                    depth--;
                    if (depth == 0) {
                        end++;
                        break;
                    }
                }
                end++;
            }
            if (depth == 0) {
                String obj = text.substring(start, end);
                res.add(parseObject(obj));
                i = end;
            } else {
                break;
            }
        }
        return res;
    }

    private static Map<String, Object> parseObject(String o) {
        Map<String, Object> map = new HashMap<>();
        Pattern p = Pattern.compile("\"([^\"]+)\"\\s*:\\s*(\"([^\"]*)\"|[^,}\\n]+)");
        Matcher m = p.matcher(o);
        while (m.find()) {
            String key = m.group(1);
            String val;
            if (m.group(3) != null) val = m.group(3);
            else val = m.group(2).trim();
            map.put(key, val);
        }
        return map;
    }
}
