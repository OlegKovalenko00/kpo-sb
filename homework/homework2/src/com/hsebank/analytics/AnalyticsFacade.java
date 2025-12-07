package com.hsebank.analytics;

import com.hsebank.domain.Operation;
import com.hsebank.repository.OperationRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public final class AnalyticsFacade {
    private final OperationRepository repo;

    public AnalyticsFacade(OperationRepository repo) {
        this.repo = repo;
    }

    public BigDecimal netForPeriod(LocalDate from, LocalDate to) {
        return repo.findAll().stream()
                .filter(o -> !o.getDate().isBefore(from) && !o.getDate().isAfter(to))
                .map(o -> o.getType() == Operation.Type.INCOME ? o.getAmount() : o.getAmount().negate())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Map<String, BigDecimal> groupByCategory(LocalDate from, LocalDate to) {
        return repo.findAll().stream()
                .filter(o -> !o.getDate().isBefore(from) && !o.getDate().isAfter(to))
                .collect(Collectors.groupingBy(Operation::getCategoryId, Collectors.reducing(BigDecimal.ZERO,
                        o -> o.getType() == Operation.Type.INCOME ? o.getAmount() : o.getAmount().negate(),
                        BigDecimal::add)));
    }
}