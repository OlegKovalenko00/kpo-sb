package com.hsebank.service;

import com.hsebank.domain.BankAccount;
import com.hsebank.domain.Operation;
import com.hsebank.factory.DomainFactory;
import com.hsebank.repository.BankAccountRepository;
import com.hsebank.repository.OperationRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public final class OperationService {
    private final OperationRepository repo;
    private final BankAccountRepository bankRepo;
    private final DomainFactory factory;

    public OperationService(OperationRepository repo,
                            BankAccountRepository bankRepo,
                            DomainFactory factory) {
        this.repo = repo;
        this.bankRepo = bankRepo;
        this.factory = factory;
    }

    public Operation create(Operation.Type type,
                            String bankAccountId,
                            String categoryId,
                            BigDecimal amount,
                            LocalDate date,
                            String description) {
        Operation op = factory.createOperation(type, bankAccountId, categoryId, amount, date, description);
        applyToAccountBalance(op);
        return repo.save(op);
    }

    public Operation addOperation(Operation.Type type,
                                  String bankAccountId,
                                  String categoryId,
                                  BigDecimal amount,
                                  LocalDate date,
                                  String description) {
        return create(type, bankAccountId, categoryId, amount, date, description);
    }

    public void updateOperation(String id,
                                Operation.Type type,
                                String bankAccountId,
                                String categoryId,
                                BigDecimal amount,
                                LocalDate date,
                                String description) {
        Optional<Operation> existingOpt = repo.findById(id);
        if (existingOpt.isEmpty()) {
            throw new IllegalArgumentException("Operation not found: " + id);
        }
        Operation existing = existingOpt.get();

        rollbackFromAccountBalance(existing);

        Operation updated = factory.createOperation(id, type, bankAccountId, categoryId, amount, date, description);
        applyToAccountBalance(updated);
        repo.save(updated);
    }

    public Optional<Operation> find(String id) { return repo.findById(id); }

    public Collection<Operation> list() { return repo.findAll(); }

    public List<Operation> listByAccount(String bankAccountId) {
        return repo.findAll().stream()
                .filter(o -> bankAccountId.equals(o.getBankAccountId()))
                .collect(Collectors.toList());
    }

    public void delete(String id) {
        repo.findById(id).ifPresent(op -> {
            rollbackFromAccountBalance(op);
            repo.delete(id);
        });
    }

    public void recalculateAllBalancesFromHistory() {
        for (BankAccount acc : bankRepo.findAll()) {
            BigDecimal cur = acc.getBalance();
            if (cur.compareTo(BigDecimal.ZERO) != 0) {
                acc.applyDelta(cur.negate());
                bankRepo.save(acc);
            }
        }
        repo.findAll().stream()
                .sorted((o1, o2) -> o1.getDate().compareTo(o2.getDate()))
                .forEach(this::applyToAccountBalance);
    }

    private void applyToAccountBalance(Operation op) {
        BigDecimal delta = op.getType() == Operation.Type.INCOME
                ? op.getAmount()
                : op.getAmount().negate();

        bankRepo.findById(op.getBankAccountId()).ifPresent(acc -> {
            acc.applyDelta(delta);
            bankRepo.save(acc);
        });
    }

    private void rollbackFromAccountBalance(Operation op) {
        BigDecimal delta = op.getType() == Operation.Type.INCOME
                ? op.getAmount().negate()
                : op.getAmount();

        bankRepo.findById(op.getBankAccountId()).ifPresent(acc -> {
            acc.applyDelta(delta);
            bankRepo.save(acc);
        });
    }
}
