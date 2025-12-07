package com.hsebank.metrics;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MetricsCollector {

    private final Map<String, Long> totalTimeByCommand = new HashMap<>();
    private final Map<String, Integer> countByCommand = new HashMap<>();

    public void record(String name, long nanos) {
        totalTimeByCommand.merge(name, nanos, Long::sum);
        countByCommand.merge(name, 1, Integer::sum);
    }

    public Map<String, Double> averageMillisByCommand() {
        Map<String, Double> result = new HashMap<>();
        for (String key : totalTimeByCommand.keySet()) {
            long totalNanos = totalTimeByCommand.get(key);
            int count = countByCommand.getOrDefault(key, 1);
            double avgMillis = (totalNanos / 1_000_000.0) / count;
            result.put(key, avgMillis);
        }
        return Collections.unmodifiableMap(result);
    }
}
